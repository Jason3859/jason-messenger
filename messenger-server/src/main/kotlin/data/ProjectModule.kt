package dev.jason.data

import dev.jason.data.database.SupabaseDB
import dev.jason.data.database.UsersSupabaseDB
import dev.jason.domain.DatabaseRepository
import dev.jason.domain.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val projectModule = module {
    singleOf<DatabaseRepository>(::SupabaseDB)
    singleOf<UserRepository>(::UsersSupabaseDB)
}