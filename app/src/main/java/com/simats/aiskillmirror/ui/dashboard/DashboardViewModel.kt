package com.simats.aiskillmirror.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<DashboardUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onSelectTab(tab: DashboardTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun onViewSkillMirrorClick() {
        viewModelScope.launch {
            _eventChannel.send(DashboardUiEvent.ViewSkillMirror)
        }
    }

    fun onViewPlanClick() {
        viewModelScope.launch {
            _eventChannel.send(DashboardUiEvent.ViewPlan)
        }
    }

    fun onContinueTrainingClick() {
        viewModelScope.launch {
            _eventChannel.send(DashboardUiEvent.ContinueTraining)
        }
    }

    fun onOpenAiCoachClick() {
        viewModelScope.launch {
            _eventChannel.send(DashboardUiEvent.OpenAiCoach)
        }
    }

    fun onNotificationsClick() {
        viewModelScope.launch {
            _eventChannel.send(DashboardUiEvent.OpenNotifications)
        }
    }
}
