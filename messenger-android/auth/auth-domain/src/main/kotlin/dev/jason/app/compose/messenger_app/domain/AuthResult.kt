package dev.jason.app.compose.messenger_app.domain

sealed interface AuthResult {
    data object Success : AuthResult
    data object NotFound : AuthResult
    data object NoInternet : AuthResult
    data object InvalidPassword : AuthResult
    data object UserAlreadyExists : AuthResult
}