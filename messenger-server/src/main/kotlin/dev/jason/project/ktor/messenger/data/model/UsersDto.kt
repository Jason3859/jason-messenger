package dev.jason.project.ktor.messenger.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UsersDto(
    val username: String,
    val password: String,
)
