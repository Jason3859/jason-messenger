package dev.jason.app.compose.messenger.data.saved_preferences

import dev.jason.app.compose.messenger.domain.api.User
import dev.jason.app.compose.messenger.domain.saved_preferences.Preferences
import dev.jason.app.compose.messenger.domain.saved_preferences.PrefsRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

class PrefsRepoImpl(private val file: File) : PrefsRepository {

    @Serializable
    private data class Prefs(
        val username: String? = null,
        val password: String? = null,
        val chatroomId: String? = null
    )

    init {
        if (!file.exists()) {
            file.createNewFile()
            file.writeText(Json.encodeToString(Prefs()))
        }
    }

    override suspend fun saveUser(user: User) {
        file.writeText(Json.encodeToString(Prefs(user.username, user.password)))
    }

    override suspend fun saveChatroomId(chatroomId: String) {
        val saved = Json.decodeFromString<Prefs>(file.readText())
        file.writeText(Json.encodeToString(Prefs(saved.username, saved.password, saved.chatroomId)))
    }

    override fun getPref(): Preferences {
        return Json.decodeFromString<Prefs>(file.readText()).toDomain()
    }

    override fun deletePrefs() {
        file.writeText(Json.encodeToString(Prefs()))
    }

    override fun deleteSavedChatroomId(chatroomId: String) {
        file.writeText(Json.encodeToString(Prefs(getPref().user?.username, getPref().user?.password)))
    }

    private fun Prefs.toDomain(): Preferences {
        val user: User? = try {
            User(this.username!!, this.password!!)
        } catch (_: Exception) {
            null
        }

        return Preferences(user, this.chatroomId)
    }
}