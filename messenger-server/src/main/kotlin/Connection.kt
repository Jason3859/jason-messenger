package dev.jason

import io.ktor.server.websocket.DefaultWebSocketServerSession

data class Connection(
    val session: DefaultWebSocketServerSession,
    val name: String,
)