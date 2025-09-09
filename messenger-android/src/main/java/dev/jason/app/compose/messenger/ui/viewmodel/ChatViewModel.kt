package dev.jason.app.compose.messenger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jason.app.compose.messenger.domain.RepositoryContainer
import dev.jason.app.compose.messenger.domain.model.Message
import dev.jason.app.compose.messenger.domain.model.Result
import dev.jason.app.compose.messenger.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repositories: RepositoryContainer
) : ViewModel() {

    private lateinit var user: User

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            repositories.apiSocketRepository.getMessages().collect { message ->
                _messages.update { current ->
                    current.toMutableList().apply {
                        add(message)
                    }
                }
            }
        }
    }

    data class ChatroomUiState(
        val chatroomId: String = "",
        val isSuccessful: Boolean = false,
        val isAccountDeleteSuccessful: Boolean = false,
        val isChatroomDeleteSuccessful: Boolean = false,
    )

    private val _chatroomUiState = MutableStateFlow(ChatroomUiState())
    val chatroomUiState = _chatroomUiState.asStateFlow()

    fun updateChatroomId(roomId: String) {
        _chatroomUiState.update { it.copy(chatroomId = roomId) }
    }

    fun connect() {
        viewModelScope.launch {
            user = repositories.prefsRepository.getPref().user!!

            val result = repositories.apiSocketRepository.connect(
                user = user,
                chatroomId = _chatroomUiState.value.chatroomId
            )

            if (result is Result.Success) {
                _chatroomUiState.update { it.copy(isSuccessful = true) }
            }

            if (result is Result.Error) {
                println(result.error.message)
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
            repositories.apiSocketRepository.sendMessage(_message.value)
            _message.update { "" }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            repositories.apiSocketRepository.closeSession()
            _messages.update { emptyList() }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            repositories.apiAuthRepository.deleteAccount(user)
            repositories.prefsRepository.deletePrefs()
            _chatroomUiState.update {
                it.copy(isAccountDeleteSuccessful = true)
            }
        }
    }

    fun deleteChatroom() {
        viewModelScope.launch {
            repositories.apiAuthRepository.deleteChatroom(_chatroomUiState.value.chatroomId)
            _chatroomUiState.update {
                it.copy(isChatroomDeleteSuccessful = true)
            }
        }
    }
}