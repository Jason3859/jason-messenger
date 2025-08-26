package dev.jason.data.database

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

        if (System.getenv("ACTIVE_PROFILE") == "production") {
            Database.connect(url, user = username, password = password)
        } else {
            Database.connect("jdbc:sqlite:messages.db")
        }

        transaction {
            SchemaUtils.create(MessagesDao, UsersDao)
        }
    }
}