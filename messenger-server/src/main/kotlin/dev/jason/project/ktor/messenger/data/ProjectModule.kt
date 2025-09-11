package dev.jason.project.ktor.messenger.data

import dev.jason.project.ktor.messenger.data.database.MessagesDatabaseRepository
import dev.jason.project.ktor.messenger.data.database.UsersDatabaseRepository
import dev.jason.project.ktor.messenger.domain.DatabaseRepository
import dev.jason.project.ktor.messenger.domain.UserRepository
import org.koin.dsl.module
import java.sql.Connection
import java.sql.DriverManager

val projectModule = module {
    single<Connection> {
        DriverManager.getConnection(
            "jdbc:postgresql://${System.getenv("PGHOST")}:${System.getenv("PGPORT")}/${System.getenv("PGDATABASE")}" +
                    "?user=${System.getenv("PGUSER")}&password=${System.getenv("PGPASSWORD")}&sslmode=require&options=-c%20TimeZone=UTC"
        ).also {
            it.createStatement().use { stmt ->
                stmt.execute("SET TIMEZONE TO 'Asia/Kolkata'")
            }
        }
    }

    single<DatabaseRepository> {
        MessagesDatabaseRepository(get())
    }

    single<UserRepository> {
        UsersDatabaseRepository(get())
    }
}