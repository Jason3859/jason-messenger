package dev.jason.project.ktor.messenger.data

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: Long,
    val chatRoomId: String,
    val sender: String,
    val message: String,
    val timestamp: Long
)