package dev.jason

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/chat") {
            val currentUserId = call.parameters["userId"] ?: return@webSocket
            val otherUserId = call.parameters["otherUserId"] ?: return@webSocket
            val chatId = getChatId(currentUserId, otherUserId)
            val connection = Connection(this, currentUserId)
            val chatMembers = chatRooms.getOrPut(chatId) { mutableSetOf() }
            chatMembers += connection

            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        val message = "$currentUserId: $text"

                        chatRooms[chatId]?.forEach {
                            it.session.send(message)
                        }
                    }
                }
            } finally {
                chatRooms[chatId]?.remove(connection)
            }
        }
    }
}
