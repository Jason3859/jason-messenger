package dev.jason.app.compose.messenger_app.auth_ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.jason.app.compose.messenger_app.auth_ui.R
import dev.jason.app.compose.messenger_app.auth_ui.action.AuthAction
import dev.jason.app.compose.messenger_app.auth_ui.controller.SnackbarController
import dev.jason.app.compose.messenger_app.auth_ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CoreAuthScreen(
    buttonAction: AuthAction,
    onAction: (AuthAction) -> Unit,
    buttonText: String,
    uiState: AuthViewModel.UiState,
    updateState: (AuthViewModel.UiState) -> Unit,
    topBar: @Composable () -> Unit,
    extraButton: (@Composable () -> Unit)? = null,
    extraTextField: (@Composable () -> Unit)? = null,
    usernameTextFieldPlaceHolder: String = "Enter your username",
    passwordTextFieldPlaceHolder: String = "Enter your password",
    @Suppress("ModifierParameter") modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val action = {
        focusManager.clearFocus()
        onAction(buttonAction)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        SnackbarController.events.collect { event ->
            snackbarHostState.showSnackbar(event)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = topBar,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .testTag(stringResource(R.string.snackbar_tag))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = modifier
                    .padding(16.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = { updateState(uiState.copy(username = it)) },
                        placeholder = { Text(usernameTextFieldPlaceHolder) },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(percent = 25),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .testTag(stringResource(R.string.username_text_field))
                    )

                    Spacer(Modifier.height(15.dp))

                    if (extraTextField != null) {
                        extraTextField.invoke()
                        Spacer(Modifier.height(15.dp))
                    }

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = { updateState(uiState.copy(password = it)) },
                        placeholder = { Text(passwordTextFieldPlaceHolder) },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        singleLine = true,
                        visualTransformation = if (uiState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardActions = KeyboardActions(
                            onDone = { action.invoke() }
                        ),
                        shape = RoundedCornerShape(percent = 25),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .testTag(stringResource(R.string.password_text_field)),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    updateState(uiState.copy(showPassword = !uiState.showPassword))
                                }
                            ) {
                                Icon(
                                    painter = painterResource(if (uiState.showPassword) R.drawable.visibility_off else R.drawable.visibility),
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(15.dp))

                    Button(
                        onClick = action,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .testTag(stringResource(R.string.action_button))
                    ) {
                        Text(buttonText)
                    }

                    extraButton?.invoke()
                }
            }
        }
    }
}