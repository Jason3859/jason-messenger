package dev.jason.data.database

import dev.jason.domain.User
import dev.jason.domain.UserRepository
import dev.jason.domain.Response
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UsersSupabaseDB : UserRepository {
    override suspend fun addUser(user: User): Response = dbQuery {
        val users = getAllUsers()

        if (users.contains(user)) {
            Response.UserAlreadyExists()
        }

        UsersDao.insert {
            it[username] = user.username
            it[password] = user.password
        }

        Response.Success()
    }

    override suspend fun getAllUsers(): List<User> = dbQuery {
        UsersDao.selectAll().map {
            User(
                username = it[UsersDao.username],
                password = it[UsersDao.password],
            )
        }
    }

    override suspend fun findUser(user: User): Response = dbQuery {
        try {
            val users = UsersDao.selectAll().map {
                User(
                    username = it[UsersDao.username],
                    password = it[UsersDao.password]
                )
            }
            var isPasswordValid = false

            users.forEach {
                if (it.username == user.username) {
                    if (it.password == user.password) {
                        isPasswordValid = true
                    }
                }
            }

            if (isPasswordValid) {
                Response.Success()
            } else {
                Response.InvalidPassword()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Response.NotFound()
        }
    }

    override suspend fun deleteUser(user: User): Response = dbQuery {
        try {
            val user = findUser(user)
            if (user is Response.Success) {
                UsersDao.deleteWhere { UsersDao.username eq username }
                Response.Success()
            } else {
                Response.InvalidPassword()
            }
        } catch (_: Exception) {
            Response.UnableToDelete()
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