package com.simats.aiskillmirror.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.repository.DashboardRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository = DashboardRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<DashboardUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            val result = dashboardRepository.getDashboard(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                _uiState.value = _uiState.value.copy(
                    userName = if (d.userName.isNotBlank()) d.userName else _uiState.value.userName,
                    skillScore = d.skillScore,
                    monthlyPtsGain = d.monthlyPtsGain,
                    currentStreakDays = d.currentStreakDays,
                    minutesToday = d.minutesToday,
                    goalMinutesToday = d.goalMinutesToday,
                    assessedSkillsCount = d.assessedSkillsCount,
                    learnerStatus = d.learnerStatus,
                    currentLessonTitle = d.currentLessonTitle,
                    currentLessonCategory = d.currentLessonCategory,
                    currentLessonDuration = d.currentLessonDuration,
                    currentLessonProgress = d.currentLessonProgress.toFloat()
                )
            }
        }
    }

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
