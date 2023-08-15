package com.livefront.gsonkotlinadapter.sample.network.internal.adapter

import com.livefront.gsonkotlinadapter.sample.network.internal.model.NetworkResult
import com.livefront.gsonkotlinadapter.sample.network.internal.model.asFailure
import com.livefront.gsonkotlinadapter.sample.network.internal.model.asSuccess
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A [CallAdapter.Factory] for wrapping network requests into [NetworkResult].
 */
class NetworkResultCallAdapterFactory private constructor() : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        check(returnType is ParameterizedType) { "$returnType must be parameterized" }
        val containerType = getParameterUpperBound(0, returnType)

        if (getRawType(containerType) != NetworkResult::class.java) return null
        check(containerType is ParameterizedType) { "$containerType must be parameterized" }

        val requestType = getParameterUpperBound(0, containerType)

        return if (getRawType(returnType) == Call::class.java) {
            NetworkResultCallAdapter<Any>(successType = requestType)
        } else {
            null
        }
    }

    companion object {
        fun create(): CallAdapter.Factory = NetworkResultCallAdapterFactory()
    }
}

/**
 * A [CallAdapter] for [NetworkResult]s.
 */
private class NetworkResultCallAdapter<T>(
    private val successType: Type,
) : CallAdapter<T, Call<NetworkResult<T>>> {
    override fun responseType(): Type = successType

    override fun adapt(call: Call<T>): Call<NetworkResult<T>> = NetworkResultCall(call, successType)
}

/**
 * A [Call] for wrapping a network request into a [NetworkResult].
 */
private class NetworkResultCall<T>(
    private val backingCall: Call<T>,
    private val successType: Type,
) : Call<NetworkResult<T>> {
    override fun cancel(): Unit = backingCall.cancel()

    override fun clone(): Call<NetworkResult<T>> = NetworkResultCall(backingCall, successType)

    override fun enqueue(callback: Callback<NetworkResult<T>>): Unit = backingCall.enqueue(
        object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                callback.onResponse(
                    this@NetworkResultCall,
                    when {
                        !response.isSuccessful -> Response.success(HttpException(response).asFailure())
                        body != null -> Response.success(body.asSuccess(), response.raw())
                        successType == Unit::class.java -> {
                            @Suppress("UNCHECKED_CAST")
                            Response.success((Unit as T).asSuccess(), response.raw())
                        }

                        else -> Response.success(IllegalStateException("Unexpected null body!").asFailure())
                    },
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(this@NetworkResultCall, Response.success(t.asFailure()))
            }
        },
    )

    override fun execute(): Response<NetworkResult<T>> = throw UnsupportedOperationException(
        "This call can't be executed synchronously",
    )

    override fun isCanceled(): Boolean = backingCall.isCanceled

    override fun isExecuted(): Boolean = backingCall.isExecuted

    override fun request(): Request = backingCall.request()

    override fun timeout(): Timeout = backingCall.timeout()
}
