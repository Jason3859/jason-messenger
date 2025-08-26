package dev.jason.app.compose.messenger.domain

import dev.jason.app.compose.messenger.domain.api.ApiRepository
import dev.jason.app.compose.messenger.domain.database.DatabaseRepository
import dev.jason.app.compose.messenger.domain.saved_preferences.PrefsRepository

interface RepositoryContainer {
    val databaseRepository: DatabaseRepository
    val apiRepository: ApiRepository
    val prefsRepository: PrefsRepository
}