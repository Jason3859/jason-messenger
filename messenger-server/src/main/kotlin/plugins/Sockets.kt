package dev.jason.plugins

import dev.jason.data.DatabaseRepository
import dev.jason.data.Message
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets(dbRepository: DatabaseRepository) {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val chatSessions = ConcurrentHashMap<String, MutableList<DefaultWebSocketServerSession>>()

        webSocket("/chat/{chatId}") {
            val chatId = call.parameters["chatId"]
            val userId = call.request.queryParameters["userId"]

            if (chatId == null || userId == null) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Missing parameters"))
                return@webSocket
            }

            val sessionList = chatSessions.getOrPut(chatId) { mutableListOf() }
            sessionList.add(this)

            println("User $userId connected to chat $chatId")

            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val message = frame.readText()
                        println("[$chatId][$userId]: $message")
                        dbRepository.addMessage(
                            Message(
                                id = Random.nextLong(),
                                sender = userId,
                                chatId = chatId,
                                message = message
                            )
                        )

                        // Broadcast message to all other users in the chat
                        sessionList.forEach { session ->
                            if (session != this) {
                                session.send("[$userId]: $message")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("WebSocket error for user $userId: ${e.localizedMessage}")
            } finally {
                sessionList.remove(this)
                println("User $userId disconnected from chat $chatId")
            }
        }
    }
}
