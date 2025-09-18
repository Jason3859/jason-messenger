package dev.jason.app.compose.messenger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jason.app.compose.messenger.domain.RepositoryContainer
import dev.jason.app.compose.messenger.domain.model.Result
import dev.jason.app.compose.messenger.domain.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repositories: RepositoryContainer,
) : ViewModel() {

    data class LoginUiState(
        val username: String = "",
        val password: String = "",
        val isSuccessful: Boolean = false,
        val isError: Boolean = false,
        val error: Result.Error? = null
    )

    private val preferences = repositories.prefsRepository.getPref()
    val savedPrefs = preferences

    private val _loginUiState = MutableStateFlow(LoginUiState())

    val loginUiState = _loginUiState.asStateFlow()

    init {
        if (!preferences.user?.username.isNullOrEmpty()) {
            viewModelScope.launch {
                delay(2000L)
                loginWithSavedUser().apply {
                    if (this is Result.Success) {
                        _loginUiState.update { it.copy(username = preferences.user.username, isSuccessful = true) }
                    }

                    if (this is Result.Error) {
                        _loginUiState.update { it.copy(isError = true, error = this) }
                    }
                }
            }
        }
    }

    companion object {
        const val CURRENT_VERSION = "1.1.0"
    }

    private var version: String? = null

    suspend fun isVersionLatest(): Boolean {
        val job = viewModelScope.launch {
            repositories.versionCheckRepository.getVersion().apply {
                this@MainViewModel.version = this
            }
        }

        while (!job.isCompleted) {
            delay(10L)
        }

        return (version == CURRENT_VERSION).also { println("is latest: $it") }
    }

    fun updateUsername(username: String) {
        _loginUiState.update { it.copy(username) }
    }

    fun updatePassword(password: String) {
        _loginUiState.update { it.copy(password = password) }
    }

    fun login() {
        viewModelScope.launch {
            repositories.apiAuthRepository.login(
                user = User(
                    username = _loginUiState.value.username,
                    password = _loginUiState.value.password
                )
            ).apply {
                if (this is Result.Success) {
                    repositories.prefsRepository.saveUser(
                        User(
                            _loginUiState.value.username,
                            _loginUiState.value.password
                        )
                    )
                    _loginUiState.update { it.copy(isSuccessful = true) }
                } else {
                    _loginUiState.update { it.copy(isError = true) }
                }
            }
        }
    }

    private suspend fun loginWithSavedUser(): Result {
        return repositories.apiAuthRepository.login(
            user = User(
                username = preferences.user?.username!!.also(::println),
                password = preferences.user.password.also(::println)
            )
        )
    }

    fun signin() {
        viewModelScope.launch {
            repositories.apiAuthRepository.signin(
                user = User(
                    username = _loginUiState.value.username,
                    password = _loginUiState.value.password
                )
            ).apply {
                if (this is Result.Success) {
                    repositories.prefsRepository.saveUser(
                        User(
                            _loginUiState.value.username,
                            _loginUiState.value.password
                        )
                    )
                    login()
                } else _loginUiState.update { it.copy(isError = true) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repositories.prefsRepository.deletePrefs()
            _loginUiState.update { it.copy(password = "", isSuccessful = false) }
        }
    }

}