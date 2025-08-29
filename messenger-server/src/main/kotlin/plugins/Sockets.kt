package dev.jason.plugins

import dev.jason.data.MessageDto
import dev.jason.data.toDomain
import dev.jason.data.toDto
import dev.jason.data.toLong
import dev.jason.domain.DatabaseRepository
import dev.jason.domain.Result
import dev.jason.domain.User
import dev.jason.domain.UserRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    val dbRepository by inject<DatabaseRepository>()
    val userRepository by inject<UserRepository>()
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val chatSessions = ConcurrentHashMap<String, MutableList<DefaultWebSocketServerSession>>()

        webSocket("/chat/{chatRoomId}") {
            val chatRoomId = call.parameters["chatRoomId"]
            val username = call.request.queryParameters["userId"]
            val password = call.request.queryParameters["password"]

            if (chatRoomId == null || username == null || password == null) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Missing parameters"))
                return@webSocket
            }

            val user = userRepository.findUser(User(username, password))

            if (user is Result.NotFound) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Signup first"))
                return@webSocket
            }

            if (user is Result.InvalidPassword) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid password"))
            }

            val sessionList = chatSessions.getOrPut(chatRoomId) { mutableListOf() }
            sessionList.add(this)

            println("User $username connected to chat $chatRoomId")

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
                        dbRepository.addMessage(serializedMessage.toDomain())
                        sessionList.forEach { session ->
                            if (session != this) {
                                val msgToSend = dbRepository.getAllMessages().first { it.message == message }
                                session.send(Json.encodeToString(msgToSend.toDto()))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("WebSocket error for user $username: ${e.localizedMessage}")
            } finally {
                sessionList.remove(this)
                println("User $username disconnected from chat $chatRoomId")
            }
        }
    }
}
