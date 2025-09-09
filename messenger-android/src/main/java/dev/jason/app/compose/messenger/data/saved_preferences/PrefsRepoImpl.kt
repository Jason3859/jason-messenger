package dev.jason.app.compose.messenger.data.saved_preferences

import dev.jason.app.compose.messenger.domain.model.User
import dev.jason.app.compose.messenger.domain.model.Preferences
import dev.jason.app.compose.messenger.domain.saved_preferences.PrefsRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

class PrefsRepoImpl(private val file: File) : PrefsRepository {

    @Serializable
    private data class Prefs(
        val username: String? = null,
        val password: String? = null,
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

    override fun getPref(): Preferences {
        return Json.decodeFromString<Prefs>(file.readText()).toDomain()
    }

    override fun deletePrefs() {
        file.writeText(Json.encodeToString(Prefs()))
    }

    private fun Prefs.toDomain(): Preferences {
        val user: User? = try {
            User(this.username!!, this.password!!)
        } catch (_: Exception) {
            null
        }

        return Preferences(user)
    }
}