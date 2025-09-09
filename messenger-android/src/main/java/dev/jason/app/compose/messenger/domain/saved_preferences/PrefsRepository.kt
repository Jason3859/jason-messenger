package dev.jason.app.compose.messenger.domain.saved_preferences

import dev.jason.app.compose.messenger.domain.model.Preferences
import dev.jason.app.compose.messenger.domain.model.User

interface PrefsRepository {
    suspend fun saveUser(user: User)
    fun getPref(): Preferences
    fun deletePrefs()
}