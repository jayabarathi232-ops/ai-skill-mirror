package com.simats.aiskillmirror.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.UserSession
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AiCoachViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AiCoachUiState())
    val uiState: StateFlow<AiCoachUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<AiCoachUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadChat()
    }

    fun loadChat() {
        viewModelScope.launch {
            val result = ApiClient.fetchCoachChat(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                val mapped = d.messages.map {
                    ChatMessage(
                        id = it.id,
                        text = it.text,
                        isUser = it.isUser,
                        timestamp = it.timestamp
                    )
                }
                _uiState.value = _uiState.value.copy(
                    coachName = d.coachName,
                    statusText = d.statusText,
                    bannerNoticeText = d.bannerNoticeText,
                    timestampHeader = d.timestampHeader,
                    messages = mapped,
                    suggestionChips = d.suggestionChips
                )
            }
        }
    }

    fun onInputChange(text: String) {
        _uiState.value = _uiState.value.copy(currentInputText = text)
    }

    fun onSendMessage() {
        val text = _uiState.value.currentInputText.trim()
        if (text.isNotEmpty()) {
            val optimisticMsg = ChatMessage(
                id = System.currentTimeMillis().toString(),
                text = text,
                isUser = true
            )
            val updatedMessages = _uiState.value.messages + optimisticMsg
            _uiState.value = _uiState.value.copy(
                messages = updatedMessages,
                currentInputText = ""
            )

            viewModelScope.launch {
                val result = ApiClient.sendCoachMessage(UserSession.userId, text)
                if (result.isSuccess && result.data != null) {
                    val coachReply = ChatMessage(
                        id = result.data.second.id,
                        text = result.data.second.text,
                        isUser = false,
                        timestamp = result.data.second.timestamp
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + coachReply
                    )
                }
                _eventChannel.send(AiCoachUiEvent.SendMessage)
            }
        }
    }

    fun onSuggestionChipClick(chipText: String) {
        if (chipText.contains("practice", ignoreCase = true)) {
            viewModelScope.launch {
                _eventChannel.send(AiCoachUiEvent.StartPracticeFromCoach)
            }
        } else {
            onInputChange(chipText)
            onSendMessage()
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(AiCoachUiEvent.NavigateBack)
        }
    }
}
