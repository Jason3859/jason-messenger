package dev.jason.app.compose.messenger_app.domain

interface AuthRepository {

    suspend fun login(user: User): AuthResult
    suspend fun signin(user: User): AuthResult

    fun getToken(): String
}