package dev.jason.app.compose.desktop.messenger.ui.screen

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jason.app.compose.desktop.messenger.ui.model.MessageUi
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
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Connected to $chatroomId") },
                actions = {
                    TextButton(onDisconnectClick) {
                        Text("Disconnect", color = MaterialTheme.colorScheme.error)
                    }
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
                            onSend()
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