package dev.jason.project.ktor.messenger.data.database

import dev.jason.project.ktor.messenger.domain.User
import dev.jason.project.ktor.messenger.domain.UserRepository
import dev.jason.project.ktor.messenger.domain.Result
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.collections.forEach

class UsersSupabaseDB : UserRepository {
    override suspend fun addUser(user: User): Result = dbQuery {
        val users = getAllUsers()

        var alreadyExists = false

        users.forEach {
            if (it.username == user.username) {
                alreadyExists = true
            }
        }

        if (alreadyExists) {
            Result.UserAlreadyExists()
        } else {
            UsersDao.insert {
                it[username] = user.username
                it[password] = user.password
            }

            Result.Success()
        }
    }

    override suspend fun getAllUsers(): List<User> = dbQuery {
        UsersDao.selectAll().map {
            User(
                username = it[UsersDao.username],
                password = it[UsersDao.password],
            )
        }
    }

    override suspend fun findUser(user: User): Result = dbQuery {
        try {
            val users = UsersDao.selectAll().map {
                User(
                    username = it[UsersDao.username],
                    password = it[UsersDao.password]
                )
            }
            var isPasswordValid = false
            var userFound = false

            users.forEach {
                if (it.username == user.username) {
                    userFound = true
                    if (it.password == user.password) {
                        isPasswordValid = true
                    }
                }
            }

            if (userFound) {
                if (isPasswordValid) {
                    Result.Success()
                } else {
                    Result.InvalidPassword()
                }
            } else {
                Result.NotFound()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.NotFound()
        }
    }

    override suspend fun deleteUser(user: User): Result = dbQuery {
        try {
            val user = findUser(user)
            if (user is Result.Success) {
                UsersDao.deleteWhere { UsersDao.username eq username }
                Result.Success()
            } else {
                Result.InvalidPassword()
            }
        } catch (_: Exception) {
            Result.UnableToDelete()
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