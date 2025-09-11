package dev.jason.app.compose.messenger.data.di

import dev.jason.app.compose.messenger.data.api.auth.ApiAuthRepoImpl
import dev.jason.app.compose.messenger.data.prefs.PrefsRepoImpl
import dev.jason.app.compose.messenger.domain.RepositoryContainer
import dev.jason.app.compose.messenger.domain.api.ApiAuthRepository
import dev.jason.app.compose.messenger.domain.prefs.PrefsRepository
import dev.jason.app.compose.messenger.ui.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

object MessengerApp {
    enum class Qualifier {
        HTTP_CLIENT, REPOSITORY_CONTAINER, MAIN_VIEW_MODEL, API_AUTH_REPO, PREFS_REPO
    }

    private val module = module {

        single<OkHttpClient>(named(Qualifier.HTTP_CLIENT)) {
            OkHttpClient()
        }

        single<RepositoryContainer>(named(Qualifier.REPOSITORY_CONTAINER)) {
            object : RepositoryContainer {
                override val apiAuthRepository: ApiAuthRepository
                    get() = get(named(Qualifier.API_AUTH_REPO))

                override val prefsRepository: PrefsRepository
                    get() = get(named(Qualifier.PREFS_REPO))
            }
        }

        single<PrefsRepository>(named(Qualifier.PREFS_REPO)) {
            PrefsRepoImpl()
        }

        single<ApiAuthRepository>(named(Qualifier.API_AUTH_REPO)) {
            ApiAuthRepoImpl(get(named(Qualifier.HTTP_CLIENT)))
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