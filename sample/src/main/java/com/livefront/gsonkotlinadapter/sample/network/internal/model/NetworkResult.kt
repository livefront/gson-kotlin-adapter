package com.livefront.gsonkotlinadapter.sample.network.internal.model

import com.livefront.gsonkotlinadapter.sample.network.internal.model.NetworkResult.Failure
import com.livefront.gsonkotlinadapter.sample.network.internal.model.NetworkResult.Success

/**
 * A wrapper class for a network response of type [T]. If the network request is successful, the typed response will be
 * contained in [Success]. If the network request fails, the relevant exception will be contained in [Failure].
 */
sealed class NetworkResult<out T> {
    /**
     * The network request succeeded with the given [value].
     */
    data class Success<T>(
        val value: T,
    ) : NetworkResult<T>()

    /**
     * The network request failed with the given [throwable].
     */
    data class Failure(
        val throwable: Throwable,
    ) : NetworkResult<Nothing>()
}

/**
 * Maps a successful [NetworkResult] with the given [transform], and leaves failures untouched.
 */
inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> = when (this) {
    is Success -> transform(value).asSuccess()
    is Failure -> this
}

/**
 * Converts the given [Throwable] to a [Failure].
 */
fun Throwable.asFailure(): NetworkResult<Nothing> = Failure(throwable = this)

/**
 * Converts the given response [T] to a [Success].
 */
fun <T> T.asSuccess(): NetworkResult<T> = Success(value = this)
