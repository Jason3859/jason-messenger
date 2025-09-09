package dev.jason.app.compose.messenger.data.api.socket

import dev.jason.app.compose.messenger.data.api.mappers.toDomain
import dev.jason.app.compose.messenger.data.api.model.MessageDto
import dev.jason.app.compose.messenger.domain.api.ApiSocketRepository
import dev.jason.app.compose.messenger.domain.model.Message
import dev.jason.app.compose.messenger.domain.model.Result
import dev.jason.app.compose.messenger.domain.model.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.Json
import okhttp3.*

class ApiSocketImpl(
    private val client: OkHttpClient,
) : ApiSocketRepository {

    private var socket: WebSocket? = null
    private val json = Json { ignoreUnknownKeys = true }
    private val messages = MutableSharedFlow<Message>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun connect(user: User, chatroomId: String): Result = suspendCancellableCoroutine { cont ->

        val request = Request.Builder()
            .url("wss://jason-messenger.up.railway.app/chat/$chatroomId?userId=${user.username}&password=${user.password}")
            .build()

        val messagesRequest = Request.Builder()
            .url("https://jason-messenger.up.railway.app/get-messages/$chatroomId")
            .build()

        val job = coroutineScope.launch {
            val request = client.newCall(messagesRequest).execute()
            val response = request.body

            val deserialized = json.decodeFromString<List<MessageDto>>(response?.string() ?: return@launch)

            deserialized.forEach {
                messages.emit(it.toDomain())
            }
        }

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                socket = webSocket
                println("WebSocket connected to $chatroomId")
                cont.resume(Result.Success) { cause, _, _ -> }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val messageDto = json.decodeFromString<MessageDto>(text)
                    coroutineScope.launch {
                        while (!job.isCompleted) {
                            delay(10)
                        }
                        messages.emit(messageDto.toDomain())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("WebSocket closing: $reason")
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("WebSocket failed: ${t.message}")
                if (!cont.isCompleted) cont.resume(Result.Error(t)) { cause, _, _ -> }
            }
        }

        client.newWebSocket(request, listener)

        cont.invokeOnCancellation {
            socket?.close(1000, "Cancelled")
        }
    }

    override suspend fun sendMessage(message: String) {
        socket?.send(message)
    }

    override fun getMessages(): SharedFlow<Message> {
        return messages
    }

    override suspend fun closeSession() {
        socket?.close(1000, "ended")
    }
}