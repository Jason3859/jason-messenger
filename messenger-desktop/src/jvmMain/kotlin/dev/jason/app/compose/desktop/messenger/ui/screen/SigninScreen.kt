package dev.jason.app.compose.desktop.messenger.ui.screen

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
import dev.jason.app.compose.desktop.messenger.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigninScreen(
    uiState: MainViewModel.LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSigninClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Create a new account") },
                navigationIcon = {
                    IconButton(onBack) {

                    }
                }
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

            var password by remember { mutableStateOf("") }

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
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter your password") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                singleLine = true,
                modifier = modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(percent = 25),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
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

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                placeholder = { Text("Reenter your password") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardActions = KeyboardActions(
                    onDone = { onSigninClick() }
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
                    if (password != uiState.password) {
//                        Toast.makeText(
//                            context,
//                            "Passwords did not match",
//                            Toast.LENGTH_LONG
//                        ).show()
                        return@Button
                    }

                    onSigninClick()
                },
                modifier = modifier.fillMaxWidth(0.9f)
            ) {
                Text("Signin")
            }
        }
    }
}