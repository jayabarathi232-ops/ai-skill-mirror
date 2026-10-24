package com.simats.aiskillmirror.ui.speech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SpeechAnalysisViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SpeechAnalysisUiState())
    val uiState: StateFlow<SpeechAnalysisUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SpeechAnalysisUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onPracticeAgainClick() {
        viewModelScope.launch {
            _eventChannel.send(SpeechAnalysisUiEvent.PracticeAgain)
        }
    }

    fun onPlayRecordingClick() {
        viewModelScope.launch {
            _eventChannel.send(SpeechAnalysisUiEvent.PlayRecording)
        }
    }

    fun onShareClick() {
        viewModelScope.launch {
            _eventChannel.send(SpeechAnalysisUiEvent.ShareAnalysis)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(SpeechAnalysisUiEvent.NavigateBack)
        }
    }
}
