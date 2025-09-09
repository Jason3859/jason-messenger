package dev.jason.app.compose.messenger.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jason.app.compose.messenger.ui.theme.MessengerTheme
import dev.jason.app.compose.messenger.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterChatroomScreen(
    username: String,
    uiState: ChatViewModel.ChatroomUiState,
    onChatroomIdChange: (String) -> Unit,
    onConnectClick: () -> Unit,
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
        val focusManager = LocalFocusManager.current

        BackHandler {
            onBack()
        }

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

            OutlinedTextField(
                value = uiState.chatroomId,
                onValueChange = onChatroomIdChange,
                placeholder = { Text("Enter chatroom id") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        if (uiState.chatroomId.isBlank()) {
                            Toast.makeText(context, "Chatroom Id cannot be empty", Toast.LENGTH_LONG).show()
                            return@KeyboardActions
                        }
                        if (uiState.chatroomId.contains(' ')) {
                            Toast.makeText(context, "Chatroom id cannot have spaces", Toast.LENGTH_SHORT).show()
                            return@KeyboardActions
                        }
                        focusManager.clearFocus()
                        onConnectClick()
                    }
                ),
                modifier = modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(25)
            )

            Spacer(modifier.height(15.dp))

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
                    focusManager.clearFocus()
                    onConnectClick()
                },
                modifier = modifier.fillMaxWidth(0.9f)
            ) {
                Text("Connect to chatroom ${uiState.chatroomId}")
            }
        }
    }
}

@Preview
@Composable
private fun ChatroomPreview() {
    MessengerTheme { 
        EnterChatroomScreen(
            username = "test",
            uiState = ChatViewModel.ChatroomUiState(),
            onChatroomIdChange = {  },
            onConnectClick = {  },
            onLogoutClick = {  },
            onBack = {  }
        )
    }
}