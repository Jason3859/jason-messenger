package dev.jason.project.ktor.messenger

import dev.jason.project.ktor.messenger.data.projectModule
import dev.jason.project.ktor.messenger.plugins.configureRouting
import dev.jason.project.ktor.messenger.plugins.configureSecurity
import dev.jason.project.ktor.messenger.plugins.configureSerialization
import dev.jason.project.ktor.messenger.plugins.configureSockets
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    Database.connect(
        url = System.getenv("DB_URL"),
        user = System.getenv("DB_USER"),
        password = System.getenv("DB_PASSWORD"),
        driver = "org.postgresql.Driver"
    )

    initKoin()
    configureSecurity()
    configureSerialization()
    configureSockets()
    configureRouting()
}

private fun Application.initKoin() {
    install(Koin) {
        modules(projectModule)
    }
}