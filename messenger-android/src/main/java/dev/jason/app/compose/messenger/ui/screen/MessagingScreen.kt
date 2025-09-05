package dev.jason.app.compose.messenger.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jason.app.compose.messenger.ui.MessageUi
import dev.jason.app.compose.messenger.ui.toUi
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    messages: List<MessageUi>,
    chatroomId: String,
    message: String,
    onMessageValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusRequester = LocalFocusManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Connected to $chatroomId") },
                actions = {
                    IconButton(onClick = onInfoClick) {
                        Icon(Icons.Default.Info, null)
                    }
                }
            )
        },
        bottomBar = {
            OutlinedTextField(
                value = message,
                onValueChange = onMessageValueChange,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (message.isBlank()) {
                                Toast.makeText(context, "Message cannot be empty.", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }
                            onSend()
                            focusRequester.clearFocus()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { onSend() }
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.navigationBars.asPaddingValues())
            )
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        MessagesList(
            messages, modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
private fun MessagesList(
    messages: List<MessageUi>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(messages) {
            Message(it)
        }
    }
}

@Composable
private fun Message(
    message: MessageUi
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(message.sender, fontSize = 15.sp)
        Text(message.text, fontSize = 20.sp)
        Row(Modifier.fillMaxWidth(), Arrangement.End) {
            Text(message.timestamp, fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    device = "spec:width=1280px,height=2856px,dpi=480,isRound=true,navigation=buttons",
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
)
@Composable
private fun MessagePrev() {
    MessagingScreen(
        messages = listOf(
            MessageUi(
                id = 1,
                chatRoomId = "example",
                sender = "jason",
                text = "text",
                timestamp = LocalDateTime.of(
                    2024,
                    0,
                    2,
                    13,
                    12,
                    30
                ).toUi()
            ),
            MessageUi(
                id = 1,
                chatRoomId = "example",
                sender = "jason",
                text = "text",
                timestamp = LocalDateTime.of(
                    2024,
                    0,
                    2,
                    13,
                    12,
                    30
                ).toUi()
            ),
            MessageUi(
                id = 1,
                chatRoomId = "example",
                sender = "jason",
                text = "text",
                timestamp = LocalDateTime.of(
                    2024,
                    0,
                    2,
                    13,
                    12,
                    30
                ).toUi()
            ),
        ),
        chatroomId = "example",
        message = "message",
        onMessageValueChange = { TODO() },
        onSend = { TODO() },
        onInfoClick = { TODO() },
    )
}