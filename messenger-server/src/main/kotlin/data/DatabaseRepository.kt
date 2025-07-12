package dev.jason.data

import kotlinx.serialization.json.Json
import java.io.File

interface DatabaseRepository {
    suspend fun addMessage(message: Message)
}

class LocalJsonDbRepository : DatabaseRepository {

    private val jsonFile = File("src\\main\\resources\\messages.json")

    init {
        if (!jsonFile.exists()) {
            jsonFile.createNewFile()
        }
    }

    override suspend fun addMessage(message: Message) {
        val content = jsonFile.readText()
        if (content.isNotEmpty()) {
            val existing = Json.decodeFromString<List<Message>>(content)
            val writable = existing + message
            jsonFile.writeText(Json.encodeToString(writable))
        } else {
            jsonFile.writeText(Json.encodeToString(listOf(message)))
        }
    }
}