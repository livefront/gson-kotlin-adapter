package com.livefront.gsonkotlinadapter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.JsonAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.livefront.gsonkotlinadapter.util.defaultValue
import com.livefront.gsonkotlinadapter.util.getSerializedNames
import com.livefront.gsonkotlinadapter.util.isPrimitive
import com.livefront.gsonkotlinadapter.util.resolveParameterType
import com.livefront.gsonkotlinadapter.util.toKClass
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaConstructor

/**
 * This [TypeAdapterFactory] constructs Kotlin classes using their default constructor, allowing
 * for properties to be initialized properly. This ensures that the JSON fulfills the nullability
 * contract of the model and calls the class's init block.
 */
class KotlinReflectiveTypeAdapterFactory private constructor(
    private val enableDefaultPrimitiveValues: Boolean
) : TypeAdapterFactory {
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
        require(!kotlinRawType.isInner) { "Cannot serialize inner class ${rawType.name}" }
        kotlinRawType.primaryConstructor ?: return null
        return Adapter(this, gson, type, kotlinRawType, enableDefaultPrimitiveValues)
    }

    internal class Adapter<T : Any>(
        factory: TypeAdapterFactory,
        gson: Gson,
        type: TypeToken<T>,
        private val kClass: KClass<T>,
        private val enableDefaultPrimitiveValues: Boolean
    ) : TypeAdapter<T>() {
        private val primaryConstructor: KFunction<T> = kClass
            .primaryConstructor!!
            .apply { isAccessible = true }

        private val declaringClass: Class<T> = primaryConstructor.javaConstructor?.declaringClass!!

        /**
         * Provides a mapping between parameter and automatic type-specific defaults (i.e. null for
         * objects, 0 for numeric primitives, '\u0000' for chars, false for booleans). If the
         * parameter [KParameter.isOptional] then it will not exist in this map and the
         * manually-supplied default value will be used when the constructor is called. The
         * contents of this map may vary based on the [enableDefaultPrimitiveValues] property.
         */
        private val constructorParameterDefaultsMap: Map<KParameter, Any?> = primaryConstructor
            .parameters
            .filterNot(KParameter::isOptional)
            .associateWith { if (enableDefaultPrimitiveValues) it.defaultValue else null }

        private val constructorParameterNameMap: Map<KParameter, List<String>> = primaryConstructor
            .parameters
            .map { it to it.getSerializedNames(declaringClass) }
            .toMap()

        private val invalidReadParameters: Set<KParameter> = constructorParameterNameMap
            .filter { (parameter, names) ->
                if (enableDefaultPrimitiveValues && parameter.isPrimitive) {
                    false
                } else {
                    names.isEmpty() && !parameter.isOptional
                }
            }
            .keys

        private val constructorMap: Map<String, KParameter> = constructorParameterNameMap
            .entries
            .flatMap { (parameter, names) -> names.map { it to parameter } }
            .toMap()

        private val delegateAdapter: TypeAdapter<T> = gson.getDelegateAdapter(factory, type)
        private val innerAdapters: Map<KParameter, TypeAdapter<*>> = constructorParameterNameMap
            .filterNot { (_, names) -> names.isEmpty() }
            .keys
            .associateWith { gson.getAdapter(type.resolveParameterType(it)) }

        override fun write(writer: JsonWriter, value: T?) {
            if (value == null) {
                writer.nullValue()
                return
            }
            delegateAdapter.write(writer, value)
        }

        override fun read(reader: JsonReader): T? {
            require(!kClass.isAbstract) { "Cannot deserialize abstract class '${kClass.simpleName}'" }
            require(!kClass.isSealed) { "Cannot deserialize sealed class '${kClass.simpleName}'" }
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

            // Add all stored default values if the JSON did not include it
            constructorParameterDefaultsMap.map { (parameter, data) ->
                constructorParams.putIfAbsent(parameter, data)
            }
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
         * class's init block. Setting the [enableDefaultPrimitiveValues] to `true` will allow
         * nonnull primitive values to use default values when manually-supplied default values are
         * not present and a value is not present in the JSON.
         */
        fun create(
            enableDefaultPrimitiveValues: Boolean = false
        ): KotlinReflectiveTypeAdapterFactory = KotlinReflectiveTypeAdapterFactory(
            enableDefaultPrimitiveValues
        )
    }
}
