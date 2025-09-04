package dev.jason.project.ktor.messenger.data.database

import dev.jason.project.ktor.messenger.data.toLocalDateTime
import dev.jason.project.ktor.messenger.data.toLong
import dev.jason.project.ktor.messenger.domain.DatabaseRepository
import dev.jason.project.ktor.messenger.domain.Message
import dev.jason.project.ktor.messenger.domain.Result
import java.sql.Connection

class MessagesDatabaseRepository(private val connection: Connection) : DatabaseRepository {

    init {
        connection.createStatement().use {
            it.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS messages (
                    id SERIAL PRIMARY KEY,
                    chatroomId TEXT NOT NULL,
                    sender TEXT NOT NULL,
                    text TEXT NOT NULL,
                    timestamp INTEGER NOT NULL
                );
            """.trimIndent()
            )
        }
    }

    override suspend fun addMessage(message: Message) {
        connection.prepareStatement(
            """
                INSERT INTO messages (chatroomId, sender, text, timestamp) VALUES (?, ?, ?, ?);
            """.trimIndent()
        ).use {
            it.apply {
                setString(1, message.chatRoomId)
                setString(2, message.sender)
                setString(3, message.message)
                setLong(4, message.timestamp.toLong())
                executeUpdate()
            }
        }
    }

    override suspend fun getAllMessages(): List<Message> {
        val list = mutableListOf<Message>()
        connection.createStatement().use {
            val rs = it.executeQuery("SELECT * FROM messages")
            while (rs.next()) {
                list.add(
                    Message(
                        id = rs.getLong("id"),
                        chatRoomId = rs.getString("chatroomId"),
                        sender = rs.getString("sender"),
                        message = rs.getString("text"),
                        timestamp = rs.getLong("timestamp").toLocalDateTime()
                    )
                )
            }
        }
        return list
    }

    override suspend fun deleteChatRoom(chatroomID: String): Result {
        connection.prepareStatement("DELETE FROM messages WHERE chatroomId = ?").use {
            it.apply {
                setString(1, chatroomID)
                executeUpdate()
            }
        }
        return Result.Success()
    }
}