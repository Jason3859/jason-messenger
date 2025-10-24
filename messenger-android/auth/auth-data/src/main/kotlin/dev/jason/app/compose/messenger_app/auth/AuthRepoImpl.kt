package dev.jason.app.compose.messenger_app.auth

import android.util.Log
import dev.jason.app.compose.messenger_app.domain.AuthRepository
import dev.jason.app.compose.messenger_app.domain.AuthResult
import dev.jason.app.compose.messenger_app.domain.User
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.UnknownHostException

internal class AuthRepoImpl(
    private val client: OkHttpClient,
    private val baseUrl: String,
): AuthRepository {
    private val token = MutableStateFlow<String?>(null)

    @Serializable
    private data class Token(val token: String)

    override suspend fun login(user: User): AuthResult {
        try {
            val request = Request.Builder()
                .url("$baseUrl/login")
                .post(
                    Json.encodeToString(
                        mapOf(
                            "username" to user.username,
                            "password" to user.password
                        )
                    ).toRequestBody("application/json".toMediaType())
                )
                .build()

            val response = client.newCall(request).execute()
            val body = response.body.string()

            Log.d("AuthRepoImpl", "api login response code: ${response.code}")
            Log.d("AuthRepoImpl", "api login response body: $body")

            if (response.code == HttpStatusCode.OK.value) {
                val token = Json.decodeFromString<Token>(body).token
                this.token.update { token }
                return AuthResult.Success
            }

            if (response.code == HttpStatusCode.NotFound.value) {
                return AuthResult.NotFound
            }

            if (response.code == HttpStatusCode.Unauthorized.value) {
                return AuthResult.InvalidPassword
            }

            return AuthResult.NotFound

        } catch (_: UnknownHostException) {
            return AuthResult.NoInternet
        }
    }

    override suspend fun signin(user: User): AuthResult {
        try {
            val request = Request.Builder()
                .url("$baseUrl/signin")
                .post(
                    Json.encodeToString(
                        mapOf(
                            "username" to user.username,
                            "password" to user.password
                        )
                    ).toRequestBody("application/json".toMediaType())
                )
                .build()

            val response = client.newCall(request).execute()
            val body = response.body

            Log.d("AuthRepoImpl", "api signin response code: ${response.code}")
            Log.d("AuthRepoImpl", "Api signin response body: ${body.string()}")

            return when (response.code) {
                HttpStatusCode.Created.value -> AuthResult.Success
                HttpStatusCode.Conflict.value, HttpStatusCode.NotAcceptable.value -> AuthResult.UserAlreadyExists
                else -> {
                    Log.d("AuthRepoImpl", "response code: ${response.code}")
                    throw IllegalStateException()
                }
            }

        } catch (_: UnknownHostException) {
            return AuthResult.NoInternet
        }
    }

    override fun getToken(): String {
        val token by lazy { this.token.value }

        return token ?: throw IllegalStateException("Token is null")
    }
}