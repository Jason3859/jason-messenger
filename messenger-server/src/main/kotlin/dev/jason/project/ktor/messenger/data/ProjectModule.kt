package dev.jason.project.ktor.messenger.data

import dev.jason.project.ktor.messenger.data.database.ExposedDbRepoImpls
import dev.jason.project.ktor.messenger.domain.db.MessagesDatabaseRepository
import dev.jason.project.ktor.messenger.domain.db.UsersDatabaseRepository
import org.koin.dsl.module
import java.sql.Connection
import java.sql.DriverManager

val projectModule = module {
    single<Connection> {
        DriverManager.getConnection(
            System.getenv("DB_URL"),
            System.getenv("DB_USER"),
            System.getenv("DB_PASSWORD"),
        )
    }

    single<MessagesDatabaseRepository> {
        ExposedDbRepoImpls.ExposedMessagesDbRepo()
    }

    single<UsersDatabaseRepository> {
        ExposedDbRepoImpls.ExposedUsersDbRepo()
    }
}