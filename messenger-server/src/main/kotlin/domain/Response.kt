package dev.jason.domain

sealed interface Response {
    data object Success : Response
    data object NotFound : Response
    data object InvalidPassword : Response

    data object UnableToDelete : Response

    data object UserAlreadyExists : Response
}