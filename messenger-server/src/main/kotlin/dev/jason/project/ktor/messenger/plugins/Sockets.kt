package dev.jason.project.ktor.messenger.plugins

import dev.jason.project.ktor.messenger.data.model.MessageDto
import dev.jason.project.ktor.messenger.data.model.toDomain
import dev.jason.project.ktor.messenger.data.model.toDto
import dev.jason.project.ktor.messenger.data.model.toLong
import dev.jason.project.ktor.messenger.domain.db.MessagesDatabaseRepository
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.header
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    val messagesDbRepository by inject<MessagesDatabaseRepository>()
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val chatSessions = ConcurrentHashMap<String, MutableList<DefaultWebSocketServerSession>>()

        authenticate("auth-jwt") {
            webSocket("/chat/{chatroomid}") {
                val chatRoomId = call.parameters["chatroomid"]
                val principal = call.principal<JWTPrincipal>()
                    ?: return@webSocket close(
                        CloseReason(
                            CloseReason.Codes.CANNOT_ACCEPT,
                            "unauthorized"
                        )
                    )

                val username = principal.payload.getClaim("username").asString()

                if (chatRoomId == null) {
                    close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Missing parameters"))
                    return@webSocket
                }

                val sessionList = chatSessions.getOrPut(chatRoomId) { mutableListOf() }
                sessionList.add(this)

                println("User $username connected to chat $chatRoomId")
                sessionList.forEach { session ->
                    session.send(
                        Json.encodeToString(
                            MessageDto(
                                id = Random.nextLong(),
                                chatRoomId = chatRoomId,
                                sender = "server@3859✓",
                                message = "User $username connected to the chat",
                                timestamp = LocalDateTime.now().toLong()
                            )
                        )
                    )
                }

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val message = frame.readText()
                            val serializedMessage = MessageDto(
                                id = Random.nextLong(),
                                chatRoomId = chatRoomId,
                                sender = username,
                                message = message,
                                timestamp = LocalDateTime.now().toLong()
                            )
                            messagesDbRepository.addMessage(serializedMessage.toDomain())
                            launch(Dispatchers.IO) {
                                sessionList.forEach { session ->
                                    if (session != this) {
                                        val msgToSend = messagesDbRepository.getAllMessages()
                                            .last { it.message == message }
                                        session.send(Json.encodeToString(msgToSend.toDto()))
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("WebSocket error for user $username: ${e.localizedMessage}")
                    e.printStackTrace()
                } finally {
                    val token = call.request.header("Authorization")!!.removePrefix("Bearer ")

                    TokenBlacklist.invalidatedTokens.add(token)

                    sessionList.remove(this)
                    println("User $username disconnected from chat $chatRoomId")
                    sessionList.forEach { session ->
                        session.send(
                            Json.encodeToString(
                                MessageDto(
                                    id = Random.nextLong(),
                                    chatRoomId = chatRoomId,
                                    sender = "server@3859✓",
                                    message = "User $username disconnected from the chat",
                                    timestamp = LocalDateTime.now().toLong()
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}
