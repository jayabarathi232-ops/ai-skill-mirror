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

            // 1. Fetch general progress analytics
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

            // 2. Fetch real weekly progress for interactive day selection
            try {
                val wpRes = ApiClient.fetchWeeklyProgress(UserSession.userId, _uiState.value.currentWeekOffset)
                if (wpRes.isSuccess && wpRes.data != null) {
                    val wp = wpRes.data
                    val todayIdx = if (wp.days.isNotEmpty()) {
                        val found = wp.days.indexOfFirst { it.isToday }
                        if (found >= 0) found else 0
                    } else -1
                    val selDay = if (todayIdx in wp.days.indices) wp.days[todayIdx] else null
                    _uiState.value = _uiState.value.copy(
                        weeklyProgress = wp,
                        selectedDayIndex = todayIdx,
                        selectedDay = selDay
                    )
                }
            } catch (ignored: Exception) {}
        }
    }

    fun selectDay(index: Int) {
        val days = if (_uiState.value.weeklyProgress.days.size == 7) {
            _uiState.value.weeklyProgress.days
        } else {
            com.simats.aiskillmirror.data.network.createDefaultWeekDays(_uiState.value.currentWeekOffset)
        }
        if (index in days.indices) {
            val selected = days[index]
            val updatedWp = if (_uiState.value.weeklyProgress.days.size != 7) {
                _uiState.value.weeklyProgress.copy(days = days)
            } else {
                _uiState.value.weeklyProgress
            }
            _uiState.value = _uiState.value.copy(
                weeklyProgress = updatedWp,
                selectedDayIndex = index,
                selectedDay = selected
            )
        }
    }

    fun changeWeekOffset(delta: Int) {
        val newOffset = _uiState.value.currentWeekOffset + delta
        val initialDays = com.simats.aiskillmirror.data.network.createDefaultWeekDays(newOffset)
        val todayIdx = initialDays.indexOfFirst { it.isToday }.let { if (it >= 0) it else 0 }
        _uiState.value = _uiState.value.copy(
            currentWeekOffset = newOffset,
            weeklyProgress = _uiState.value.weeklyProgress.copy(
                weekOffset = newOffset,
                days = initialDays
            ),
            selectedDayIndex = todayIdx,
            selectedDay = initialDays.getOrNull(todayIdx),
            isLoadingWeeklyProgress = true
        )
        viewModelScope.launch {
            val res = ApiClient.fetchWeeklyProgress(UserSession.userId, newOffset)
            _uiState.value = _uiState.value.copy(isLoadingWeeklyProgress = false)
            if (res.isSuccess && res.data != null) {
                val wp = res.data
                val resolvedIdx = if (wp.days.isNotEmpty()) {
                    val found = wp.days.indexOfFirst { it.isToday }
                    if (found >= 0) found else 0
                } else 0
                val selDay = wp.days.getOrNull(resolvedIdx) ?: initialDays.getOrNull(resolvedIdx)
                _uiState.value = _uiState.value.copy(
                    weeklyProgress = wp,
                    selectedDayIndex = resolvedIdx,
                    selectedDay = selDay
                )
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
