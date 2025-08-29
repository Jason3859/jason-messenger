package dev.jason.app.compose.messenger.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel

@Composable
fun SigninScreen(
    uiState: MainViewModel.LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSigninClick: () -> Unit,
    onSignedIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                placeholder = { Text("Enter your username") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                singleLine = true
            )

            TextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                placeholder = { Text("Enter your password") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = { onSigninClick() }
                )
            )

            Button(
                onClick = onSigninClick
            ) {
                Text("Signin")
            }

            if (uiState.isSuccessful) {
                onSignedIn()
            }
        }
    }
}