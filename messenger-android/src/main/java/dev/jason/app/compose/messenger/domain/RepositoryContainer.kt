package dev.jason.app.compose.messenger.domain

import dev.jason.app.compose.messenger.domain.api.ApiAuthRepository
import dev.jason.app.compose.messenger.domain.api.ApiSocketRepository
import dev.jason.app.compose.messenger.domain.saved_preferences.PrefsRepository

interface RepositoryContainer {
    val apiAuthRepository: ApiAuthRepository
    val prefsRepository: PrefsRepository
    val apiSocketRepository: ApiSocketRepository
}