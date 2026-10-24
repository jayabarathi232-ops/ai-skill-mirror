package com.simats.aiskillmirror.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProgressAnalyticsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressAnalyticsUiState())
    val uiState: StateFlow<ProgressAnalyticsUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<ProgressAnalyticsUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onSelectTimeframe(timeframe: TimeframeTab) {
        _uiState.value = _uiState.value.copy(selectedTimeframe = timeframe)
    }

    fun onViewReportClick() {
        viewModelScope.launch {
            _eventChannel.send(ProgressAnalyticsUiEvent.ViewReport)
        }
    }

    fun onCalendarClick() {
        viewModelScope.launch {
            _eventChannel.send(ProgressAnalyticsUiEvent.OpenCalendar)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(ProgressAnalyticsUiEvent.NavigateBack)
        }
    }
}
