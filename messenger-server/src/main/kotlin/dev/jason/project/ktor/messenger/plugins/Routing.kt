package dev.jason.project.ktor.messenger.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.jason.project.ktor.messenger.data.model.UsersDto
import dev.jason.project.ktor.messenger.data.model.toDomain
import dev.jason.project.ktor.messenger.data.model.toDto
import dev.jason.project.ktor.messenger.domain.db.MessagesDatabaseRepository
import dev.jason.project.ktor.messenger.domain.db.UsersDatabaseRepository
import dev.jason.project.ktor.messenger.domain.model.Result
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.util.Date
import java.util.UUID

@Serializable
data class Token(val token: String)

fun Application.configureRouting() {
    val usersDbRepository by inject<UsersDatabaseRepository>()
    val messagesDbRepository by inject<MessagesDatabaseRepository>()
    val secret = System.getenv("JWT_SECRET")
    val issuer = System.getenv("JWT_ISSUER")
    val audience = System.getenv("JWT_AUDIENCE")

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        authenticate("auth-jwt") {
            get("/get-messages") {
                val messages = messagesDbRepository
                    .getAllMessages()
                    .map { it.toDto() }

                val serialized = Json.encodeToString(messages)

                call.respond(serialized)
            }
        }

        post("/signin") {
            try {
                val body = call.receive<UsersDto>()
                val result = usersDbRepository.addUser(body.toDomain())

                if (result is Result.UserAlreadyExists) {
                    call.respond(HttpStatusCode.Conflict, "User already exists")
                    return@post
                }

                if (body.username == "server@3859✓") {
                    call.respond(HttpStatusCode.NotAcceptable, "Cannot create user with that name")
                }

                call.respond(HttpStatusCode.Created, "User ${body.username} signed in")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
                e.printStackTrace()
            }
        }

        post("/login") {
            try {
                val body = call.receive<UsersDto>()
                val result = usersDbRepository.findUser(body.toDomain())

                when (result) {
                    is Result.Success -> {
                        val token = JWT.create()
                            .withAudience(audience)
                            .withIssuer(issuer)
                            .withClaim("username", body.username)
                            .withJWTId(UUID.randomUUID().toString())
                            .withExpiresAt(Date(System.currentTimeMillis() + 600000))
                            .sign(Algorithm.HMAC256(secret))
                        call.response.headers.append("Cache-Control", "no-cache, no-store, must-revalidate")
                        call.respond(Token(token))
                    }
                    is Result.NotFound -> call.respond(HttpStatusCode.NotFound, "User not found")
                    is Result.InvalidPassword -> call.respond(HttpStatusCode.Unauthorized, "Invalid password")
                    else -> throw IllegalArgumentException("Unknown error")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
                e.printStackTrace()
            }
        }

        delete("/delete-account") {
            try {
                val body = call.receive<UsersDto>()
                val response = usersDbRepository.deleteUser(body.toDomain())
                if (response is Result.Success) {
                    call.respond(HttpStatusCode.Accepted).also { println("User ${body.username} deleted their account") }
                } else println(response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
                e.printStackTrace()
            }
        }

        delete("/delete-chatroom") {
            @Serializable
            data class ChatroomDto(val chatroomid: String)
            try {
                val body = call.receive<ChatroomDto>()
                messagesDbRepository.deleteChatRoom(body.chatroomid)
                call.respond(HttpStatusCode.Accepted).also { println("deleted chatroom ${body.chatroomid}") }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
                e.printStackTrace()
            }
        }
    }
}
