package dev.jason.app.compose.messenger.domain.api

interface VersionCheckRepository {
    suspend fun getVersion(): String
}