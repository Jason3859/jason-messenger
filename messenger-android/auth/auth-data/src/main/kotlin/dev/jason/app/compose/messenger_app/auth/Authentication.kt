package dev.jason.app.compose.messenger_app.auth

import dev.jason.app.compose.messenger_app.domain.AuthRepository
import dev.jason.app.compose.messenger_app.domain.AuthResult
import dev.jason.app.compose.messenger_app.domain.User

class Authentication(private val repository: AuthRepository) {

    suspend fun login(username: String, password: String): AuthResult {
        val user = User(username, password)
        return repository.login(user)
    }

    suspend fun signin(username: String, password: String): AuthResult {
        val user = User(username, password)
        return repository.signin(user)
    }
}