package com.livefront.gsonkotlinadapter.util

import com.google.gson.annotations.SerializedName
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KParameter

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
