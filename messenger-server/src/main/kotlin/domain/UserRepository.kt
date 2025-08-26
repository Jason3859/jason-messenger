package dev.jason.domain

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun addUser(user: User): UsersResponse
    suspend fun findUser(username: String, password: String): UsersResponse
    suspend fun deleteUser(username: String, password: String): UsersResponse
}