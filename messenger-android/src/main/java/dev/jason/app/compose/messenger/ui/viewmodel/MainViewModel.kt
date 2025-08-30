package dev.jason.app.compose.messenger.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jason.app.compose.messenger.data.database.MessageEntity
import dev.jason.app.compose.messenger.data.database.mappers.toDomain
import dev.jason.app.compose.messenger.data.database.mappers.toLong
import dev.jason.app.compose.messenger.domain.RepositoryContainer
import dev.jason.app.compose.messenger.domain.api.Result
import dev.jason.app.compose.messenger.domain.api.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel(
    private val repositories: RepositoryContainer,
    private val context: Context,
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
            loginWithSavedUser()
            _loginUiState.update { it.copy(username = preferences.user.username) }
        }
        if (!preferences.chatroomId.isNullOrEmpty()) {
            connectWithSavedChatroom()
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
                if (this is Result.NotFound) {
                    Toast.makeText(context, "User with username ${_loginUiState.value.username} not found", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loginWithSavedUser() {
        viewModelScope.launch {
            repositories.apiRepository.login(
                user = User(
                    username = preferences.user?.username!!,
                    password = preferences.user.password
                )
            )
        }
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

    fun logoutOfChatroom() {
        viewModelScope.launch {
            val savedChatroom = preferences.chatroomId!!
            repositories.prefsRepository.deleteSavedChatroomId(savedChatroom)
        }
    }

    private val _chatroomId = MutableStateFlow("")
    val chatroomId = _chatroomId.asStateFlow()

    fun updateChatroomId(roomId: String) {
        _chatroomId.update { roomId }
    }

    fun connect() {
        viewModelScope.launch {
            repositories.apiRepository.connect(
                user = User(
                    username = _loginUiState.value.username,
                    password = _loginUiState.value.password
                ),
                chatroomID = _chatroomId.value
            )
        }
    }

    private fun connectWithSavedChatroom() {
        viewModelScope.launch {
            repositories.apiRepository.connect(
                user = preferences?.user!!,
                chatroomID = preferences.chatroomId!!
            )
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
                    chatRoomId = _chatroomId.value,
                    sender = _loginUiState.value.username,
                    message = _message.value,
                    timestamp = LocalDateTime.now().toLong()
                ).toDomain()
            )
        }
    }

}