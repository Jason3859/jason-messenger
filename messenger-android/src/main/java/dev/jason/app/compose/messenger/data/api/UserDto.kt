package dev.jason.app.compose.messenger.data.api

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val username: String,
    val password: String,
)