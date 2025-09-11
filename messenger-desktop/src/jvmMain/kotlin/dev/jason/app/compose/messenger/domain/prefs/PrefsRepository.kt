package dev.jason.app.compose.messenger.domain.prefs

import dev.jason.app.compose.messenger.domain.model.User

interface PrefsRepository {

    suspend fun saveUser(user: User)
    fun getUser(): User?

    suspend fun saveRoom(roomId: String)
    fun getRoom(): String?
}