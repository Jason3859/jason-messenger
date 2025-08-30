package dev.jason.app.compose.messenger.data.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import dev.jason.app.compose.messenger.data.api.mappers.toDto
import dev.jason.app.compose.messenger.domain.api.ApiRepository
import dev.jason.app.compose.messenger.domain.api.Result
import dev.jason.app.compose.messenger.domain.api.User
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.UnknownHostException

class ApiRepoImpl(
    private val client: HttpClient,
    private val baseUrl: String,
    private val context: Context
) : ApiRepository {

    @Serializable
    data class ApiResponse(
        val username: String?,
        val password: String?,
        val verified: Boolean,
    )

    private lateinit var session: WebSocketSession

    override suspend fun signin(user: User): Result {
        return try {
            val request = client.post("$baseUrl/signup") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(user.toDto()))
            }
            val response = request.bodyAsText()
            Log.d("Signup", response)
            val serializedResponse = Json.decodeFromString<ApiResponse>(response)
            if (serializedResponse.username == "user already exists") {
                Toast.makeText(
                    context,
                    "User with ${user.username} already exists. Try another one.",
                    Toast.LENGTH_LONG
                ).show()
                Result.UserAlreadyExists
            } else {
                if (serializedResponse.verified) {
                    Toast.makeText(context, "Signed In", Toast.LENGTH_LONG).show()
                }
                Result.Success
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
            Log.e("Signin Error", e.stackTraceToString())
            Result.Error(e.message!!)
        }
    }

    override suspend fun login(user: User): Result {
        return try {
            val request = client.post("$baseUrl/signin") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(user.toDto()))
            }
            val response = request.bodyAsText()
            Log.d("signin", response)
            val serializedResponse = Json.decodeFromString<ApiResponse>(response)

            if (serializedResponse.password == "invalid") {
                Toast.makeText(context, "Invalid Password", Toast.LENGTH_LONG).show()
                Result.InvalidPassword
            } else if (!serializedResponse.verified) {
                if (serializedResponse.username == null) {
                    Toast.makeText(
                        context,
                        "User with username ${user.username} not found. Try signing in.",
                        Toast.LENGTH_LONG
                    ).show()
                    Result.NotFound
                } else {
                    Result.Success
                }
            } else {
                Result.Success
            }
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, e.message!!, Toast.LENGTH_LONG).show()
            }
            Log.e("login", e.stackTraceToString())
            Result.Error(e.message!!)
        }
    }

    override suspend fun connect(
        user: User,
        chatroomID: String
    ): Result {
        return try {
            val session = client.webSocketSession(
                urlString = "wss://jason-messenger.onrender.com/chat/$chatroomID"
            ) {
                parametersOf("userId", user.username)
                parametersOf("password", user.password)
            }
            this.session = session
            Log.d("websocket connection", "connected to $chatroomID")
            Toast.makeText(context, "Connected to $chatroomID", Toast.LENGTH_SHORT).show()
            Result.Success
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            Log.e("websocket error", e.stackTraceToString())
            Result.Error(e.message!!)
        }
    }

    override suspend fun sendMessage(message: String) {
        session.send(message)
    }
}