package dev.jason.app.compose.messenger.data.api.version

import dev.jason.app.compose.messenger.data.api.model.VersionDto
import dev.jason.app.compose.messenger.domain.api.VersionCheckRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class VersionCheckRepoImpl(private val client: HttpClient) : VersionCheckRepository {
    override suspend fun getVersion(): String {
        val request = client.get("https://raw.githubusercontent.com/Jason3859/application-versions/main/messenger.json")
        val response = request.bodyAsText()
        val serialized = Json.decodeFromString<VersionDto>(response)

        return serialized.version
    }
}