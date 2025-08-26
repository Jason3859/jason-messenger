package dev.jason.app.compose.messenger.domain.api

interface ApiRepository {
    suspend fun signin(user: User): Boolean
    suspend fun login(user: User): Boolean
    suspend fun connect(user: User, chatroomID: String): Boolean
    suspend fun sendMessage(message: String)
}