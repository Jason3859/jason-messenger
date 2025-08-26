package dev.jason.app.compose.messenger.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chatRoomId: String,
    val sender: String,
    val message: String,
    val timestamp: Long
)
