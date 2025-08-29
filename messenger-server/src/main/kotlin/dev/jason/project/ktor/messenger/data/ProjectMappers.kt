package dev.jason.project.ktor.messenger.data

import dev.jason.project.ktor.messenger.domain.Message
import dev.jason.project.ktor.messenger.domain.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Message.toDto(): MessageDto = MessageDto(
    id = id,
    chatRoomId = chatRoomId,
    sender = sender,
    message = message,
    timestamp = timestamp.toLong()
)

fun MessageDto.toDomain(): Message = Message(
    id = id,
    chatRoomId = chatRoomId,
    sender = sender,
    message = message,
    timestamp = timestamp.toLocalDateTime()
)

fun UsersDto.toDomain(): User = User(
    username = username,
    password = password
)

fun LocalDateTime.toLong(): Long {
    return this.atZone(ZoneId.systemDefault()).toEpochSecond()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
}