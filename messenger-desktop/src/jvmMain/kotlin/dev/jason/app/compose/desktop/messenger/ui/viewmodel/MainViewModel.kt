package dev.jason.app.compose.desktop.messenger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jason.app.compose.desktop.messenger.domain.RepositoryContainer
import dev.jason.app.compose.desktop.messenger.domain.model.Message
import dev.jason.app.compose.desktop.messenger.domain.model.Result
import dev.jason.app.compose.desktop.messenger.domain.model.User
import dev.jason.app.compose.desktop.messenger.ui.nav.Routes
import dev.jason.app.compose.desktop.messenger.ui.util.SnackbarController
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

    private val _currentDestination = MutableStateFlow<Routes>(_startDestination.value)
    val currentDestination = _currentDestination.asStateFlow()

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
                    _currentDestination.update { Routes.SigninLoadingScreen }
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

                SnackbarController.sendResult(this)
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

            _currentDestination.update { Routes.LoginLoadingScreen }

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

                    _currentDestination.update { Routes.EnterRoomIdScreen }
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

                SnackbarController.sendResult(this)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repositories.prefsRepository.deleteUser()
            _currentDestination.update { Routes.LoginScreen }
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

    data class ChatroomUiState(
        val roomId: String = "",
        val isSuccessful: Boolean = false,
        val isAccountDeleteSuccessful: Boolean = false,
        val isChatroomDeleteSuccessful: Boolean = false,
    )

    private val _uiState = MutableStateFlow(ChatroomUiState())
    val chatroomUiState = _uiState.asStateFlow()

    private val _messages = MutableStateFlow(listOf<Message>())
    val messages = _messages.asStateFlow()

    init {
        loginWithSavedUser()
        connectWithSavedRoomId()

        viewModelScope.launch {
            repositories.apiSocketRepository.getMessages().collect { message ->
                _messages.update { current ->
                    current + message
                }
            }
        }
    }

    private fun connectWithSavedRoomId() {
        repositories.prefsRepository.getRoom().apply {
            if (this != null) {
                _uiState.update {
                    it.copy(
                        roomId = this
                    )
                }
                connect()
            }
        }
    }

    fun updateRoomId(roomId: String) {
        _uiState.update {
            it.copy(
                roomId = roomId
            )
        }
    }

    fun connect() {
        viewModelScope.launch {
            if (_uiState.value.roomId.isEmpty() || _uiState.value.roomId.isBlank()) {
                SnackbarController.sendWarning("Room Id cannot be empty.")
                return@launch
            }

            if (_uiState.value.roomId.contains(' ')) {
                SnackbarController.sendWarning("Room Id cannot contain spaces.")
                return@launch
            }

            _currentDestination.update { Routes.ConnectLoadingScreen }

            repositories.apiSocketRepository.connect(
                user = repositories.prefsRepository.getUser()!!,
                roomId = _uiState.value.roomId
            ).apply {
                if (this is Result.Success) {
                    repositories.prefsRepository.saveRoom(_uiState.value.roomId)

                    _uiState.update {
                        it.copy(
                            isSuccessful = true
                        )
                    }

                    _currentDestination.update { Routes.MessagingScreen }
                }

                if (this is Result.Error) {
                    SnackbarController.sendResult(this)
                }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            repositories.apiSocketRepository.closeSession()
            repositories.prefsRepository.deleteRoom()
        }
    }

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    fun updateMessage(message: String) {
        _message.update {
            message
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (_message.value.isEmpty() || _message.value.isBlank()) {
                SnackbarController.sendWarning("Message cannot be empty.")
                return@launch
            }

            repositories.apiSocketRepository.sendMessage(_message.value)
            _message.update { "" }
        }
    }

}