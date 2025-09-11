package dev.jason.app.compose.desktop.messenger.domain.api

import dev.jason.app.compose.desktop.messenger.domain.model.Result
import dev.jason.app.compose.desktop.messenger.domain.model.User

interface ApiAuthRepository {
    suspend fun signin(user: User): Result
    suspend fun login(user: User): Result
    suspend fun deleteAccount(user: User): Result
    suspend fun deleteChatroom(chatroomId: String): Result
}