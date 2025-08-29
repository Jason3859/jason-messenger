package dev.jason.project.ktor.messenger.domain

interface DatabaseRepository {
    suspend fun addMessage(message: Message)
    suspend fun getAllMessages(): List<Message>
    suspend fun deleteChatRoom(chatroomID: String): Result
}