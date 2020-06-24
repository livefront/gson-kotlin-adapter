package com.livefront.gsonkotlinadapter.util

import com.google.gson.annotations.SerializedName
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

/**
 * Returns the default value of this [KParameter] or `null`.
 *
 * Note that this refers to a constant type-specific default value (i.e. null for objects, 0 for
 * numeric primitives, etc.) as opposed to manually-supplied defaults found in a function /
 * constructor parameter list.
 */
internal val KParameter.defaultValue: Any?
    get() {
        if (type.isMarkedNullable) return null
        return when (type.classifier as? KClass<*>) {
            Boolean::class -> false
            Byte::class -> 0.toByte()
            Char::class -> 0.toChar()
            Double::class -> 0.0
            Float::class -> 0F
            Integer::class -> 0
            Long::class -> 0L
            Short::class -> 0.toShort()
            else -> null
        }
    }

/**
 * Returns the `true` if this [KParameter] is a primitive, `false` otherwise.
 */
internal val KParameter.isPrimitive: Boolean
    get() = when (type.classifier as? KClass<*>) {
        Boolean::class,
        Byte::class,
        Char::class,
        Double::class,
        Float::class,
        Integer::class,
        Long::class,
        Short::class -> true
        else -> false
    }

/**
 * Retrieves all possible names for the [KParameter] based on the name of the property, the
 * [SerializedName] annotation, and whether it's [Transient] or not.
 */
internal fun <T : Any> KParameter.getSerializedNames(declaringClass: Class<T>): List<String> {
    val parameterName: String = name ?: return emptyList()
    val declaredField: Field? = try {
        declaringClass.getDeclaredField(parameterName)
    } catch (e: NoSuchFieldException) {
        null
    }
    val serializedName: SerializedName? = declaredField?.getAnnotation(SerializedName::class.java)
    return when {
        declaredField == null || Modifier.isTransient(declaredField.modifiers) -> emptyList()
        serializedName != null -> listOf(serializedName.value) + serializedName.alternate
        else -> listOf(parameterName)
    }
}
