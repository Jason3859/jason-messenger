package dev.jason.app.compose.messenger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jason.app.compose.messenger.data.database.MessageEntity
import dev.jason.app.compose.messenger.data.database.mappers.toDomain
import dev.jason.app.compose.messenger.data.database.mappers.toLong
import dev.jason.app.compose.messenger.domain.RepositoryContainer
import dev.jason.app.compose.messenger.domain.api.Result
import dev.jason.app.compose.messenger.domain.api.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel(
    private val repositories: RepositoryContainer,
) : ViewModel() {

    data class LoginUiState(
        val username: String = "",
        val password: String = "",
        val isSuccessful: Boolean = false,
    )

    private val preferences = repositories.prefsRepository.getPref()
    val savedPrefs = preferences

    private val _loginUiState = MutableStateFlow(LoginUiState())

    val loginUiState = _loginUiState.asStateFlow()

    init {
        if (!preferences.user?.username.isNullOrEmpty()) {
            viewModelScope.launch {
                delay(2000L)
                loginWithSavedUser()
                _loginUiState.update { it.copy(username = preferences.user.username, isSuccessful = true) }
            }
        }
    }

    fun updateUsername(username: String) {
        _loginUiState.update { it.copy(username) }
    }

    fun updatePassword(password: String) {
        _loginUiState.update { it.copy(password = password) }
    }

    fun login() {
        viewModelScope.launch {
            repositories.apiRepository.login(
                user = User(
                    username = _loginUiState.value.username,
                    password = _loginUiState.value.password
                )
            ).apply {
                if (this is Result.Success) {
                    repositories.prefsRepository.saveUser(User(_loginUiState.value.username, _loginUiState.value.password))
                    _loginUiState.update { it.copy(isSuccessful = true) }
                }
            }
        }
    }

    private suspend fun loginWithSavedUser() {
        repositories.apiRepository.login(
            user = User(
                username = preferences.user?.username!!.also(::println),
                password = preferences.user.password.also(::println)
            )
        )
    }

    fun signin() {
        viewModelScope.launch {
            repositories.apiRepository.signin(
                user = User(
                    username = _loginUiState.value.username,
                    password = _loginUiState.value.password
                )
            ).apply {
                if (this is Result.Success) {
                    _loginUiState.update { it.copy(isSuccessful = true) }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repositories.prefsRepository.deletePrefs()
            _loginUiState.update { it.copy(password = "", isSuccessful = false) }
        }
    }

    data class ChatroomUiState(
        val chatroomId: String = "",
        val isSuccessful: Boolean = false,
    )

    private val _chatroomUiState = MutableStateFlow(ChatroomUiState())
    val chatroomUiState = _chatroomUiState.asStateFlow()

    fun updateChatroomId(roomId: String) {
        _chatroomUiState.update { it.copy(chatroomId = roomId) }
    }

    fun connect() {
        viewModelScope.launch {
            repositories.apiRepository.connect(
                user = User(
                    username = _loginUiState.value.username,
                    password = _loginUiState.value.password
                ),
                chatroomID = _chatroomUiState.value.chatroomId
            ).apply {
                if (this is Result.Success) {
                    _chatroomUiState.update { it.copy(isSuccessful = true) }
                }
            }
        }
    }

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    fun updateMessage(message: String) {
        _message.update { message }
    }

    fun sendMessage() {
        viewModelScope.launch {
            repositories.apiRepository.sendMessage(_message.value)
            repositories.databaseRepository.addMessage(
                message = MessageEntity(
                    chatRoomId = _chatroomUiState.value.chatroomId,
                    sender = _loginUiState.value.username,
                    message = _message.value,
                    timestamp = LocalDateTime.now().toLong()
                ).toDomain()
            )
        }
    }

}