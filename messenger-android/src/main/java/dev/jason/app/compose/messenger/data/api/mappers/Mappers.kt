package dev.jason.app.compose.messenger.data.api.mappers

import dev.jason.app.compose.messenger.data.api.model.MessageDto
import dev.jason.app.compose.messenger.data.api.model.UserDto
import dev.jason.app.compose.messenger.domain.model.Message
import dev.jason.app.compose.messenger.domain.model.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun User.toDto() = UserDto(
    username, password
)

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