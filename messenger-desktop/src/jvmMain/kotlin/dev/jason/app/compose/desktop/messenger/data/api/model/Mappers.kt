package dev.jason.app.compose.desktop.messenger.data.api.model

import dev.jason.app.compose.desktop.messenger.domain.model.Message
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun MessageDto.toDomain() = Message(
    id = id,
    chatRoomId = chatRoomId,
    sender = sender,
    message = message,
    timestamp = timestamp.toLocalDateTime()
)

fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
}