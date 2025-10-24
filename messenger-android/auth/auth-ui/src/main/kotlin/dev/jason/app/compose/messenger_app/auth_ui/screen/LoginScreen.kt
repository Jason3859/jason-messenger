package dev.jason.app.compose.messenger_app.auth_ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jason.app.compose.messenger_app.auth_ui.action.AuthAction
import dev.jason.app.compose.messenger_app.auth_ui.controller.NavigationController
import dev.jason.app.compose.messenger_app.auth_ui.route.AuthRoute
import dev.jason.app.compose.messenger_app.auth_ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginScreen(
    uiState: AuthViewModel.UiState,
    updateState: (AuthViewModel.UiState) -> Unit,
    onAction: (AuthAction) -> Unit
) {

    CoreAuthScreen(
        buttonText = "Login",
        uiState = uiState,
        buttonAction = AuthAction.LoginAction,
        onAction = onAction,
        updateState = updateState,
        extraButton = {
            Button(
                onClick = {
                    NavigationController.navigate(AuthRoute.SigninScreen)
                },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text("First time? Signin instead")
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text("Welcome Back!")
                }
            )
        },
        modifier = Modifier
            .size(width = 400.dp, height = 300.dp)
    )
}