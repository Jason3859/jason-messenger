package dev.jason.project.ktor.messenger.plugins

import dev.jason.project.ktor.messenger.data.UsersDto
import dev.jason.project.ktor.messenger.data.toDomain
import dev.jason.project.ktor.messenger.data.toDto
import dev.jason.project.ktor.messenger.domain.DatabaseRepository
import dev.jason.project.ktor.messenger.domain.UserRepository
import dev.jason.project.ktor.messenger.domain.Result
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

@Serializable
data class Response(val username: String?, val password: String?, val verified: Boolean)

fun Application.configureRouting() {
    val userRepository by inject<UserRepository>()
    val dbRepository by inject<DatabaseRepository>()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/get-messages/{chatroom}") {
            val chatroom = call.pathParameters["chatroom"]

            val messages = dbRepository
                .getAllMessages()
                .map { it.toDto() }
                .filter { it.chatRoomId == chatroom }

            val serialized = Json.encodeToString(messages)

            call.respond(serialized)
        }

        post("/signup") {
            try {
                val body = call.receive<UsersDto>()
                val result = userRepository.addUser(body.toDomain())

                if (result is Result.UserAlreadyExists) {
                    call.respond(Response("user already exists", null, false))
                    return@post
                }
                call.respond(Response(body.username, body.password, true)).also { println("User ${body.username} signed in") }
            } catch (e: Exception) {
                call.respond(e.message!!)
                e.printStackTrace()
            }
        }

        post("/signin") {
            try {
                val body = call.receive<UsersDto>()
                val result = userRepository.findUser(body.toDomain())

                call.respond(
                    when (result) {
                        is Result.Success -> Response(body.username, body.password, true).also { println("User ${body.username} logged in") }
                        is Result.NotFound -> Response(null, null, false)
                        is Result.InvalidPassword -> Response(null, "invalid", false)
                        else -> throw IllegalArgumentException("Unknown error")
                    }
                )
            } catch (e: Exception) {
                call.respond(e.localizedMessage)
                e.printStackTrace()
            }
        }

        delete("/delete-account") {
            try {
                val body = call.receive<UsersDto>()
                val response = userRepository.deleteUser(body.toDomain())
                if (response is Result.Success) {
                    call.respond(Result.Success()).also { println("User ${body.username} deleted their account") }
                } else println(response)
            } catch (e: Exception) {
                call.respond(e.localizedMessage)
                e.printStackTrace()
            }
        }

        delete("/delete-chatroom") {
            @Serializable
            data class ChatroomDto(val chatroomID: String)
            try {
                val body = call.receive<ChatroomDto>()
                dbRepository.deleteChatRoom(body.chatroomID)
                call.respond(Result.Success()).also { println("deleted chatroom ${body.chatroomID}") }
            } catch (e: Exception) {
                call.respond(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }
}
