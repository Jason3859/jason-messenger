package dev.jason.app.compose.messenger.domain

import dev.jason.app.compose.messenger.domain.api.ApiAuthRepository
import dev.jason.app.compose.messenger.domain.prefs.PrefsRepository

interface RepositoryContainer {
    val apiAuthRepository: ApiAuthRepository
    val prefsRepository: PrefsRepository
}