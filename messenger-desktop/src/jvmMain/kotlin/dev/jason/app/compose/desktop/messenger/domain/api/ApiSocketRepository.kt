package dev.jason.app.compose.desktop.messenger.domain.api

import dev.jason.app.compose.desktop.messenger.domain.model.Message
import dev.jason.app.compose.desktop.messenger.domain.model.Result
import dev.jason.app.compose.desktop.messenger.domain.model.User
import kotlinx.coroutines.flow.SharedFlow

interface ApiSocketRepository {

    fun getMessages(): SharedFlow<Message>
    suspend fun connect(user: User, roomId: String): Result
    suspend fun sendMessage(message: String)
    suspend fun closeSession()
}