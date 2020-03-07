package com.livefront.gsonkotlinadapter.util

import com.google.gson.reflect.TypeToken
import kotlin.reflect.KClass

/**
 * Helper method for converting a [TypeToken] to a corresponding Kotlin class.
 *
 * Note that if the `TypeToken` represents a generic type, the returned [KClass] is for the
 * raw type.
 */
internal fun <T : Any> TypeToken<T>.toKClass(): KClass<T> =
    @Suppress("UNCHECKED_CAST")
    (rawType as Class<T>).kotlin
