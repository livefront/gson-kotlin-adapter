package com.livefront.gsonkotlinadapter.sample.util

import android.view.View
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Returns flow of [Unit] that emits whenever this [View] is clicked.
 */
fun View.clicks(): Flow<Unit> = callbackFlow {
    setOnClickListener { launch { send(Unit) } }
    awaitClose { setOnClickListener(null) }
}
    .buffer(capacity = Channel.RENDEZVOUS)

/**
 * Emits a [Channel.RENDEZVOUS] flow of [T] mapped clicks from this [View].
 */
fun <T : Any> View.mapClicks(mapper: suspend (Unit) -> T): Flow<T> = clicks().map(mapper)
