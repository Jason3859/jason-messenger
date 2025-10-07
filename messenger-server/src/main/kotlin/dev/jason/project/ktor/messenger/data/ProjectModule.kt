package dev.jason.project.ktor.messenger.data

import dev.jason.project.ktor.messenger.data.database.MongoDb
import dev.jason.project.ktor.messenger.domain.db.MessagesDatabaseRepository
import dev.jason.project.ktor.messenger.domain.db.UsersDatabaseRepository
import org.koin.dsl.module

val projectModule = module {

    single<MessagesDatabaseRepository> {
        MongoDb.MessagesDbRepoImpl()
    }

    single<UsersDatabaseRepository> {
        MongoDb.UsersDbRepoImpl()
    }
}