package dev.jason.project.ktor.messenger.data.database

import dev.jason.project.ktor.messenger.domain.Result
import dev.jason.project.ktor.messenger.domain.User
import dev.jason.project.ktor.messenger.domain.UserRepository
import java.sql.Connection

class UsersDatabaseRepository(private val connection: Connection) : UserRepository {

    init {
        connection.createStatement().use {
            it.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    username TEXT NOT NULL,
                    password TEXT NOT NULL
                );
            """.trimIndent())
        }
    }

    override suspend fun getAllUsers(): List<User> {
        val list = mutableListOf<User>()

        connection.createStatement().use {
            val result = it.executeQuery("SELECT * FROM users")

            while (result.next()) {
                list.add(
                    User(
                        username = result.getString("username"),
                        password = result.getString("password")
                    )
                )
            }
        }

        return list
    }

    override suspend fun addUser(user: User): Result {
        val existing = getAllUsers()
        var usernameAlreadyExists = false

        existing.forEach { existingUser ->
            if (existingUser.username == user.username) {
                usernameAlreadyExists = true
            }
        }

        if (usernameAlreadyExists) {
            return Result.UserAlreadyExists()
        }

        connection.prepareStatement("""
            INSERT INTO users (username, password) VALUES (?, ?)
        """.trimIndent()).use {
            it.apply {
                setString(1, user.username)
                setString(2, user.password)
                executeUpdate()
            }
        }

        return Result.Success()
    }

    override suspend fun findUser(user: User): Result {
        val users = getAllUsers()
        var found = false
        var isPasswordValid = false

        users.forEach {
            if (it.username == user.username) {
                found = true
                if (it.password == user.password) {
                    isPasswordValid = true
                }
            }
        }

        return if (found) {
            if (isPasswordValid) Result.Success()
            else Result.InvalidPassword()
        } else Result.NotFound()
    }

    override suspend fun deleteUser(user: User): Result {
        var deletionResult: Result = Result.Success()

        connection.prepareStatement("""
            DELETE FROM users WHERE username = ?
        """.trimIndent()).use {
            it.apply {
                val result = findUser(user)

                if (result is Result.Success) {
                    setString(1, user.username)
                    executeUpdate()
                } else if (result is Result.InvalidPassword) {
                    deletionResult = Result.InvalidPassword()
                } else deletionResult = Result.NotFound()
            }
        }

        return deletionResult
    }
}