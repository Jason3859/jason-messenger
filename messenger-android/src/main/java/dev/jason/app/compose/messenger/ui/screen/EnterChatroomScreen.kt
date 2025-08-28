package dev.jason.app.compose.messenger.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterChatroomScreen(
    chatroomId: String,
    onChatroomIdChange: (String) -> Unit,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Enter Chatroom id") }
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
            TextField(
                value = chatroomId,
                onValueChange = onChatroomIdChange
            )

            Button(
                onClick = {
                    if (chatroomId.isBlank()) {
                        Toast.makeText(context, "Chatroom Id cannot be empty", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    onConnectClick()
                }
            ) {
                Text("Connect to chatroom $chatroomId")
            }
        }
    }
}