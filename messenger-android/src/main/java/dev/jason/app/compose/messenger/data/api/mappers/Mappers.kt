package dev.jason.app.compose.messenger.data.api.mappers

import dev.jason.app.compose.messenger.data.api.MessageDto
import dev.jason.app.compose.messenger.data.api.UserApiResponse
import dev.jason.app.compose.messenger.data.api.UserDto
import dev.jason.app.compose.messenger.data.database.mappers.toEntity
import dev.jason.app.compose.messenger.data.database.mappers.toLocalDateTime
import dev.jason.app.compose.messenger.data.database.mappers.toLong
import dev.jason.app.compose.messenger.domain.api.User
import dev.jason.app.compose.messenger.domain.database.Message

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