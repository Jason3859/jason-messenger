package dev.jason.app.compose.desktop.messenger.data.api.version

import dev.jason.app.compose.desktop.messenger.data.api.model.Version
import dev.jason.app.compose.desktop.messenger.domain.api.VersionCheckRepository
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class VersionCheckRepoImpl(private val client: OkHttpClient) : VersionCheckRepository {
    override suspend fun getVersion(): String {
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/Jason3859/application-versions/main/messenger.json")
            .get()
            .build()

        val response = client.newCall(request).execute()
        val body = response.body ?: throw IllegalStateException()
        val serialized = Json.decodeFromString<Version>(body.string())

        return serialized.version
    }
}