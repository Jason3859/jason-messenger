package dev.jason.project.ktor.messenger.data.database

import org.jetbrains.exposed.sql.Table

object UsersDao : Table("users") {
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
    val id = long("id").autoIncrement()
    val username = text("username")
    val password = text("password")
}