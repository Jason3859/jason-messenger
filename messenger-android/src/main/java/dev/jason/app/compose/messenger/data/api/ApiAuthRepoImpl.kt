package dev.jason.app.compose.messenger.data.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import dev.jason.app.compose.messenger.data.api.mappers.toDto
import dev.jason.app.compose.messenger.domain.api.ApiAuthRepository
import dev.jason.app.compose.messenger.domain.api.Result
import dev.jason.app.compose.messenger.domain.api.User
import dev.jason.app.compose.messenger.domain.database.Message
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.UnknownHostException

class ApiAuthRepoImpl(
    private val client: HttpClient,
    private val context: Context
) : ApiAuthRepository {

    private val baseUrl = "https://jason-messenger.up.railway.app"

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
            Result.Error(e)
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
            Result.Error(e)
        }
    }

    override suspend fun connect(user: User, chatroomID: String): Result {
        return try {
            session = client.webSocketSession {
                url("wss://jason-messenger.up.railway.app/chat/$chatroomID?userId=${user.username}&password=${user.password}")
            }

            Log.d("ws", "Session initialized & connected")

            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        Log.d("ws", "Message: ${frame.readText()}")
                    }

                    is Frame.Close -> {
                        Log.d("ws", "Closed by server")
                    }

                    else -> Log.d("ws", "error")
                }
            }

            Result.Success
        } catch (e: Exception) {
            Log.w("websocket", e.stackTraceToString())
            Result.Error(e)
        }
    }


    override suspend fun listenToMessages() {
        for (frame in session.incoming) {
            Log.d("ws message", (frame as? Frame.Text)?.readText() ?: "error")
        }
    }


    override suspend fun sendMessage(message: Message) {
        TODO()
    }
}