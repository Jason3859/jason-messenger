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
            Response.UserAlreadyExists
        }

        UsersDao.insert {
            it[username] = username
            it[password] = password
        }

        Response.Success
    }

    override suspend fun getAllUsers(): List<User> = dbQuery {
        UsersDao.selectAll().map {
            User(
                username = it[UsersDao.username],
                password = it[UsersDao.password],
            )
        }
    }

    override suspend fun findUser(username: String, password: String): Response {
        return try {
            val user = UsersDao.selectAll().map {
                User(
                    username = it[UsersDao.username],
                    password = it[UsersDao.password]
                )
            }.first { it.username == username }
            val isPwdValid = user.password == password
            if (isPwdValid) {
                Response.Success
            } else {
                Response.InvalidPassword
            }
        } catch (_: Exception) {
            Response.NotFound
        }
    }

    override suspend fun deleteUser(username: String, password: String): Response {
        return try {
            val user = findUser(username, password)
            if (user is Response.Success) {
                UsersDao.deleteWhere { UsersDao.username eq username }
                Response.Success
            } else {
                Response.InvalidPassword
            }
        } catch (_: Exception) {
            Response.UnableToDelete
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