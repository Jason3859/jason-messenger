package dev.jason.app.compose.messenger.domain.api

interface ApiRepository {
    suspend fun signin(user: User): Result
    suspend fun login(user: User): Result
    suspend fun connect(user: User, chatroomID: String): Result
    suspend fun sendMessage(message: String)
}