package com.simats.aiskillmirror.ui.progress

enum class TimeframeTab {
    WEEK,
    MONTH,
    ALL_TIME
}

data class ProgressSkillItem(
    val name: String,
    val score: Int,
    val maxScore: Int = 100,
    val isTopTier: Boolean = false
)

data class RecentPracticeScoreItem(
    val id: Int,
    val taskTitle: String,
    val mainSkill: String,
    val subcategoryName: String,
    val durationText: String,
    val xpText: String,
    val score: Int?,
    val scoreStatus: String,
    val scoreReason: String?,
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val aiFeedback: String = "",
    val metrics: Map<String, Any> = emptyMap(),
    val dateText: String
)

data class ProgressAnalyticsUiState(
    val headerTitle: String = "Progress you can see",
    val headerCategory: String = "YOUR GROWTH, IN FOCUS",
    val selectedTimeframe: TimeframeTab = TimeframeTab.MONTH,
    val overallScore: Int = 78,
    val pointsGainText: String = "↑ 14 points",
    val scoreRangeText: String = "From 64 to 78 · Sep 29 – Oct 20",
    val totalSessions: Int = 0,
    val sessionsWeeklyGainText: String = "↑ 0 this week",
    val timeInvestedText: String = "0.0 h",
    val timeWeeklyGainText: String = "↑ 0h this week",
    val skillItems: List<ProgressSkillItem> = emptyList(),
    val recentPracticeScores: List<RecentPracticeScoreItem> = emptyList(),
    val isLoading: Boolean = false,
    val selectedScoreDetail: RecentPracticeScoreItem? = null
)

sealed interface ProgressAnalyticsUiEvent {
    object ViewReport : ProgressAnalyticsUiEvent
    object OpenCalendar : ProgressAnalyticsUiEvent
    object NavigateBack : ProgressAnalyticsUiEvent
}
