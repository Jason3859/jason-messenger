package dev.jason.app.compose.messenger.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDao {
    @Insert
    suspend fun addMessage(message: MessageEntity)

    @Query("SELECT * FROM messages")
    fun getAllMessages(): Flow<List<MessageEntity>>
}