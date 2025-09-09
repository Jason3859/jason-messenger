package dev.jason.app.compose.messenger.data.api.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import dev.jason.app.compose.messenger.data.api.mappers.toDto
import dev.jason.app.compose.messenger.data.api.model.ChatroomDto
import dev.jason.app.compose.messenger.domain.api.ApiAuthRepository
import dev.jason.app.compose.messenger.domain.model.Result
import dev.jason.app.compose.messenger.domain.model.User
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
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

    override suspend fun signin(user: User): Result {
        return try {
            val request = client.post("$baseUrl/signup") {
                contentType(ContentType.Application.Json)
                setBody(Json.Default.encodeToString(user.toDto()))
            }
            val response = request.bodyAsText()
            Log.d("Signup", response)
            val serializedResponse = Json.Default.decodeFromString<ApiResponse>(response)
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
                setBody(Json.Default.encodeToString(user.toDto()))
            }
            val response = request.bodyAsText()
            Log.d("signin", response)
            val serializedResponse = Json.Default.decodeFromString<ApiResponse>(response)

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

    override suspend fun deleteAccount(user: User): Result {
        return try {
            client.delete("$baseUrl/delete-account") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(user.toDto()))
            }

            Toast.makeText(
                context,
                "Successfully deleted ${user.username}",
                Toast.LENGTH_LONG
            ).show()

            Result.Success

        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    override suspend fun deleteChatroom(chatroomId: String): Result {
        return try {
            client.delete("$baseUrl/delete-chatroom") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(ChatroomDto(chatroomId)))
            }

            Toast.makeText(
                context,
                "Successfully deleted $chatroomId",
                Toast.LENGTH_LONG
            ).show()

            Result.Success
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}