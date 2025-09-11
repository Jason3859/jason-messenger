package dev.jason.app.compose.desktop.messenger.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.jason.app.compose.desktop.messenger.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterRoomIdScreen(
    username: String,
    uiState: ChatViewModel.ChatroomUiState,
    onChatroomIdChange: (String) -> Unit,
    onConnectClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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

            OutlinedTextField(
                value = uiState.roomId,
                onValueChange = onChatroomIdChange,
                placeholder = { Text("Enter chatroom id") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        onConnectClick()
                    }
                ),
                modifier = modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(25)
            )

            Spacer(modifier.height(15.dp))

            Button(
                onClick = {
                    onConnectClick()
                },
                modifier = modifier.fillMaxWidth(0.9f)
            ) {
                Text("Connect to chatroom ${uiState.roomId}")
            }
        }
    }
}