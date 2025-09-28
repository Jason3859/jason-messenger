package dev.jason.project.ktor.messenger.data.database

import org.jetbrains.exposed.v1.core.Table

object Dao {
    object MessagesDao : Table("messages") {
        override val primaryKey: PrimaryKey?
            get() = PrimaryKey(id)
        val id = long("id").autoIncrement()
        val chatroomid = text("chatroomid")
        val sender = text("sender")
        val text = text("text")
        val timestamp = long("timestamp")
    }

    object UsersDao : Table("users") {
        override val primaryKey: PrimaryKey?
            get() = PrimaryKey(id)
        val id = long("id").autoIncrement()
        val username = text("username")
        val password = text("password")
    }
}
