package dev.jason.domain

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun addUser(user: User): Result
    suspend fun findUser(user: User): Result
    suspend fun deleteUser(user: User): Result
}