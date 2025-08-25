package dev.jason.domain

interface DatabaseRepository {
    suspend fun addMessage(message: Message)
    suspend fun getAllMessages(): List<Message>
}