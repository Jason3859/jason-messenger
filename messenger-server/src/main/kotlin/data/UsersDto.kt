package dev.jason.data

import kotlinx.serialization.Serializable

@Serializable
data class UsersDto(
    val username: String,
    val password: String,
)
