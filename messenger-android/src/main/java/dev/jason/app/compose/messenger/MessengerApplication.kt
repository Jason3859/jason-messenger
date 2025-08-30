package dev.jason.app.compose.messenger

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import dev.jason.app.compose.messenger.data.api.ApiRepoImpl
import dev.jason.app.compose.messenger.data.database.DbRepoImpl
import dev.jason.app.compose.messenger.data.database.MessagesDatabase
import dev.jason.app.compose.messenger.data.saved_preferences.PrefsRepoImpl
import dev.jason.app.compose.messenger.domain.RepositoryContainer
import dev.jason.app.compose.messenger.domain.api.ApiRepository
import dev.jason.app.compose.messenger.domain.database.DatabaseRepository
import dev.jason.app.compose.messenger.domain.saved_preferences.PrefsRepository
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.WebSockets
import java.io.File

class MessengerApplication : Application() {

    lateinit var databaseRepository: DatabaseRepository
    private val prefsFile: File by lazy {
        File(getExternalFilesDir(null), "saved_prefs.json")
    }

    fun getFile() = prefsFile

    companion object {
        private const val TIMEOUT_MILLIS: Long = 1000000
        private val client = HttpClient(OkHttp) {
            install(WebSockets)
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT_MILLIS
            }
        }

        private const val BASE_URL = "https://jason-messenger.onrender.com"

        val viewModelFactory = viewModelFactory {
            initializer {
                MainViewModel(
                    repositories = object : RepositoryContainer {
                        override val databaseRepository: DatabaseRepository
                            get() = getApplication().databaseRepository
                        override val apiRepository: ApiRepository
                            get() = ApiRepoImpl(client, BASE_URL, getApplication())
                        override val prefsRepository: PrefsRepository
                            get() = PrefsRepoImpl(getApplication().getFile())
                    },
                    context = getApplication()
                )
            }
        }

        private fun CreationExtras.getApplication() = this[APPLICATION_KEY] as MessengerApplication
    }

    override fun onCreate() {
        super.onCreate()
        databaseRepository = DbRepoImpl(createDatabase().messagesDao())
    }

    private fun createDatabase(): MessagesDatabase {
        return Room.databaseBuilder<MessagesDatabase>(this, "messages.db")
            .build()
    }
}