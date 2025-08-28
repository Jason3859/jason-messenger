package dev.jason.app.compose.messenger.domain.api

sealed interface Result {
    data object Success : Result
    data object NotFound : Result
    data object InvalidPassword : Result
    data object UnableToDelete : Result
    data object UserAlreadyExists : Result
    data class Error(val message: String) : Result
}