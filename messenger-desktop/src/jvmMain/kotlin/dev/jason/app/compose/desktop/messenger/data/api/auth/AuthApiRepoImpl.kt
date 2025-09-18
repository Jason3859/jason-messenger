package dev.jason.app.compose.desktop.messenger.data.api.auth

import dev.jason.app.compose.desktop.messenger.data.api.model.ChatroomDto
import dev.jason.app.compose.desktop.messenger.data.api.model.UserDto
import dev.jason.app.compose.desktop.messenger.domain.api.ApiAuthRepository
import dev.jason.app.compose.desktop.messenger.domain.model.Result
import dev.jason.app.compose.desktop.messenger.domain.model.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AuthApiRepoImpl(private val client: OkHttpClient) : ApiAuthRepository {

    @Serializable
    data class ApiResponse(
        val username: String?,
        val password: String?,
        val verified: Boolean,
    )

    private companion object {
        const val BASE_URL = "https://jason-messenger.up.railway.app"
    }

    override suspend fun signin(user: User): Result {
        return try {
            val requestBody = Json.encodeToString(UserDto(user.username, user.password))

            val request = Request.Builder()
                .url("$BASE_URL/signup")
                .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body

            val deserialized = Json.decodeFromString<ApiResponse>(body?.string() ?: return Result.Error(null))

            if (deserialized.username == "user already exists") {
                Result.UserAlreadyExists
            } else {
                if (deserialized.verified) {
                    Result.Success
                } else {
                    Result.Error(null)
                }
            }.also { body.close() }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    override suspend fun login(user: User): Result {
        return try {
            val request = Request.Builder()
                .url("$BASE_URL/signin")
                .post(
                    Json.encodeToString(UserDto(user.username, user.password))
                        .toRequestBody("application/json".toMediaTypeOrNull())
                )
                .build()

            val response = client.newCall(request).execute()
            val body = response.body ?: return Result.Error(null)
            val deserialized = Json.decodeFromString<ApiResponse>(body.string())

            if (deserialized.password == "invalid") {
                Result.InvalidPassword
            } else {
                if (deserialized.username == null) {
                    Result.UserNotFound
                } else {
                    Result.Success
                }
            }.also { body.close() }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    override suspend fun deleteAccount(user: User): Result {
        return try {
            val request = Request.Builder()
                .url("$BASE_URL/delete-account")
                .delete(
                    Json.encodeToString(UserDto(user.username, user.password))
                        .toRequestBody("application/json".toMediaTypeOrNull())
                )
                .build()

            client.newCall(request).execute()
            Result.Success
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    override suspend fun deleteChatroom(chatroomId: String): Result {
        return try {
            val request = Request.Builder()
                .url("$BASE_URL/delete-chatroom")
                .delete(
                    Json.encodeToString(ChatroomDto(chatroomId))
                        .toRequestBody("application/json".toMediaTypeOrNull())
                )
                .build()

            client.newCall(request).execute()
            Result.Success
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}