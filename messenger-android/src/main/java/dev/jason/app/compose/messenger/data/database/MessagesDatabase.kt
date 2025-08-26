package dev.jason.app.compose.messenger.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MessageEntity::class], version = 1, exportSchema = true)
abstract class MessagesDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessagesDao
}