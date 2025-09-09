package dev.jason.app.compose.messenger.domain.model

sealed interface Result {
    data object Success : Result
    data object NotFound : Result
    data object InvalidPassword : Result
    data object UserAlreadyExists : Result
    data class Error(val error: Throwable) : Result
}