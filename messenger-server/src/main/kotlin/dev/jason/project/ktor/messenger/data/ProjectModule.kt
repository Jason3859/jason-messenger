package dev.jason.project.ktor.messenger.data

import dev.jason.project.ktor.messenger.data.database.SupabaseDB
import dev.jason.project.ktor.messenger.data.database.UsersSupabaseDB
import dev.jason.project.ktor.messenger.domain.DatabaseRepository
import dev.jason.project.ktor.messenger.domain.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val projectModule = module {
    singleOf<DatabaseRepository>(::SupabaseDB)
    singleOf<UserRepository>(::UsersSupabaseDB)
}