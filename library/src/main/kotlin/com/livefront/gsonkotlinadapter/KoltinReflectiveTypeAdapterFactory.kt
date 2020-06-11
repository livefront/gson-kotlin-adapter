package com.livefront.gsonkotlinadapter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.JsonAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.livefront.gsonkotlinadapter.util.getSerializedNames
import com.livefront.gsonkotlinadapter.util.resolveParameterType
import com.livefront.gsonkotlinadapter.util.toKClass
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaConstructor

/**
 * This [TypeAdapterFactory] constructs Kotlin classes using their default constructor, allowing
 * for properties to be initialized properly. This ensures that the JSON fulfills the nullability
 * contract of the model and calls the class's init block.
 */
class KotlinReflectiveTypeAdapterFactory private constructor() : TypeAdapterFactory {
    override fun <T : Any> create(
        gson: Gson,
        type: TypeToken<T>
    ): TypeAdapter<T>? {
        val rawType: Class<in T> = type.rawType
        if (rawType.isLocalClass) return null
        if (rawType.isInterface) return null
        if (rawType.isEnum) return null
        if (rawType.isAnnotationPresent(JsonAdapter::class.java)) return null
        if (!rawType.isAnnotationPresent(KOTLIN_METADATA)) return null
        val kotlinRawType: KClass<T> = type.toKClass()
        require(!kotlinRawType.isAbstract) { "Cannot serialize abstract class ${rawType.name}" }
        require(!kotlinRawType.isSealed) { "Cannot serialize sealed class ${rawType.name}" }
        require(!kotlinRawType.isInner) { "Cannot serialize inner class ${rawType.name}" }
        kotlinRawType.primaryConstructor ?: return null
        return Adapter(this, gson, type, kotlinRawType)
    }

    internal class Adapter<T : Any>(
        factory: TypeAdapterFactory,
        gson: Gson,
        type: TypeToken<T>,
        kClass: KClass<T>
    ) : TypeAdapter<T>() {
        private val primaryConstructor: KFunction<T> = kClass.primaryConstructor!!
        private val declaringClass: Class<T> = primaryConstructor.javaConstructor?.declaringClass!!
        private val constructorParameterNameMap: Map<KParameter, List<String>> = primaryConstructor
            .parameters
            .map { it to it.getSerializedNames(declaringClass) }
            .toMap()

        private val invalidReadParameters: List<KParameter> = constructorParameterNameMap
            .entries
            .filter { (parameter, names) -> names.isEmpty() && !parameter.isOptional }
            .map { it.key }

        private val constructorMap: Map<String, KParameter> = constructorParameterNameMap
            .entries
            .flatMap { (parameter, names) -> names.map { it to parameter } }
            .toMap()

        private val delegateAdapter: TypeAdapter<T> = gson.getDelegateAdapter(factory, type)
        private val innerAdapters: Map<KParameter, TypeAdapter<*>> = primaryConstructor
            .parameters
            .associateWith { gson.getAdapter(type.resolveParameterType(it)) }

        override fun write(writer: JsonWriter, value: T?) {
            if (value == null) {
                writer.nullValue()
                return
            }
            delegateAdapter.write(writer, value)
        }

        override fun read(reader: JsonReader): T? {
            require(invalidReadParameters.isEmpty()) {
                val names: String = invalidReadParameters
                    .filter { it.name != null }
                    .joinToString(separator = ", ") { it.name!! }
                "Transient constructor parameters must provide a default value. ($names)"
            }
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return null
            }
            val constructorParams: MutableMap<KParameter, Any?> = mutableMapOf()
            constructorMap.values.forEach { if (!it.isOptional) constructorParams[it] = null }

            reader.beginObject()
            while (reader.hasNext()) {
                constructorMap[reader.nextName()]
                    ?.also { parameter: KParameter ->
                        val replacedValue: Any? = constructorParams.put(
                            parameter,
                            innerAdapters.getValue(parameter).read(reader)
                        )
                        require(replacedValue == null) {
                            "${declaringClass.simpleName} declares multiple JSON fields named ${parameter.name}"
                        }
                    }
                    ?: reader.skipValue()
            }
            reader.endObject()

            return primaryConstructor.callBy(constructorParams)
        }
    }

    companion object {
        /**
         * Classes annotated with this are eligible for this adapter.
         */
        private val KOTLIN_METADATA: Class<Metadata> = Metadata::class.java

        /**
         * Returns an new instance of [KotlinReflectiveTypeAdapterFactory] which constructs classes
         * using the default constructor, allowing for properties to initialized the properly.
         * This ensures that the JSON fulfills the nullability contract of the model and calls the
         * class's init block.
         */
        fun create(): KotlinReflectiveTypeAdapterFactory = KotlinReflectiveTypeAdapterFactory()
    }
}
