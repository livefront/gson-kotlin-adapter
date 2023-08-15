package com.livefront.gsonkotlinadapter.sample.model

data class User(
    val doesNotExist: String,
    val email: String,
    val location: UserLocation,
    val name: UserFullName,
)

data class UserFullName(
    val firstName: String,
    val lastName: String,
    val title: String,
)

data class UserLocation(
    val city: String,
    val postalCode: String,
    val state: String,
)
