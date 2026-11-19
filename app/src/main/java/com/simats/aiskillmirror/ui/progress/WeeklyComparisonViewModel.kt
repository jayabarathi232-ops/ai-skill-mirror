package com.simats.aiskillmirror.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.network.ApiClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class WeeklyComparisonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyComparisonUiState())
    val uiState: StateFlow<WeeklyComparisonUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<WeeklyComparisonUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadComparison(0)
    }

    fun loadComparison(weekOffset: Int = _uiState.value.currentWeekOffset) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                currentWeekOffset = weekOffset
            )
            val result = ApiClient.fetchWeeklyComparison(UserSession.userId, weekOffset)
            if (result.isSuccess && result.data != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    comparisonData = result.data,
                    currentWeekOffset = weekOffset
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
            }
        }
    }

    fun changeWeekOffset(delta: Int) {
        val newOffset = _uiState.value.currentWeekOffset + delta
        if (newOffset > 0) return // Future weeks are disabled
        loadComparison(newOffset)
    }

    fun retryAiSummary() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiRetrying = true)
            val result = ApiClient.fetchWeeklyComparison(
                userId = UserSession.userId,
                weekOffset = _uiState.value.currentWeekOffset,
                includeAi = true
            )
            if (result.isSuccess && result.data != null) {
                _uiState.value = _uiState.value.copy(
                    isAiRetrying = false,
                    comparisonData = result.data
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isAiRetrying = false,
                    errorMessage = result.message
                )
            }
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(WeeklyComparisonUiEvent.NavigateBack)
        }
    }
}
