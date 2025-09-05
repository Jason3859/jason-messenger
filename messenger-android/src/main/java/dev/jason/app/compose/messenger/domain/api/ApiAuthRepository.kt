package dev.jason.app.compose.messenger.domain.api

import dev.jason.app.compose.messenger.domain.database.Message

interface ApiAuthRepository {
    suspend fun signin(user: User): Result
    suspend fun login(user: User): Result
    suspend fun connect(user: User, chatroomID: String): Result
    suspend fun sendMessage(message: Message)
    suspend fun listenToMessages()
}