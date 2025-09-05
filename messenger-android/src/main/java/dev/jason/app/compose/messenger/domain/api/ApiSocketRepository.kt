package dev.jason.app.compose.messenger.domain.api

import dev.jason.app.compose.messenger.domain.database.Message
import kotlinx.coroutines.flow.SharedFlow

interface ApiSocketRepository {

    fun getMessages(): SharedFlow<Message>
    suspend fun connect(user: User, chatroomId: String): Result
    suspend fun sendMessage(message: String)
    suspend fun closeSession()
}