package dev.jason.project.ktor.messenger.domain.db

import dev.jason.project.ktor.messenger.domain.model.Result
import dev.jason.project.ktor.messenger.domain.model.User

interface UsersDatabaseRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun addUser(user: User): Result
    suspend fun findUser(user: User): Result
    suspend fun deleteUser(user: User): Result
}