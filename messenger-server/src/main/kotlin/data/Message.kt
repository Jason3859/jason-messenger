package dev.jason.data

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long,
    val chatId: String,
    val sender: String,
    val message: String
)