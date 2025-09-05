package dev.jason.app.compose.messenger.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.jason.app.compose.messenger.ui.viewmodel.ChatViewModel
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterChatroomScreen(
    username: String,
    uiState: ChatViewModel.ChatroomUiState,
    onChatroomIdChange: (String) -> Unit,
    onConnectClick: () -> Unit,
    onConnect: () -> Unit,
    onLogoutClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Logged in as $username") },
                actions = {
                    TextButton(
                        onClick = onLogoutClick
                    ) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enter an unique id for your chatroom.")
            Text("Share this with the one you want to chat with.")

            Spacer(modifier.height(30.dp))

            TextField(
                value = uiState.chatroomId,
                onValueChange = onChatroomIdChange,
                placeholder = { Text("Enter chatroom id") }
            )

            Spacer(modifier.height(10.dp))

            Button(
                onClick = {
                    if (uiState.chatroomId.isBlank()) {
                        Toast.makeText(context, "Chatroom Id cannot be empty", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (uiState.chatroomId.contains(' ')) {
                        Toast.makeText(context, "Chatroom id cannot have spaces", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    Toast.makeText(context, "Connecting to ${uiState.chatroomId}", Toast.LENGTH_SHORT).show()
                    onConnectClick()
                }
            ) {
                Text("Connect to chatroom ${uiState.chatroomId}")
            }
        }

        BackHandler {
            onBack()
        }
    }

    if (uiState.isSuccessful) {
        onConnect()
    }
}