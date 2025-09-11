package dev.jason.app.compose.desktop.messenger.domain.model

sealed interface Result {
    data object Success : Result
    data object UserNotFound : Result
    data object InvalidPassword : Result
    data object UserAlreadyExists : Result
    data class Error(val error: Throwable?, val message: String? = null) : Result
}