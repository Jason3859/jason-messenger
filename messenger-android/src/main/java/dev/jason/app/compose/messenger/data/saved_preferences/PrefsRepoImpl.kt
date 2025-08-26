package dev.jason.app.compose.messenger.data.saved_preferences

import dev.jason.app.compose.messenger.domain.api.User
import dev.jason.app.compose.messenger.domain.saved_preferences.Preferences
import dev.jason.app.compose.messenger.domain.saved_preferences.PrefsRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class PrefsRepoImpl(private val file: File) : PrefsRepository {

    @Serializable
    private data class Prefs(
        val username: String = "",
        val password: String = "",
        val chatroomId: String = ""
    )

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    private lateinit var user: User

    override suspend fun saveUser(user: User) {
        this.user = user
        file.writeText(Json.encodeToString(Prefs(user.username, user.username)))
    }

    override suspend fun saveChatroomId(chatroomId: String) {
        val instance = Prefs(user.username, user.password, chatroomId)
        file.writeText(Json.encodeToString(instance))
    }

    override fun getPref(): Preferences? {
        return if (!file.exists()) {
            Json.decodeFromString<Prefs>(file.readText()).toDomain()
        } else null
    }

    private fun Prefs.toDomain() = Preferences(
        user = User(user.username, user.password),
        chatroomId = chatroomId
    )
}