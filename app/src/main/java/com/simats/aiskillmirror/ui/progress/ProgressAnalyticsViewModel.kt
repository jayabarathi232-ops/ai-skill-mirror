package com.simats.aiskillmirror.ui.progress

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

class ProgressAnalyticsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressAnalyticsUiState())
    val uiState: StateFlow<ProgressAnalyticsUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<ProgressAnalyticsUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = ApiClient.fetchAnalytics(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                val mappedSkills = d.skillItems.map {
                    ProgressSkillItem(
                        name = it.name,
                        score = it.score,
                        maxScore = it.maxScore,
                        isTopTier = it.isTopTier
                    )
                }
                val mappedRecent = d.recentPracticeScores.map {
                    RecentPracticeScoreItem(
                        id = it.id,
                        taskTitle = it.taskTitle,
                        mainSkill = it.mainSkill,
                        subcategoryName = it.subcategoryName,
                        durationText = it.durationText,
                        xpText = "+${it.xpAwarded} XP",
                        score = it.score,
                        scoreStatus = it.scoreStatus,
                        scoreReason = it.scoreReason,
                        strengths = it.strengths,
                        weaknesses = it.weaknesses,
                        aiFeedback = it.aiFeedback,
                        metrics = it.metrics,
                        dateText = it.dateText
                    )
                }

                _uiState.value = _uiState.value.copy(
                    overallScore = d.overallScore,
                    pointsGainText = d.pointsGainText,
                    scoreRangeText = d.scoreRangeText,
                    totalSessions = d.totalSessions,
                    sessionsWeeklyGainText = d.sessionsWeeklyGainText,
                    timeInvestedText = d.timeInvestedText,
                    timeWeeklyGainText = d.timeWeeklyGainText,
                    skillItems = if (mappedSkills.isNotEmpty()) mappedSkills else _uiState.value.skillItems,
                    recentPracticeScores = mappedRecent,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onSelectTimeframe(timeframe: TimeframeTab) {
        _uiState.value = _uiState.value.copy(selectedTimeframe = timeframe)
    }

    fun selectScoreDetail(item: RecentPracticeScoreItem?) {
        _uiState.value = _uiState.value.copy(selectedScoreDetail = item)
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
