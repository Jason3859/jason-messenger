package dev.jason.app.compose.desktop.messenger.domain.api

interface VersionCheckRepository {
    suspend fun getVersion(): String
}