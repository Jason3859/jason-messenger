package dev.jason.app.compose.messenger.ui.util

import dev.jason.app.compose.messenger.domain.model.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackbarController {

    private val _events = MutableSharedFlow<String?>()
    val events = _events.asSharedFlow()

    suspend fun sendEvent(result: Result) {
        val value = when (result) {
            is Result.UserAlreadyExists -> "User with that username already exists. Try another one."
            is Result.InvalidPassword -> "Password is invalid!"
            is Result.Success -> "Success"
            is Result.UserNotFound -> "User with that username not found. Try signing in."
            is Result.Error -> {
                if (result.error?.message?.contains("host is known") == true) {
                    "No Internet"
                } else {
                    result.message
                }
            }
        }

        _events.emit(value)
    }
}