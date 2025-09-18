package dev.jason.app.compose.desktop.messenger.domain

import dev.jason.app.compose.desktop.messenger.domain.api.ApiAuthRepository
import dev.jason.app.compose.desktop.messenger.domain.api.ApiSocketRepository
import dev.jason.app.compose.desktop.messenger.domain.api.VersionCheckRepository
import dev.jason.app.compose.desktop.messenger.domain.prefs.PrefsRepository

interface RepositoryContainer {
    val apiAuthRepository: ApiAuthRepository
    val apiSocketRepository: ApiSocketRepository
    val prefsRepository: PrefsRepository
    val versionCheckRepository: VersionCheckRepository
}