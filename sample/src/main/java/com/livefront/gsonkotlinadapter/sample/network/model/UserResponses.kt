package com.livefront.gsonkotlinadapter.sample.network.model

import com.google.gson.annotations.SerializedName
import com.livefront.gsonkotlinadapter.sample.model.User
import com.livefront.gsonkotlinadapter.sample.model.UserFullName
import com.livefront.gsonkotlinadapter.sample.model.UserLocation

data class UserResponses(
    @SerializedName("results") val results: List<UserResponse>,
)

data class UserResponse(
    @SerializedName("this_does_not_exist") val doesNotExist: Int = 1_551_123,
    @SerializedName("email") val email: String,
    @SerializedName("location") val location: LocationResponse,
    @SerializedName("name") val name: NameResponse,
)

data class NameResponse(
    @SerializedName("first") val firstName: String,
    @SerializedName("last") val lastName: String,
    @SerializedName("title") val title: String,
)

data class LocationResponse(
    @SerializedName("city") val city: String,
    @SerializedName("postcode") val postalCode: String,
    @SerializedName("state") val state: String,
)

fun UserResponse.toDomain(): User = User(
    doesNotExist = doesNotExist.toString(),
    email = email,
    location = location.toDomain(),
    name = name.toDomain(),
)

fun NameResponse.toDomain(): UserFullName = UserFullName(
    firstName = firstName,
    lastName = lastName,
    title = title,
)

fun LocationResponse.toDomain(): UserLocation = UserLocation(
    city = city,
    postalCode = postalCode,
    state = state,
)
