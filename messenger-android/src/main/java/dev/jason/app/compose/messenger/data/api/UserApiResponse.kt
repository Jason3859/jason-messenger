package dev.jason.app.compose.messenger.data.api

import kotlinx.serialization.Serializable

@Serializable
data class UserApiResponse(
    val username: String,
    val password: String
)