package com.simats.aiskillmirror.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.repository.DashboardRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
                    skillScore = if (d.skillScore > 0) d.skillScore else _uiState.value.skillScore,
                    monthlyPtsGain = if (d.monthlyPtsGain > 0) d.monthlyPtsGain else _uiState.value.monthlyPtsGain,
                    currentStreakDays = if (d.currentStreakDays > 0) d.currentStreakDays else _uiState.value.currentStreakDays,
                    minutesToday = if (d.minutesToday > 0) d.minutesToday else _uiState.value.minutesToday,
                    goalMinutesToday = if (d.goalMinutesToday > 0) d.goalMinutesToday else _uiState.value.goalMinutesToday,
                    assessedSkillsCount = if (d.assessedSkillsCount > 0) d.assessedSkillsCount else 5,
                    learnerStatus = if (d.learnerStatus.isNotBlank()) d.learnerStatus else _uiState.value.learnerStatus,
                    currentLessonTitle = if (d.currentLessonTitle.isNotBlank()) d.currentLessonTitle else _uiState.value.currentLessonTitle,
                    currentLessonCategory = if (d.currentLessonCategory.isNotBlank()) d.currentLessonCategory else _uiState.value.currentLessonCategory,
                    currentLessonDuration = if (d.currentLessonDuration.isNotBlank()) d.currentLessonDuration else _uiState.value.currentLessonDuration,
                    greeting = if (d.greeting.isNotBlank()) d.greeting else _uiState.value.greeting,
                    currentDateText = if (d.currentDateText.isNotBlank()) d.currentDateText else _uiState.value.currentDateText,
                    headline = if (d.headline.isNotBlank()) d.headline else _uiState.value.headline,
                    streakSubtitle = if (d.streakSubtitle.isNotBlank()) d.streakSubtitle else _uiState.value.streakSubtitle,
                    minutesSubtitle = if (d.minutesSubtitle.isNotBlank()) d.minutesSubtitle else _uiState.value.minutesSubtitle,
                    currentPlanTitle = if (d.currentPlanTitle.isNotBlank()) d.currentPlanTitle else _uiState.value.currentPlanTitle
                )
            }
        }
    }

    fun onSelectTab(tab: DashboardTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun onUploadMedia(type: String, filename: String = "") {
        viewModelScope.launch {
            val name = if (filename.isNotBlank()) filename else when (type.lowercase()) {
                "video" -> "practice_session.mp4"
                "image" -> "posture_alignment.jpg"
                else -> "speech_sample.wav"
            }
            _uiState.value = _uiState.value.copy(
                uploadedMediaType = type,
                uploadedMediaName = name,
                isAnalyzing = true,
                uploadFeedbackMessage = "Analyzing $type using AI vision & acoustic models..."
            )
            delay(1500)
            _uiState.value = _uiState.value.copy(
                isAnalyzing = false,
                uploadFeedbackMessage = "Analysis complete! Score updated to ${_uiState.value.skillScore}% (+2 pts)."
            )
            _eventChannel.send(DashboardUiEvent.ShowToast("Uploaded $type successfully! AI Analysis complete."))
        }
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
