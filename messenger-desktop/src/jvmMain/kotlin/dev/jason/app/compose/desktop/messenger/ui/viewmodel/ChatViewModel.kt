package dev.jason.app.compose.desktop.messenger.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dev.jason.app.compose.desktop.messenger.domain.RepositoryContainer
import dev.jason.app.compose.desktop.messenger.domain.model.Result
import dev.jason.app.compose.desktop.messenger.ui.nav.Routes
import dev.jason.app.compose.desktop.messenger.ui.util.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(private val repositories: RepositoryContainer) : MainViewModel(repositories) {

    data class ChatroomUiState(
        val roomId: String = "",
        val isSuccessful: Boolean = false,
        val isAccountDeleteSuccessful: Boolean = false,
        val isChatroomDeleteSuccessful: Boolean = false,
    )

    private val _uiState = MutableStateFlow(ChatroomUiState())
    val chatroomUiState = _uiState.asStateFlow()

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

            repositories.apiSocketRepository.connect(
                user = repositories.prefsRepository.getUser()!!,
                roomId = _uiState.value.roomId
            ).apply {
                if (this is Result.Success) {
                    _uiState.update {
                        it.copy(
                            isSuccessful = true
                        )
                    }

                    destination.update {
                        Routes.EnterRoomIdScreen
                    }
                }

                if (this is Result.Error) {
                    SnackbarController.sendResult(this)
                }
            }
        }
    }
}