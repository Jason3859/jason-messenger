package dev.jason.app.compose.messenger

import android.app.Application
import dev.jason.app.compose.messenger.data.api.auth.ApiAuthRepoImpl
import dev.jason.app.compose.messenger.data.api.socket.ApiSocketImpl
import dev.jason.app.compose.messenger.data.saved_preferences.PrefsRepoImpl
import dev.jason.app.compose.messenger.domain.RepositoryContainer
import dev.jason.app.compose.messenger.domain.api.ApiAuthRepository
import dev.jason.app.compose.messenger.domain.api.ApiSocketRepository
import dev.jason.app.compose.messenger.domain.saved_preferences.PrefsRepository
import dev.jason.app.compose.messenger.ui.viewmodel.ChatViewModel
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

class MessengerApplication : Application() {

    companion object {
        private const val TIMEOUT_MILLIS: Long = 1_000_000_000
    }

    enum class Qualifier {
        API_AUTH_REPOSITORY, API_SOCKET_REPOSITORY, PREFS_REPOSITORY, KTOR_HTTP_CLIENT,
        OKHTTP_CLIENT, PREFS_FILE, MAIN_VIEW_MODEL, CHAT_VIEW_MODEL, REPOSITORY_CONTAINER
    }


    private val applicationModule = module {

        single(named(Qualifier.OKHTTP_CLIENT)) {
            OkHttpClient()
        }

        single(named(Qualifier.KTOR_HTTP_CLIENT)) {
            HttpClient(OkHttp) {
                install(HttpTimeout) {
                    requestTimeoutMillis = TIMEOUT_MILLIS
                }
            }
        }

        single<ApiAuthRepository>(named(Qualifier.API_AUTH_REPOSITORY)) {
            ApiAuthRepoImpl(
                client = get(named(Qualifier.KTOR_HTTP_CLIENT)),
                context = androidContext()
            )
        }

        single<File>(named(Qualifier.PREFS_FILE)) {
            File(androidContext().getExternalFilesDir(null), "saved_prefs.json")
        }

        single<PrefsRepository>(named(Qualifier.PREFS_REPOSITORY)) {
            PrefsRepoImpl(
                file = get(named(Qualifier.PREFS_FILE))
            )
        }

        single<ApiSocketRepository>(named(Qualifier.API_SOCKET_REPOSITORY)) {
            ApiSocketImpl(
                client = get(named(Qualifier.OKHTTP_CLIENT))
            )
        }

        single<RepositoryContainer>(named(Qualifier.REPOSITORY_CONTAINER)) {
            object : RepositoryContainer {
                override val apiAuthRepository: ApiAuthRepository
                    get() = get(named(Qualifier.API_AUTH_REPOSITORY))

                override val prefsRepository: PrefsRepository
                    get() = get(named(Qualifier.PREFS_REPOSITORY))

                override val apiSocketRepository: ApiSocketRepository
                    get() = get(named(Qualifier.API_SOCKET_REPOSITORY))
            }
        }

        viewModel<ChatViewModel>(named(Qualifier.CHAT_VIEW_MODEL)) {
            ChatViewModel(
                repositories = get(named(Qualifier.REPOSITORY_CONTAINER))
            )
        }

        viewModel<MainViewModel>(named(Qualifier.MAIN_VIEW_MODEL)) {
            MainViewModel(
                repositories = get(named(Qualifier.REPOSITORY_CONTAINER))
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MessengerApplication)
            modules(applicationModule)
        }
    }
}