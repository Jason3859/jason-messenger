package dev.jason.app.compose.messenger.domain.database

import java.time.LocalDateTime

data class Message(
    val id: Long,
    val chatRoomId: String,
    val sender: String,
    val message: String,
    val timestamp: LocalDateTime
)
