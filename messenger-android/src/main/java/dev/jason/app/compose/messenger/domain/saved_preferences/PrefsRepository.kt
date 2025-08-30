package dev.jason.app.compose.messenger.domain.saved_preferences

import dev.jason.app.compose.messenger.domain.api.User

interface PrefsRepository {
    suspend fun saveUser(user: User)
    suspend fun saveChatroomId(chatroomId: String)
    fun getPref(): Preferences
    fun deletePrefs()
    fun deleteSavedChatroomId(chatroomId: String)
}