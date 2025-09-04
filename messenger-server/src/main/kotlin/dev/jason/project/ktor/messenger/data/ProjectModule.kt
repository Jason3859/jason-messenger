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
            "jdbc:postgresql://db.wybspetuojavvbpqyewv.supabase.co:5432/postgres?user=postgres&password=${
                System.getenv(
                    "SUPABASE_PASSWORD"
                )
            }"
        )
    }

    single<DatabaseRepository> {
        MessagesDatabaseRepository(get())
    }

    single<UserRepository> {
        UsersDatabaseRepository(get())
    }
}