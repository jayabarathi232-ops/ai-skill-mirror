package com.simats.aiskillmirror.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun onInputChange(text: String) {
        _uiState.value = _uiState.value.copy(currentInputText = text)
    }

    fun onSendMessage() {
        val text = _uiState.value.currentInputText.trim()
        if (text.isNotEmpty()) {
            val updatedMessages = _uiState.value.messages + ChatMessage(
                id = System.currentTimeMillis().toString(),
                text = text,
                isUser = true
            )
            _uiState.value = _uiState.value.copy(
                messages = updatedMessages,
                currentInputText = ""
            )
            viewModelScope.launch {
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
