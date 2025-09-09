package dev.jason.app.compose.messenger.ui.model

data class MessageUi(
    val id: Long,
    val chatRoomId: String,
    val sender: String,
    val text: String,
    val timestamp: String
)