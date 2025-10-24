package dev.jason.app.compose.messenger_app.auth_ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jason.app.compose.messenger_app.auth.Authentication
import dev.jason.app.compose.messenger_app.auth_ui.action.AuthAction
import dev.jason.app.compose.messenger_app.auth_ui.controller.NavigationController
import dev.jason.app.compose.messenger_app.auth_ui.controller.SnackbarController
import dev.jason.app.compose.messenger_app.auth_ui.route.AuthRoute
import dev.jason.app.compose.messenger_app.domain.AuthResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AuthViewModel(
    private val authentication: Authentication
) : ViewModel(CoroutineScope(Dispatchers.IO)) {

    data class UiState(
        val username: String = "",
        val password: String = "",
        val conformPassword: String = "",
        val showPassword: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun updateState(uiState: UiState) {
        _uiState.update { uiState }
    }

    fun onAction(action: AuthAction) {
        viewModelScope.launch {
            when (action) {
                AuthAction.LoginAction -> login()
                AuthAction.SigninAction -> signin()
            }
        }
    }

    private suspend fun login() {
        checkIfEmpty { return }

        NavigationController.navigate(AuthRoute.LoadingScreen("Logging in"), true)

        authentication.login(
            username = _uiState.value.username,
            password = _uiState.value.password
        ).apply {
            _uiState.update {
                if (this@apply is AuthResult.Success) {
                    // TODO: onDone()
                    return
                }

                if (this@apply is AuthResult.InvalidPassword) {
                    NavigationController.navigate(AuthRoute.LoginScreen, true)
                    SnackbarController.sendWarningDelayed(this@apply)
                    return
                }

                if (this@apply is AuthResult.NotFound) {
                    NavigationController.navigate(AuthRoute.LoginScreen, true)
                    SnackbarController.sendWarningDelayed(this@apply)
                    return
                }

                throw IllegalStateException()
            }
        }
    }

    private suspend fun signin() {
        checkIfEmpty { return }
        checkPassword { return }

        NavigationController.navigate(AuthRoute.LoadingScreen("Creating your account"), true)

        authentication.signin(
            username = _uiState.value.username,
            password = _uiState.value.password
        ).apply {
            _uiState.update {
                if (this@apply is AuthResult.Success) {
                    // TODO: onDone()
                    login()
                    return
                }

                if (this@apply is AuthResult.UserAlreadyExists) {
                    NavigationController.navigate(AuthRoute.SigninScreen, true)
                    SnackbarController.sendWarningDelayed(this@apply)
                    return
                }

                throw IllegalStateException()
            }
        }
    }

    private suspend inline fun checkIfEmpty(onEmpty: () -> Unit) {
        val uiState = _uiState.value

        if (uiState.username == "" || uiState.password == "") {
            SnackbarController.sendFieldsCannotBeEmpty()
            onEmpty()
        }
    }

    private suspend inline fun checkPassword(onFail: () -> Unit) {
        val uiState = _uiState.value

        if (uiState.password != uiState.conformPassword) {
            SnackbarController.sendPasswordsDidNotMatch()
            onFail()
        }
    }
}