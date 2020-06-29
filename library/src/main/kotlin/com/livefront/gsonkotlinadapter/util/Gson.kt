package com.livefront.gsonkotlinadapter.util

import com.google.gson.Gson

/**
 * Helper method that warms the cache for all [classes] passed in.
 *
 * This can be used to proactively build the adapters associated with the `classes`.
 */
fun Gson.warmClassCaches(vararg classes: Class<*>) {
    classes.forEach { getAdapter(it) }
}
