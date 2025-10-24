package dev.jason.app.compose.messenger_app.auth_ui.controller

import dev.jason.app.compose.messenger_app.domain.AuthResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackbarController {

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    suspend fun sendWarningDelayed(warning: AuthResult, delayTimeMillis: Long = 500) {
        delay(delayTimeMillis)
        _events.emit(warning.toMessage())
    }

    suspend fun sendFieldsCannotBeEmpty() {
        _events.emit("Fields cannot be empty")
    }

    suspend fun sendPasswordsDidNotMatch() {
        _events.emit("Passwords did not match")
    }

    private fun AuthResult.toMessage(): String {
        return when (this) {
            AuthResult.InvalidPassword -> "Invalid Password. Try again"
            AuthResult.NoInternet -> "No Internet"
            AuthResult.NotFound -> "User with that name not found. Try signing in"
            AuthResult.UserAlreadyExists -> "User with that name already exists. Try another one"
            AuthResult.Success -> throw IllegalStateException()
        }
    }
}