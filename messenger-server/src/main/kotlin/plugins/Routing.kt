package dev.jason.plugins

import dev.jason.data.UsersDto
import dev.jason.data.toDomain
import dev.jason.domain.DatabaseRepository
import dev.jason.domain.UserRepository
import dev.jason.domain.Response
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
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

        post("/signup") {
            try {
                val body = call.receive<UsersDto>()
                userRepository.addUser(body.toDomain())

                call.respond(Response(body.username, body.password, true)).also { println("User ${body.username} signed in") }
            } catch (e: Exception) {
                call.respond(e.localizedMessage)
            }
        }

        post("/signin") {
            try {
                val body = call.receive<UsersDto>()
                val result = userRepository.findUser(body.username, body.password)

                call.respond(
                    when (result) {
                        is Response.Success -> Response(body.username, body.password, true).also { println("User ${body.username} logged in") }
                        is Response.NotFound -> Response(null, null, false)
                        is Response.InvalidPassword -> Response(null, "invalid", false)
                        else -> throw IllegalArgumentException("Unknown error")
                    }
                )
            } catch (e: Exception) {
                call.respond(e.localizedMessage)
            }
        }

        delete("/delete-account") {
            try {
                val body = call.receive<UsersDto>()
                userRepository.deleteUser(body.username, body.password)
                call.respond(Response.Success).also { println("User ${body.username} deleted their account") }
            } catch (e: Exception) {
                call.respond(e.localizedMessage)
            }
        }

        delete("/delete-chatroom") {
            @Serializable
            data class ChatroomDto(val chatroomID: String)
            try {
                val body = call.receive<ChatroomDto>()
                dbRepository.deleteChatRoom(body.chatroomID)
                call.respond(Response.Success).also { println("deleted chatroom ${body.chatroomID}") }
            } catch (e: Exception) {
                call.respond(e.localizedMessage)
            }
        }
    }
}
