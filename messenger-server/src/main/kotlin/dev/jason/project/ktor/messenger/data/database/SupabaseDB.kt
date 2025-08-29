package dev.jason.project.ktor.messenger.data.database

import dev.jason.project.ktor.messenger.data.MessageDto
import dev.jason.project.ktor.messenger.data.toDomain
import dev.jason.project.ktor.messenger.data.toLong
import dev.jason.project.ktor.messenger.domain.DatabaseRepository
import dev.jason.project.ktor.messenger.domain.Message
import dev.jason.project.ktor.messenger.domain.Result
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class SupabaseDB : DatabaseRepository {
    override suspend fun getAllMessages(): List<Message> = dbQuery {
        MessagesDao.selectAll().map { row ->
            MessageDto(
                id = row[MessagesDao.id],
                chatRoomId = row[MessagesDao.chatRoomID],
                sender = row[MessagesDao.sender],
                message = row[MessagesDao.text],
                timestamp = row[MessagesDao.timestamp]
            ).toDomain()
        }
    }

    override suspend fun addMessage(message: Message): Unit = dbQuery {
        MessagesDao.insert {
            it[chatRoomID] = message.chatRoomId
            it[sender] = message.sender
            it[text] = message.message
            it[timestamp] = message.timestamp.toLong()
        }
    }

    override suspend fun deleteChatRoom(chatroomID: String): Result {
        return try {
            val chatroom = getAllMessages().map { it.chatRoomId }
            if (!chatroom.contains(chatroomID)) {
                Result.NotFound()
            }
            MessagesDao.deleteWhere { MessagesDao.chatRoomID eq chatroomID }
            Result.Success()
        } catch (_: Exception) {
            Result.UnableToDelete()
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T {
        return newSuspendedTransaction(Dispatchers.IO) {
            block()
        }
    }
}