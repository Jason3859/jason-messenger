package dev.jason.data

import kotlinx.serialization.json.Json
import java.io.File

interface DatabaseRepository {
    suspend fun addMessage(message: Message)
}

class LocalJsonDbRepository : DatabaseRepository {

    private val jsonFile = File("src\\main\\resources\\messages.json")

    override suspend fun addMessage(message: Message) {
        if (jsonFile.readText().isNotEmpty()) {
            jsonFile.appendText(
                text = Json.encodeToString(message)
            )
        } else {
            jsonFile.writeText(Json.encodeToString(message))
        }
    }
}