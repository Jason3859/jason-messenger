package dev.jason.app.compose.messenger.ui.screen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel

@Composable
fun LoginScreen(
    uiState: MainViewModel.LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSigninClick: () -> Unit,
    onLoggedIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
                    onDone = { onLoginClick() }
                )
            )

            Button(
                onClick = {
                    if (uiState.username.isBlank() && uiState.password.isBlank()) {
                        Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (uiState.username.isBlank()) {
                        Toast.makeText(context, "Username cannot be empty", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (uiState.password.isBlank()) {
                        Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    onLoginClick()
                }
            ) {
                Text("Login")
            }

            Button(onSigninClick) {
                Text("First time? Signin instead")
            }

            if (uiState.isSuccessful) {
                println("logged in")
                onLoggedIn()
            }
        }
    }
}