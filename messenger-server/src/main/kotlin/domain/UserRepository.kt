package dev.jason.domain

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun addUser(user: User): Response
    suspend fun findUser(username: String, password: String): Response
    suspend fun deleteUser(username: String, password: String): Response
}