package dev.jason.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/messages") {
            val code = call.parameters["code"]

            if (code != System.getenv("code")) {
                call.respond("Unauthorized")
                return@get
            }

            val file = File("src\\main\\resources\\messages.json")

            call.respond(file.readText())
        }
    }
}
