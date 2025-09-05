package dev.jason.app.compose.messenger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jason.app.compose.messenger.domain.RepositoryContainer
import dev.jason.app.compose.messenger.domain.api.Result
import dev.jason.app.compose.messenger.domain.api.User
import dev.jason.app.compose.messenger.domain.database.Message
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repositories: RepositoryContainer
) : ViewModel() {

    private lateinit var user: User

    private val preferences = repositories.prefsRepository.getPref()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    init {
        _messages.update {
            repositories.databaseRepository.getAllMessages()
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000L),
                    emptyList()
                ).value
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
            user = preferences.user!!

            val result = repositories.apiSocketRepository.connect(
                user = user,
                chatroomId = _chatroomUiState.value.chatroomId
            )

            if (result is Result.Success) {
                println("success")
                _chatroomUiState.update { it.copy(isSuccessful = true) }

                repositories.apiSocketRepository.getMessages().collect { message ->
                    _messages.update {
                        it + message
                    }
                    repositories.databaseRepository.addMessage(message)
                }
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
        }
    }
}