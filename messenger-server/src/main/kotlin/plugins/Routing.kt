package dev.jason.plugins

import dev.jason.data.UsersDto
import dev.jason.data.toDomain
import dev.jason.domain.UserRepository
import dev.jason.domain.UsersResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class Response(val username: String?, val password: String?, val verified: Boolean)

fun Application.configureRouting() {
    val repository by inject<UserRepository>()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/signup") {
            try {
                val body = call.receive<UsersDto>()
                repository.addUser(body.toDomain())

                call.respond(Response(body.username, body.password, true))
            } catch (e: Exception) {
                call.respond(e.localizedMessage)
            }
        }

        post("/signin") {
            try {
                val body = call.receive<UsersDto>()
                val result = repository.findUser(body.username, body.password)

                call.respond(
                    when (result) {
                        is UsersResponse.Success -> Response(body.username, body.password, true)
                        is UsersResponse.NotFound -> Response(null, null, false)
                        is UsersResponse.InvalidPassword -> Response(null, "invalid", false)
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
                repository.deleteUser(body.username, body.password)

            } catch (e: Exception) {
                call.respond(e.localizedMessage)
            }
        }
    }
}
