package dev.jason.project.ktor.messenger.data.database

import org.jetbrains.exposed.sql.Table

object MessagesDao : Table() {
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
    val id = long("id").autoIncrement()
    val chatRoomID = text("chatRoomID")
    val sender = text("sender")
    val text = text("text")
    val timestamp = long("timestamp")
}