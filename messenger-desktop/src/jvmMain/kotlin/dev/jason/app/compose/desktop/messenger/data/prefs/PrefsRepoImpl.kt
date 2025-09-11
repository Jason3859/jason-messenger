package dev.jason.app.compose.desktop.messenger.data.prefs

import dev.jason.app.compose.desktop.messenger.domain.model.User
import dev.jason.app.compose.desktop.messenger.domain.prefs.PrefsRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

class PrefsRepoImpl : PrefsRepository {

    @Serializable
    data class UserPref(
        val username: String,
        val password: String,
    )

    @Serializable
    data class RoomPref(val roomId: String)

    private val parent = File("res")

    private val userFile = File(parent, "user.json")
    private val roomFile = File(parent, "room.json")

    init {
        if (!parent.exists()) {
            parent.mkdirs()
        }

        userFile.apply {
            if (!exists()) createNewFile()
        }
        roomFile.apply {
            if (!exists()) createNewFile()
        }
    }

    override suspend fun saveUser(user: User) {
        val user = Json.encodeToString(user.toPref())
        userFile.writeText(user)
    }

    override fun getUser(): User? {
        return if (userFile.readText().isNotBlank()) {
            Json.decodeFromString<UserPref>(userFile.readText()).toUser()
        } else null
    }

    override suspend fun deleteUser() {
        userFile.writeText("")
    }

    override suspend fun saveRoom(roomId: String) {
        roomFile.writeText(Json.encodeToString(RoomPref(roomId)))
    }

    override fun getRoom(): String? {
        return if (roomFile.readText().isNotBlank()) {
            Json.decodeFromString<RoomPref>(roomFile.readText()).roomId
        } else null
    }

    override suspend fun deleteRoom() {
        roomFile.writeText("")
    }

    private fun User.toPref() = UserPref(this.username, this.password)
    private fun UserPref.toUser() = User(this.username, this.password)
}