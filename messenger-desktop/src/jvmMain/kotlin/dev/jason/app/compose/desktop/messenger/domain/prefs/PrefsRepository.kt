package dev.jason.app.compose.desktop.messenger.domain.prefs

import dev.jason.app.compose.desktop.messenger.domain.model.User

interface PrefsRepository {

    suspend fun saveUser(user: User)
    fun getUser(): User?
    suspend fun deleteUser()

    suspend fun saveRoom(roomId: String)
    fun getRoom(): String?
    suspend fun deleteRoom()
}