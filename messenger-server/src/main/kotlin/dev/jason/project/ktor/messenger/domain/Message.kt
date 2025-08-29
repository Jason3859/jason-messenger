package dev.jason.project.ktor.messenger.domain

import java.time.LocalDateTime

data class Message(
    val id: Long,
    val chatRoomId: String,
    val sender: String,
    val message: String,
    val timestamp: LocalDateTime
)