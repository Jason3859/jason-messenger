@file:OptIn(ExperimentalMaterial3Api::class)

package dev.jason.app.compose.messenger.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Welcome back!") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var showPassword by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                placeholder = { Text("Enter your username") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                singleLine = true,
                modifier = modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(percent = 25)
            )

            Spacer(modifier.height(15.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                placeholder = { Text("Enter your password") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardActions = KeyboardActions(
                    onDone = { onLoginClick() }
                ),
                modifier = modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(percent = 25),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            showPassword = !showPassword
                        }
                    ) {
                        Icon(
                            imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier.height(15.dp))

            Button(
                onClick = {
                    if (uiState.username.isBlank() && uiState.password.isBlank()) {
//                        Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (uiState.username.isBlank()) {
//                        Toast.makeText(context, "Username cannot be empty", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (uiState.password.isBlank()) {
//                        Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    onLoginClick()
                },
                modifier = modifier.fillMaxWidth(0.9f)
            ) {
                Text("Login")
            }

            Button(
                onClick = onSigninClick,
                modifier = modifier.fillMaxWidth(0.9f)
            ) {
                Text("First time? Signin instead")
            }

            if (uiState.isSuccessful) {
                println("logged in")
                onLoggedIn()
            }
        }
    }
}