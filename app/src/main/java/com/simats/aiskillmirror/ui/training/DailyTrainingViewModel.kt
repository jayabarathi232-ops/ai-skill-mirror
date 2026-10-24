package com.simats.aiskillmirror.ui.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DailyTrainingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DailyTrainingUiState())
    val uiState: StateFlow<DailyTrainingUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<DailyTrainingUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onStartPracticeClick() {
        viewModelScope.launch {
            _eventChannel.send(DailyTrainingUiEvent.StartPracticeSession)
        }
    }

    fun onCalendarClick() {
        viewModelScope.launch {
            _eventChannel.send(DailyTrainingUiEvent.OpenCalendar)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(DailyTrainingUiEvent.NavigateBack)
        }
    }
}
