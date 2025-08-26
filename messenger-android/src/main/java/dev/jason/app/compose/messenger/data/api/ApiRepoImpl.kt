package dev.jason.app.compose.messenger.data.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import dev.jason.app.compose.messenger.data.api.mappers.toDto
import dev.jason.app.compose.messenger.domain.api.ApiRepository
import dev.jason.app.compose.messenger.domain.api.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json

class ApiRepoImpl(
    private val client: HttpClient,
    private val baseUrl: String,
    private val context: Context
) : ApiRepository {

    private lateinit var session: WebSocketSession

    override suspend fun signin(user: User): Boolean {
        return try {
            val response = client.post("$baseUrl/signin") {
                setBody(Json.encodeToString(user.toDto()))
            }
            Log.d("Signin", response.bodyAsText())
            true
        } catch (e: Exception) {
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
            Log.e("Signin Error", e.localizedMessage)
            false
        }
    }

    override suspend fun login(user: User): Boolean {
        return try {
            val response = client.post("$baseUrl/signin") {
                user.toDto()
            }
            val body = response.body<Response>()
            when (body) {
                is Response.Success -> true
                else -> false
            }
        } catch (_: Exception) {
            Toast.makeText(context, "Timeout", Toast.LENGTH_LONG).show()
            false
        }
    }

    override suspend fun connect(
        user: User,
        chatroomID: String
    ): Boolean {
        return try {
            client.webSocket(
                method = HttpMethod.Get,
                host = baseUrl,
                port = 8080,
                path = "/chat/$chatroomID"
            ) {
                parametersOf("userId", user.username)
                parametersOf("password", user.password)
                session = this
            }
            true
        } catch (_: Exception) {
            Toast.makeText(context, "Timeout", Toast.LENGTH_LONG).show()
            false
        }
    }

    override suspend fun sendMessage(message: String) {
        session.send(message)
    }
}