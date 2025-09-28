package dev.jason.project.ktor.messenger.domain.model

import kotlinx.serialization.Serializable

sealed interface Result {
    @Serializable data object Success : Result
    @Serializable data object NotFound : Result
    @Serializable data object InvalidPassword : Result
    @Serializable data object UserAlreadyExists : Result
    @Serializable object UnableToDelete : Result
}