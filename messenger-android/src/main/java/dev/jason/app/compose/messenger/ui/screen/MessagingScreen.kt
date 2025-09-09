package dev.jason.app.compose.messenger.ui.screen

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jason.app.compose.messenger.ui.model.MessageUi
import dev.jason.app.compose.messenger.ui.theme.MessengerTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    messages: List<MessageUi>,
    chatroomId: String,
    message: String,
    onMessageValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onInfoClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

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
                            focusManager.clearFocus()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null)
                    }
                },
                placeholder = {
                    Text("Message...")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        focusManager.clearFocus()
                        onSend()
                    }
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .padding(bottom = 15.dp)
                    .padding(horizontal = 8.dp),
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        val scrollState = rememberLazyListState()

        LaunchedEffect(messages) {
            delay((0.1).seconds)
            if (scrollState.isScrollInProgress) {
                return@LaunchedEffect
            }
            if (messages.isEmpty()) {
                return@LaunchedEffect
            }
            scrollState.animateScrollToItem(messages.lastIndex)
        }

        BackHandler {
            onBack()
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = scrollState
        ) {
            items(messages) { message ->
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
        }
    }
}

private val messages = List(5) { id ->
    MessageUi(
        id = id.toLong(),
        chatRoomId = "test",
        sender = "jason",
        text = "message",
        timestamp = "01/01/0001 12:00 AM"
    )
}

@Preview(device = "spec:width=1080px,height=2340px,dpi=440,isRound=true", wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MessageScreenPreview() {
    MessengerTheme {
        MessagingScreen(
            messages = messages,
            chatroomId = "test",
            message = "",
            onMessageValueChange = {  },
            onSend = {  },
            onInfoClick = {  },
            onBack = {  }
        )
    }
}