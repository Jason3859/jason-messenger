package dev.jason.project.ktor.messenger.domain

import kotlinx.serialization.Serializable

sealed interface Result {
    @Serializable data class Success(val message: String = "Success") : Result
    @Serializable data class NotFound(val message: String = "NotFound") : Result
    @Serializable data class InvalidPassword(val message: String = "InvalidPassword") : Result
    @Serializable data class UnableToDelete(val message: String = "UnableToDelete") : Result
    @Serializable data class UserAlreadyExists(val message: String = "UserAlreadyExists") : Result
}