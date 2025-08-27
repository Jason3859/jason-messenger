package dev.jason.domain

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun addUser(user: User): Response
    suspend fun findUser(user: User): Response
    suspend fun deleteUser(user: User): Response
}