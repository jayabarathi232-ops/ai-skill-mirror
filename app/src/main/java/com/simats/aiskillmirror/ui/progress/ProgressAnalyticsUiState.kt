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

data class ProgressAnalyticsUiState(
    val headerTitle: String = "Progress you can see",
    val headerCategory: String = "YOUR GROWTH, IN FOCUS",
    val selectedTimeframe: TimeframeTab = TimeframeTab.MONTH,
    val overallScore: Int = 78,
    val pointsGainText: String = "↑ 14 points",
    val scoreRangeText: String = "From 64 to 78 · Sep 29 – Oct 20",
    val totalSessions: Int = 24,
    val sessionsWeeklyGainText: String = "↑ 8 this week",
    val timeInvestedText: String = "6.2 h",
    val timeWeeklyGainText: String = "↑ 1.2h this week",
    val skillItems: List<ProgressSkillItem> = listOf(
        ProgressSkillItem("Technical skills", 86, isTopTier = true),
        ProgressSkillItem("Problem solving", 80, isTopTier = true),
        ProgressSkillItem("Interview readiness", 74, isTopTier = false),
        ProgressSkillItem("Communication", 72, isTopTier = false)
    )
)

sealed interface ProgressAnalyticsUiEvent {
    object ViewReport : ProgressAnalyticsUiEvent
    object OpenCalendar : ProgressAnalyticsUiEvent
    object NavigateBack : ProgressAnalyticsUiEvent
}
