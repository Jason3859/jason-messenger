package dev.jason.app.compose.messenger.data.database

import dev.jason.app.compose.messenger.data.database.mappers.toDomain
import dev.jason.app.compose.messenger.data.database.mappers.toEntity
import dev.jason.app.compose.messenger.domain.database.DatabaseRepository
import dev.jason.app.compose.messenger.domain.database.Message

class DbRepoImpl(private val dao: MessagesDao) : DatabaseRepository {
    override suspend fun addMessage(message: Message) {
        dao.addMessage(message.toEntity())
    }

    override suspend fun getAllMessages(): List<Message> {
        return dao.getAllMessages().map { it.toDomain() }
    }
}