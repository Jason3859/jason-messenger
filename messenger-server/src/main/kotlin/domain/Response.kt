package dev.jason.domain

import kotlinx.serialization.Serializable

sealed interface Response {
    @Serializable data class Success(val message: String = "Success") : Response
    @Serializable data class NotFound(val message: String = "NotFound") : Response
    @Serializable data class InvalidPassword(val message: String = "InvalidPassword") : Response
    @Serializable data class UnableToDelete(val message: String = "UnableToDelete") : Response
    @Serializable data class UserAlreadyExists(val message: String = "UserAlreadyExists") : Response
}