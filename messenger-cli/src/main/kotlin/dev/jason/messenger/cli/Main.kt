package dev.jason.messenger.cli

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch

suspend fun main() {
    val client = HttpClient(CIO) { install(WebSockets) }

    print("Enter name: ")
    val name = readln()

    client.webSocket(
        method = HttpMethod.Get,
        host = "jason-messenger-production.up.railway.app",
        port = 443,
        path = "chat/123",
        request = {
            url.protocol = URLProtocol.WSS
            parameter("userId", name)
        }
    ) {
        launch {
            while (true) {
                print("Message: ")
                val message = readlnOrNull() ?: return@launch
                if (message.equals("exit", true)) {
                    break
                }
                send(Frame.Text(message))
            }
        }

        launch {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    println("$name: ${frame.readText()}")
                }
            }
        }
    }
}