package dev.jason.app.compose.messenger.domain.database

import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun addMessage(message: Message)
    fun getAllMessages(): Flow<List<Message>>
}