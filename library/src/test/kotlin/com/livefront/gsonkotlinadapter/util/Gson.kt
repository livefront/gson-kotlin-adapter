package com.livefront.gsonkotlinadapter.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * A helper method that deserializes the specified JSON into an object based on the reified type.
 */
internal inline fun <reified T> Gson.fromJson(
    jsonString: String
): T = fromJson(jsonString, object : TypeToken<T>() {}.type)
