package dev.jason.app.compose.messenger.domain.database

interface DatabaseRepository {
    suspend fun addMessage(message: Message)
    suspend fun getAllMessages(): List<Message>
}