package dev.jason.app.compose.messenger.data.database.mappers

import dev.jason.app.compose.messenger.data.database.MessageEntity
import dev.jason.app.compose.messenger.domain.database.Message
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun MessageEntity.toDomain() = Message(
    id = id,
    chatRoomId = chatRoomId,
    sender = sender,
    message = message,
    timestamp = timestamp.toLocalDateTime()
)

fun Message.toEntity() = MessageEntity(
    id = id,
    chatRoomId = chatRoomId,
    sender = sender,
    message = message,
    timestamp = timestamp.toLong()
)

fun LocalDateTime.toLong(): Long {
    return this.atZone(ZoneId.systemDefault()).toEpochSecond()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
}