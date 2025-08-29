package dev.jason.project.ktor.messenger.data.database

import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private const val PATH = "database.supabase"

    fun init(environment: ApplicationConfig) {
        val url = environment.config(PATH).tryGetString("url")!!
        val username = environment.config(PATH).tryGetString("username")!!
        val password = System.getenv("SUPABASE_PASSWORD")

        try {
            println("connecting to remote db")
            Database.connect(
                url = url,
                user = username,
                password = password,
                driver = "org.postgresql.Driver"
            )
            println("connected to remote db")
        } catch (e: Exception) {
            Database.connect("jdbc:sqlite:messages.db")
            println("connected to local db")
            println("error was:")
            println(e.message)
        }

        transaction {
            SchemaUtils.create(MessagesDao, UsersDao)
        }
    }
}