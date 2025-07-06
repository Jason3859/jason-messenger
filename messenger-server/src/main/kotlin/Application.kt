package dev.jason

import dev.jason.data.LocalJsonDbRepository
import dev.jason.plugins.configureRouting
import dev.jason.plugins.configureSerialization
import dev.jason.plugins.configureSockets
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureSockets(dbRepository = LocalJsonDbRepository())
    configureRouting()
}
