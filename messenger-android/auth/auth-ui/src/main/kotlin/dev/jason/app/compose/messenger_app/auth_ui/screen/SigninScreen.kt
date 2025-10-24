package dev.jason.app.compose.messenger_app.auth_ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.jason.app.compose.messenger_app.auth_ui.R
import dev.jason.app.compose.messenger_app.auth_ui.action.AuthAction
import dev.jason.app.compose.messenger_app.auth_ui.controller.NavigationController
import dev.jason.app.compose.messenger_app.auth_ui.route.AuthRoute
import dev.jason.app.compose.messenger_app.auth_ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SigninScreen(
    uiState: AuthViewModel.UiState,
    updateState: (AuthViewModel.UiState) -> Unit,
    onAction: (AuthAction) -> Unit
) {

    CoreAuthScreen(
        buttonText = "Signin",
        uiState = uiState,
        updateState = updateState,
        passwordTextFieldPlaceHolder = "Re-enter your password",
        extraTextField = {
            OutlinedTextField(
                value = uiState.conformPassword,
                onValueChange = { updateState(uiState.copy(conformPassword = it)) },
                placeholder = { Text("Enter your password") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(percent = 25),
                visualTransformation = if (uiState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
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
        },
        buttonAction = AuthAction.SigninAction,
        onAction = onAction,
        topBar = {
            TopAppBar(
                title = {
                    Text("Create a new account")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            NavigationController.navigate(AuthRoute.LoginScreen, true)
                        }
                    ) {
                        Icon(painterResource(R.drawable.arrow_back), null)
                    }
                }
            )
        },
        modifier = Modifier
            .size(width = 400.dp, height = 320.dp)
    )
}