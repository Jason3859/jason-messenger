package dev.jason

import dev.jason.data.database.DatabaseFactory
import dev.jason.data.projectModule
import dev.jason.domain.DatabaseRepository
import dev.jason.plugins.configureRouting
import dev.jason.plugins.configureSerialization
import dev.jason.plugins.configureSockets
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    initKoin()
    DatabaseFactory.init(environment.config)
    val dbRepository by inject<DatabaseRepository>()
    configureSerialization()
    configureSockets(dbRepository)
    configureRouting()
}

private fun Application.initKoin() {
    install(Koin) {
        modules(projectModule)
    }
}