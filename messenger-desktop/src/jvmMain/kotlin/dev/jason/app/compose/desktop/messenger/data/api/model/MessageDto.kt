package dev.jason.app.compose.desktop.messenger.data.api.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: Long,
    val chatRoomId: String,
    val sender: String,
    val message: String,
    val timestamp: Long
)