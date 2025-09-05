package dev.jason.app.compose.messenger.data.database

import dev.jason.app.compose.messenger.data.database.mappers.toDomain
import dev.jason.app.compose.messenger.data.database.mappers.toEntity
import dev.jason.app.compose.messenger.domain.database.DatabaseRepository
import dev.jason.app.compose.messenger.domain.database.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DbRepoImpl(private val dao: MessagesDao) : DatabaseRepository {
    override suspend fun addMessage(message: Message) {
        dao.addMessage(message.toEntity())
    }

    override fun getAllMessages(): Flow<List<Message>> {
        return dao.getAllMessages().map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }
}