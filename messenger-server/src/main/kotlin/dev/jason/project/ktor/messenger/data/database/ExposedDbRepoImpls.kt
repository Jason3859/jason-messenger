@file:Suppress("DEPRECATION")

package dev.jason.project.ktor.messenger.data.database

import dev.jason.project.ktor.messenger.data.database.Dao.MessagesDao
import dev.jason.project.ktor.messenger.data.database.Dao.UsersDao
import dev.jason.project.ktor.messenger.data.model.MessageDto
import dev.jason.project.ktor.messenger.data.model.toDomain
import dev.jason.project.ktor.messenger.data.model.toLong
import dev.jason.project.ktor.messenger.domain.db.MessagesDatabaseRepository
import dev.jason.project.ktor.messenger.domain.db.UsersDatabaseRepository
import dev.jason.project.ktor.messenger.domain.model.Message
import dev.jason.project.ktor.messenger.domain.model.Result
import dev.jason.project.ktor.messenger.domain.model.User
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

object ExposedDbRepoImpls {
    class ExposedUsersDbRepo : UsersDatabaseRepository {
        override suspend fun addUser(user: User): Result = dbQuery {
            val users = getAllUsers()

            var alreadyExists = false

            users.forEach {
                if (it.username == user.username) {
                    alreadyExists = true
                }
            }

            if (alreadyExists) {
                Result.UserAlreadyExists
            } else {
                UsersDao.insert {
                    it[username] = user.username
                    it[password] = user.password
                }

                Result.Success
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
                        Result.Success
                    } else {
                        Result.InvalidPassword
                    }
                } else {
                    Result.NotFound
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.NotFound
            }
        }

        override suspend fun deleteUser(user: User): Result = dbQuery {
            try {
                val user = findUser(user)
                if (user is Result.Success) {
                    UsersDao.deleteWhere { UsersDao.id eq id }
                    Result.Success
                } else {
                    Result.InvalidPassword
                }
            } catch (_: Exception) {
                Result.UnableToDelete
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

    class ExposedMessagesDbRepo : MessagesDatabaseRepository {
        override suspend fun getAllMessages(): List<Message> = dbQuery {
            MessagesDao.selectAll().map { row ->
                MessageDto(
                    id = row[MessagesDao.id],
                    chatRoomId = row[MessagesDao.chatroomid],
                    sender = row[MessagesDao.sender],
                    message = row[MessagesDao.text],
                    timestamp = row[MessagesDao.timestamp]
                ).toDomain()
            }
        }

        override suspend fun addMessage(message: Message): Unit = dbQuery {
            MessagesDao.insert {
                it[chatroomid] = message.chatRoomId
                it[sender] = message.sender
                it[text] = message.message
                it[timestamp] = message.timestamp.toLong()
            }
        }

        override suspend fun deleteChatRoom(chatroomID: String): Result {
            return try {
                val chatroom = getAllMessages().map { it.chatRoomId }
                if (!chatroom.contains(chatroomID)) {
                    Result.NotFound
                }
                MessagesDao.deleteWhere { MessagesDao.chatroomid eq chatroomID }
                Result.Success
            } catch (_: Exception) {
                Result.UnableToDelete
            }
        }

        private suspend fun <T> dbQuery(block: suspend () -> T): T {
            return newSuspendedTransaction(Dispatchers.IO) {
                block()
            }
        }
    }
}
