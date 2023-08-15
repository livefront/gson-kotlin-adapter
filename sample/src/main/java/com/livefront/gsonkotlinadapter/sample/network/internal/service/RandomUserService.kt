package com.livefront.gsonkotlinadapter.sample.network.internal.service

import com.livefront.gsonkotlinadapter.sample.network.internal.model.NetworkResult
import com.livefront.gsonkotlinadapter.sample.network.model.UserResponses
import retrofit2.http.GET

/**
 * Retrofit server for the random user APIs.
 */
interface RandomUserService {
    @GET("/api/")
    suspend fun getRandomUser(): NetworkResult<UserResponses>
}
