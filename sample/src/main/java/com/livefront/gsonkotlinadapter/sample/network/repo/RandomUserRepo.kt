package com.livefront.gsonkotlinadapter.sample.network.repo

import com.livefront.gsonkotlinadapter.sample.model.User
import com.livefront.gsonkotlinadapter.sample.network.internal.model.NetworkResult
import com.livefront.gsonkotlinadapter.sample.network.internal.model.map
import com.livefront.gsonkotlinadapter.sample.network.internal.service.RandomUserService
import com.livefront.gsonkotlinadapter.sample.network.model.toDomain
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository to transform the network responses into app consumable models.
 */
@Singleton
class RandomUserRepo @Inject constructor(
    private val randomUserService: RandomUserService,
) {
    suspend fun getRandomUsers(): NetworkResult<List<User>> = randomUserService
        .getRandomUser()
        .map { users -> users.results.map { it.toDomain() } }
}
