package com.simats.aiskillmirror.ui.speech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SpeechStudioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SpeechStudioUiState())
    val uiState: StateFlow<SpeechStudioUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SpeechStudioUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onStopClick() {
        _uiState.value = _uiState.value.copy(isRecording = false)
        viewModelScope.launch {
            _eventChannel.send(SpeechStudioUiEvent.StopAndGetFeedback)
        }
    }

    fun onRestartClick() {
        _uiState.value = _uiState.value.copy(timerText = "00:00", isRecording = true)
        viewModelScope.launch {
            _eventChannel.send(SpeechStudioUiEvent.RestartRecording)
        }
    }

    fun onPauseClick() {
        val paused = !_uiState.value.isPaused
        _uiState.value = _uiState.value.copy(isPaused = paused)
        viewModelScope.launch {
            _eventChannel.send(SpeechStudioUiEvent.TogglePauseRecording)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(SpeechStudioUiEvent.NavigateBack)
        }
    }
}
