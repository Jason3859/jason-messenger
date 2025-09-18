package dev.jason.app.compose.desktop.messenger.data.di

import dev.jason.app.compose.desktop.messenger.data.api.auth.AuthApiRepoImpl
import dev.jason.app.compose.desktop.messenger.data.api.socket.ApiSocketRepoImpl
import dev.jason.app.compose.desktop.messenger.data.api.version.VersionCheckRepoImpl
import dev.jason.app.compose.desktop.messenger.data.prefs.PrefsRepoImpl
import dev.jason.app.compose.desktop.messenger.domain.RepositoryContainer
import dev.jason.app.compose.desktop.messenger.domain.api.ApiAuthRepository
import dev.jason.app.compose.desktop.messenger.domain.api.ApiSocketRepository
import dev.jason.app.compose.desktop.messenger.domain.api.VersionCheckRepository
import dev.jason.app.compose.desktop.messenger.domain.prefs.PrefsRepository
import dev.jason.app.compose.desktop.messenger.ui.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

object MessengerApp {
    enum class Qualifier {
        HTTP_CLIENT, REPOSITORY_CONTAINER, MAIN_VIEW_MODEL,
        API_AUTH_REPO, PREFS_REPO, API_SOCKET_REPO, VERSION_CHECK_REPO
    }

    private val module = module {

        single<OkHttpClient>(named(Qualifier.HTTP_CLIENT)) {
            OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
                .build()
        }

        single<RepositoryContainer>(named(Qualifier.REPOSITORY_CONTAINER)) {
            object : RepositoryContainer {
                override val apiAuthRepository: ApiAuthRepository
                    get() = get(named(Qualifier.API_AUTH_REPO))

                override val apiSocketRepository: ApiSocketRepository
                    get() = get(named(Qualifier.API_SOCKET_REPO))

                override val prefsRepository: PrefsRepository
                    get() = get(named(Qualifier.PREFS_REPO))

                override val versionCheckRepository: VersionCheckRepository
                    get() = get(named(Qualifier.VERSION_CHECK_REPO))
            }
        }

        single<VersionCheckRepository>(named(Qualifier.VERSION_CHECK_REPO)) {
            VersionCheckRepoImpl(get(named(Qualifier.HTTP_CLIENT)))
        }

        single<PrefsRepository>(named(Qualifier.PREFS_REPO)) {
            PrefsRepoImpl()
        }

        single<ApiAuthRepository>(named(Qualifier.API_AUTH_REPO)) {
            AuthApiRepoImpl(get(named(Qualifier.HTTP_CLIENT)))
        }

        single<ApiSocketRepository>(named(Qualifier.API_SOCKET_REPO)) {
            ApiSocketRepoImpl(get(named(Qualifier.HTTP_CLIENT)))
        }

        single<MainViewModel>(named(Qualifier.MAIN_VIEW_MODEL)) {
            MainViewModel(get<RepositoryContainer>(named(Qualifier.REPOSITORY_CONTAINER)))
        }
    }

    fun initKoin() {
        startKoin {
            modules(module)
        }
    }
}