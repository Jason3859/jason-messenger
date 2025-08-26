package dev.jason.domain

sealed interface UsersResponse {
    data object Success : UsersResponse
    data object NotFound : UsersResponse
    data object InvalidPassword : UsersResponse

    data object UnableToDelete : UsersResponse

    data object UserAlreadyExists : UsersResponse
}