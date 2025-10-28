package dev.jason.project.ktor.messenger.data.database

import com.mongodb.MongoConfigurationException
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Projections
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dev.jason.project.ktor.messenger.data.model.toLocalDateTime
import dev.jason.project.ktor.messenger.data.model.toLong
import dev.jason.project.ktor.messenger.domain.db.MessagesDatabaseRepository
import dev.jason.project.ktor.messenger.domain.db.UsersDatabaseRepository
import dev.jason.project.ktor.messenger.domain.model.Message
import dev.jason.project.ktor.messenger.domain.model.Result
import dev.jason.project.ktor.messenger.domain.model.User
import dev.jason.project.ktor.messenger.getDotenvInstance
import kotlinx.coroutines.flow.firstOrNull
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

object MongoDb {

    private val dotenv = getDotenvInstance()
    private val mongoUrl = dotenv?.get("MONGO_URL") ?: System.getenv("MONGO_URL")
    private val client: MongoClient
    private val database: MongoDatabase

    init {
        client = createMongoClient()
        database = client.getDatabase("messenger-app")
    }

    class UsersDbRepoImpl : UsersDatabaseRepository {

        data class MongoUser(
            @field:BsonId val id: ObjectId,
            val username: String,
            val password: String,
        )

        private fun MongoUser.toDomain() = User(username, password)

        private val collection = database.getCollection<MongoUser>("users")

        override suspend fun getAllUsers(): List<User> {
            try {
                val list = mutableListOf<MongoUser>()

                collection.find<MongoUser>().collect { user ->
                    list.add(user)
                }

                return list.map { it.toDomain() }
            } catch (e: Exception) {
                System.err.println(e.stackTraceToString())
                return emptyList()
            }
        }

        override suspend fun addUser(user: User): Result {
            val users = getAllUsers()

            var existing = false

            if (user in users) {
                existing = true
            }

            if (existing) {
                return Result.UserAlreadyExists
            }

            collection.insertOne(
                MongoUser(ObjectId(), user.username, user.password)
            )

            return Result.Success
        }

        override suspend fun findUser(user: User): Result {
            val query = eq(MongoUser::username.name, user.username)
            val document = collection.find(query).firstOrNull()

            if (document == null) {
                return Result.NotFound
            }

            if (document.password != user.password) {
                return Result.InvalidPassword
            }

            return Result.Success
        }

        override suspend fun deleteUser(user: User): Result {
            val query = eq(MongoUser::username.name, user.username)

            val mongoUser = collection.find(query).firstOrNull()

            if (mongoUser == null) {
                return Result.NotFound
            }

            collection.deleteOne(eq(query))

            return Result.Success
        }
    }

    class MessagesDbRepoImpl : MessagesDatabaseRepository {

        private val collection = database.getCollection<MongoMessage>("messages")

        data class MongoMessage(
            @field:BsonId val id: ObjectId,
            val chatroomid: String,
            val sender: String,
            val text: String,
            val timestamp: Long
        )

        private fun MongoMessage.toDomain() =
            Message(id.timestamp.toLong(), chatroomid, sender, text, timestamp.toLocalDateTime())

        override suspend fun addMessage(message: Message) {
            collection.insertOne(
                MongoMessage(
                    ObjectId(),
                    message.chatRoomId,
                    message.sender,
                    message.message,
                    message.timestamp.toLong()
                )
            )
        }

        override suspend fun getAllMessages(): List<Message> {
            val list = mutableListOf<MongoMessage>()

            collection.find<MongoMessage>().collect { message ->
                list.add(message)
            }

            return list.map { it.toDomain() }
        }

        override suspend fun deleteChatRoom(chatroomID: String): Result {
            try {
                val projections = Projections.fields(
                    Projections.include(MongoMessage::chatroomid.name),
                    Projections.excludeId()
                )

                var isRoomIdExisting = false

                collection.find(eq(MongoMessage::chatroomid.name, chatroomID))
                    .projection(projections)
                    .firstOrNull()
                    .let { mongoMessage ->
                        if (mongoMessage != null) {
                            isRoomIdExisting = true
                        }
                    }

                if (!isRoomIdExisting) {
                    return Result.NotFound
                }

                collection.deleteMany(eq(MongoMessage::chatroomid.name, chatroomID))

                return Result.Success
            } catch (e: Exception) {
                System.err.println(e.stackTraceToString())
                return Result.UnableToDelete
            }
        }
    }

    private fun createMongoClient(): MongoClient {
        try {
            println("connecting to remote")
            val client = MongoClient.create(mongoUrl)
            println("connected to remote")
            return client
        } catch (_: MongoConfigurationException) {
            println("no internet")
            println("connecting to local")
            val client =  MongoClient.create("mongodb://localhost:27017")
            println("connected to local")
            return client
        } catch (_: NullPointerException) {
            println("url not found")
            println("connecting to local")
            val client =  MongoClient.create("mongodb://localhost:27017")
            println("connected to local")
            return client
        }
    }
}