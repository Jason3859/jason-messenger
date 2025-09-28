package dev.jason.project.ktor.messenger.domain.db

import dev.jason.project.ktor.messenger.domain.model.Message
import dev.jason.project.ktor.messenger.domain.model.Result

interface MessagesDatabaseRepository {
    suspend fun addMessage(message: Message)
    suspend fun getAllMessages(): List<Message>
    suspend fun deleteChatRoom(chatroomID: String): Result
}