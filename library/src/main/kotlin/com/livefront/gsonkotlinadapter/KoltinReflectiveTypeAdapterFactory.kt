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

        val primaryConstructor: KFunction<T> = kotlinRawType
            .primaryConstructor
            ?.apply { isAccessible = true }
            ?: return null
        val declaringClass: Class<T> = primaryConstructor.javaConstructor!!.declaringClass!!
        val innerAdapters: MutableMap<KParameter, TypeAdapter<*>> = mutableMapOf()
        val constructorParameterDefaultsMap: MutableMap<KParameter, Any?> = mutableMapOf()
        val invalidReadParameters: MutableSet<KParameter> = mutableSetOf()
        val constructorMap: MutableMap<String, KParameter> = mutableMapOf()

        primaryConstructor.parameters.forEach { parameter: KParameter ->
            val names: List<String> = parameter.getSerializedNames(declaringClass)
            if (names.isNotEmpty()) {
                // Retrieve adapters for serializable inner properties
                innerAdapters[parameter] = gson.getAdapter(type.resolveParameterType(parameter))
            }
            if (!parameter.isOptional) {
                // Create a map containing all default parameters where applicable
                // This can include default values for primitives if it is enabled
                constructorParameterDefaultsMap[parameter] = if (enableDefaultPrimitiveValues) {
                    parameter.defaultValue
                } else {
                    null
                }
                if (names.isEmpty() && (!parameter.isPrimitive || !enableDefaultPrimitiveValues)) {
                    // Maintain a list of parameters that will cause the object construction
                    // to fail during deserialization. We do not fail immediately because
                    // serialization is still possible
                    invalidReadParameters += parameter
                }
            }
            // Associate the parameter with the possible names
            names.forEach { name: String -> constructorMap[name] = parameter }
        }

        return Adapter(
            delegateAdapter = gson.getDelegateAdapter(this, type),
            innerAdapters = innerAdapters,
            kClass = kotlinRawType,
            primaryConstructor = primaryConstructor,
            constructorParameterDefaultsMap = constructorParameterDefaultsMap,
            invalidReadParameters = invalidReadParameters,
            constructorMap = constructorMap
        )
    }

    internal class Adapter<T : Any>(
        private val delegateAdapter: TypeAdapter<T>,
        private val innerAdapters: Map<KParameter, TypeAdapter<*>>,
        private val kClass: KClass<T>,
        private val primaryConstructor: KFunction<T>,
        private val constructorParameterDefaultsMap: Map<KParameter, Any?>,
        private val invalidReadParameters: Set<KParameter>,
        private val constructorMap: Map<String, KParameter>
    ) : TypeAdapter<T>() {
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
                            "${kClass.simpleName} declares multiple JSON fields named ${parameter.name}"
                        }
                    }
                    ?: reader.skipValue()
            }
            reader.endObject()

            // Add all stored default values if the JSON did not include it
            constructorParameterDefaultsMap.forEach { (parameter, data) ->
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
