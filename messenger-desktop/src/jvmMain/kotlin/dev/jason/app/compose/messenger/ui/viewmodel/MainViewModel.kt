package dev.jason.app.compose.messenger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jason.app.compose.messenger.domain.RepositoryContainer
import dev.jason.app.compose.messenger.domain.model.Result
import dev.jason.app.compose.messenger.domain.model.User
import dev.jason.app.compose.messenger.ui.nav.Routes
import dev.jason.app.compose.messenger.ui.util.SnackbarController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainViewModel(private val repositories: RepositoryContainer) : ViewModel() {

    data class LoginUiState(
        val username: String = "",
        val password: String = "",
        val isSuccessful: Boolean = false,
        val isError: Boolean = false,
        val error: Result.Error? = null
    )

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    private val _startDestination = MutableStateFlow<Routes>(Routes.LoginScreen)
    val startDestination = _startDestination.asStateFlow()

    fun updateUsername(username: String) {
        _loginUiState.update {
            it.copy(username = username)
        }
    }

    fun updatePassword(password: String) {
        _loginUiState.update {
            it.copy(password = password)
        }
    }

    fun signin() {
        viewModelScope.launch {
            _loginUiState.update {
                it.copy(
                    isError = false
                )
            }

            delay(1.seconds)

            repositories.apiAuthRepository.signin(
                user = User(
                    username = _loginUiState.value.username,
                    password = _loginUiState.value.password
                )
            ).apply {
                if (this is Result.Success) {
                    login()
                }

                if (this is Result.Error) {
                    _loginUiState.update {
                        it.copy(
                            isError = true,
                            error = this
                        )
                    }
                }

                if (this is Result.UserAlreadyExists) {
                    _loginUiState.update {
                        it.copy(
                            isError = true,
                            error = Result.Error(null, this.toString())
                        )
                    }
                }

                SnackbarController.sendEvent(this)
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            _loginUiState.update {
                it.copy(
                    isError = false
                )
            }

            delay(1.seconds)

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

                    _loginUiState.update {
                        it.copy(
                            isSuccessful = true
                        )
                    }

                    _startDestination.update {
                        Routes.EnterRoomIdScreen
                    }
                }

                if (this is Result.InvalidPassword) {
                    _loginUiState.update {
                        it.copy(
                            isError = true,
                            error = Result.Error(null, this.toString())
                        )
                    }
                }

                if (this is Result.UserNotFound) {
                    _loginUiState.update {
                        it.copy(
                            isError = true,
                            error = Result.Error(null, this.toString())
                        )
                    }
                }

                if (this is Result.Error) {
                    _loginUiState.update {
                        it.copy(
                            isError = true,
                            error = this
                        )
                    }
                }

                SnackbarController.sendEvent(this)
            }
        }
    }

    private fun loginWithSavedUser() {
        val user = repositories.prefsRepository.getUser()
        if (user == null) {
            return
        }
        _startDestination.update { Routes.LoginLoadingScreen }
        _loginUiState.update {
            it.copy(
                username = user.username,
                password = user.password
            )
        }
        login()
    }

    init {
        loginWithSavedUser()
    }

}