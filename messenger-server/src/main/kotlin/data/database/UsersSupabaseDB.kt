package dev.jason.data.database

import dev.jason.domain.User
import dev.jason.domain.UserRepository
import dev.jason.domain.UsersResponse
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UsersSupabaseDB : UserRepository {
    override suspend fun addUser(user: User): UsersResponse = dbQuery {
        val users = getAllUsers()

        if (users.contains(user)) {
            UsersResponse.UserAlreadyExists
        }

        UsersDao.insert {
            it[username] = username
            it[password] = password
        }

        UsersResponse.Success
    }

    override suspend fun getAllUsers(): List<User> = dbQuery {
        UsersDao.selectAll().map {
            User(
                username = it[UsersDao.username],
                password = it[UsersDao.password],
            )
        }
    }

    override suspend fun findUser(username: String, password: String): UsersResponse {
        return try {
            val user = UsersDao.selectAll().map {
                User(
                    username = it[UsersDao.username],
                    password = it[UsersDao.password]
                )
            }.first { it.username == username }
            val isPwdValid = user.password == password
            if (isPwdValid) {
                UsersResponse.Success
            } else {
                UsersResponse.InvalidPassword
            }
        } catch (_: Exception) {
            UsersResponse.NotFound
        }
    }

    override suspend fun deleteUser(username: String, password: String): UsersResponse {
        return try {
            val user = findUser(username, password)
            if (user is UsersResponse.Success) {
                UsersDao.deleteWhere { UsersDao.username eq username }
                UsersResponse.Success
            } else {
                UsersResponse.InvalidPassword
            }
        } catch (_: Exception) {
            UsersResponse.UnableToDelete
        }
    }

    private suspend inline fun <T> dbQuery(
        noinline block: suspend () -> T
    ): T {
        return newSuspendedTransaction(Dispatchers.IO) {
            block()
        }
    }
}