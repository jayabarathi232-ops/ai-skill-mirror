package com.simats.aiskillmirror.ui.training

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

class DailyTrainingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DailyTrainingUiState())
    val uiState: StateFlow<DailyTrainingUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<DailyTrainingUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadTraining()
    }

    fun loadTraining() {
        viewModelScope.launch {
            val result = ApiClient.fetchTraining(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                val completed = d.completedPracticeItems.map {
                    PracticeItem(
                        id = it.id,
                        title = it.title,
                        category = it.category,
                        durationText = it.durationText,
                        xpText = it.xpText,
                        isCompleted = it.isCompleted
                    )
                }
                _uiState.value = _uiState.value.copy(
                    headerDateText = d.headerDateText,
                    streakBadgeText = d.streakBadgeText,
                    currentMinutes = d.currentMinutes,
                    targetMinutes = d.targetMinutes,
                    practiceProgressText = d.practiceProgressText,
                    completedPracticeItems = completed,
                    upNextTitle = d.upNextTitle,
                    upNextCategory = d.upNextCategory,
                    upNextMetaText = d.upNextMetaText,
                    upNextDescription = d.upNextDescription,
                    startButtonText = d.startButtonText
                )
            }
        }
    }

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
