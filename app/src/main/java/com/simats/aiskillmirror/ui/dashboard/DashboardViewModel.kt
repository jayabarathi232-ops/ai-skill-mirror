package com.simats.aiskillmirror.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.repository.DashboardRepository
import com.simats.aiskillmirror.data.repository.ProgressRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository = DashboardRepository(),
    private val progressRepository: ProgressRepository = ProgressRepository()
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
            val notifRes = progressRepository.getNotifications(UserSession.userId)
            val unreadCount = if (notifRes.isSuccess && notifRes.data != null) notifRes.data.unreadCount else 3

            val result = dashboardRepository.getDashboard(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data

                val mySkillsList = if (d.mySkills.isNotEmpty()) {
                    d.mySkills.map { sk ->
                        SkillItem(
                            id = sk.id,
                            name = sk.name,
                            category = sk.category,
                            score = sk.score,
                            level = sk.level,
                            iconEmoji = sk.iconEmoji,
                            accentColor = sk.accentColor,
                            progress = sk.progress
                        )
                    }
                } else _uiState.value.mySkills

                val historyList = if (d.recentHistory.isNotEmpty()) {
                    d.recentHistory.map { h ->
                        PerformanceHistoryItem(
                            id = h.id,
                            skillName = h.skillName,
                            activity = h.activity,
                            score = h.score,
                            dateText = h.dateText,
                            iconEmoji = h.iconEmoji,
                            feedback = h.feedback
                        )
                    }
                } else _uiState.value.recentHistory

                _uiState.value = _uiState.value.copy(
                    userName = if (d.userName.isNotBlank()) d.userName else _uiState.value.userName,
                    skillScore = if (d.skillScore > 0) d.skillScore else _uiState.value.skillScore,
                    monthlyPtsGain = if (d.monthlyPtsGain > 0) d.monthlyPtsGain else _uiState.value.monthlyPtsGain,
                    currentStreakDays = d.currentStreakDays,
                    minutesToday = d.minutesToday,
                    goalMinutesToday = if (d.goalMinutesToday > 0) d.goalMinutesToday else 20,
                    assessedSkillsCount = if (d.assessedSkillsCount > 0) d.assessedSkillsCount else 3,
                    learnerStatus = if (d.learnerStatus.isNotBlank()) d.learnerStatus else _uiState.value.learnerStatus,
                    currentLessonTitle = if (d.currentLessonTitle.isNotBlank()) d.currentLessonTitle else _uiState.value.currentLessonTitle,
                    currentLessonCategory = if (d.currentLessonCategory.isNotBlank()) d.currentLessonCategory else _uiState.value.currentLessonCategory,
                    currentLessonDuration = if (d.currentLessonDuration.isNotBlank()) d.currentLessonDuration else _uiState.value.currentLessonDuration,
                    greeting = if (d.greeting.isNotBlank()) d.greeting else _uiState.value.greeting,
                    currentDateText = if (d.currentDateText.isNotBlank()) d.currentDateText else _uiState.value.currentDateText,
                    headline = if (d.headline.isNotBlank()) d.headline else _uiState.value.headline,
                    streakSubtitle = if (d.streakSubtitle.isNotBlank()) d.streakSubtitle else _uiState.value.streakSubtitle,
                    minutesSubtitle = if (d.minutesSubtitle.isNotBlank()) d.minutesSubtitle else _uiState.value.minutesSubtitle,
                    currentPlanTitle = if (d.currentPlanTitle.isNotBlank()) d.currentPlanTitle else _uiState.value.currentPlanTitle,
                    aiCoachTitle = if (d.aiCoachTitle.isNotBlank()) d.aiCoachTitle else _uiState.value.aiCoachTitle,
                    aiCoachSubtitle = if (d.aiCoachSubtitle.isNotBlank()) d.aiCoachSubtitle else _uiState.value.aiCoachSubtitle,
                    profilePicture = if (d.profilePicture.isNotBlank()) d.profilePicture else UserSession.profilePictureUrl,
                    unreadNotificationsCount = unreadCount,
                    mySkills = mySkillsList,
                    recentHistory = historyList
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    unreadNotificationsCount = unreadCount,
                    profilePicture = UserSession.profilePictureUrl
                )
            }

            // Fetch real weekly progress for dashboard chart
            try {
                val wpRes = ApiClient.fetchWeeklyProgress(UserSession.userId, 0)
                if (wpRes.isSuccess && wpRes.data != null) {
                    val wp = wpRes.data
                    val list = wp.days.map { day ->
                        val hrs = Math.round((day.minutes / 60.0f) * 10f) / 10f
                        WeeklyDayProgress(
                            day = day.day.firstOrNull()?.toString() ?: "M",
                            hours = if (hrs > 0f) hrs else 0f,
                            isToday = day.isToday
                        )
                    }
                    val totalHrs = Math.round((wp.totalWeeklyMinutes / 60.0f) * 10f) / 10f
                    _uiState.value = _uiState.value.copy(
                        weeklyProgress = list,
                        totalWeeklyHoursText = "$totalHrs hrs total"
                    )
                }
            } catch (ignored: Exception) {}
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
            _uiState.value = _uiState.value.copy(isUploading = true, uploadProgress = 0.05f)

            for (i in 1..10) {
                delay(120)
                _uiState.value = _uiState.value.copy(uploadProgress = i * 0.1f)
            }

            val result = dashboardRepository.uploadMedia(UserSession.userId, type, name)
            _uiState.value = _uiState.value.copy(isUploading = false, uploadProgress = 0f)

            if (result.isSuccess && result.data != null) {
                val item = PerformanceHistoryItem(
                    id = result.data.id.toString(),
                    skillName = when (result.data.skillCategory.lowercase()) {
                        "photo", "photography" -> "Photography"
                        "coding" -> "Coding"
                        else -> "Music"
                    },
                    activity = result.data.title,
                    score = result.data.score,
                    dateText = "Just now",
                    iconEmoji = when (type.lowercase()) {
                        "video" -> "📹"
                        "image", "photo" -> "📷"
                        else -> "🎙️"
                    },
                    feedback = result.data.analysisSummary
                )
                val updated = listOf(item) + _uiState.value.recentHistory
                _uiState.value = _uiState.value.copy(recentHistory = updated)
                _eventChannel.send(DashboardUiEvent.ShowToast("Uploaded successfully! AI score: ${result.data.score}/100"))
            } else {
                _eventChannel.send(DashboardUiEvent.ShowToast(result.message ?: "Upload simulated: Saved locally"))
            }
        }
    }

    fun onNotificationsClick() {
        viewModelScope.launch {
            _eventChannel.send(DashboardUiEvent.OpenNotifications)
        }
    }

    fun onViewSkillMirrorClick() {
        viewModelScope.launch {
            _eventChannel.send(DashboardUiEvent.ViewSkillMirror)
        }
    }

    fun onOpenAiCoachClick() {
        viewModelScope.launch {
            _eventChannel.send(DashboardUiEvent.OpenAiCoach)
        }
    }

    fun onRefresh() {
        loadDashboard()
    }
}
