package dev.jason.app.compose.messenger.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.jason.app.compose.messenger.R
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel

@Composable
fun SigninScreen(
    uiState: MainViewModel.LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSigninClick: () -> Unit,
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
                            painter = painterResource(
                                if (showPassword) R.drawable.ic_visibility_off else R.drawable.ic_visibility_on
                            ),
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
                            painter = painterResource(
                                if (showPassword) R.drawable.ic_visibility_off else R.drawable.ic_visibility_on
                            ),
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier.height(15.dp))

            Button(
                onClick = {
                    if (password != uiState.password) {
                        Toast.makeText(
                            context,
                            "Passwords did not match",
                            Toast.LENGTH_LONG
                        ).show()
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