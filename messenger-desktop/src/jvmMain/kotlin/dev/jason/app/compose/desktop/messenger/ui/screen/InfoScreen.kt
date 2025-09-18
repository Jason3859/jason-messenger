package dev.jason.app.compose.desktop.messenger.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jason.app.compose.desktop.messenger.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    uiState: MainViewModel.ChatroomUiState,
    onBackClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onDeleteChatroomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deleteAccountClicked = remember { mutableStateOf(false) }
    val deleteChatroomClicked = remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Connected to ${uiState.roomId}") },
                navigationIcon = {
                    IconButton(onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            mapOf(
                "Delete Chatroom ${uiState.roomId}" to { deleteChatroomClicked.value = true },
                "Delete Account" to { deleteAccountClicked.value = true }
            ).forEach {
                item {
                    TextButton(
                        onClick = it.value,
                        modifier = modifier
                            .fillParentMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = it.key,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        if (deleteAccountClicked.value) {
            Dialog(
                onConform = onDeleteAccountClick,
                toDelete = "Account",
                state = deleteAccountClicked
            )
        }

        if (deleteChatroomClicked.value) {
            Dialog(
                onConform = onDeleteChatroomClick,
                toDelete = "Chatroom",
                state = deleteChatroomClicked,
                description = "Deleting chatroom deletes all the messages of this chatroom.\nPlease ask others on this chatroom to exit to avoid bugs.\nIt will be fixed in upcoming versions of the app"
            )
        }

        if (uiState.isChatroomDeleteSuccessful || uiState.isAccountDeleteSuccessful) {
            onBackClick()
        }
    }
}

@Composable
private fun Dialog(
    onConform: () -> Unit,
    toDelete: String,
    state: MutableState<Boolean>,
    description: String? = null,
) {
    AlertDialog(
        onDismissRequest = { state.value = false },
        confirmButton = {
            Button(onConform, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Delete")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { state.value = false }
            ) {
                Text("Cancel")
            }
        },
        title = {
            Text("Conform delete $toDelete?")
        },
        text = {
            Text(description ?: "This cannot be undone")
        }
    )
}