package com.simats.aiskillmirror.ui.training

data class PracticeItem(
    val id: String,
    val title: String,
    val category: String,
    val durationText: String,
    val xpText: String,
    val isCompleted: Boolean = false
)

data class DailyTrainingUiState(
    val headerTitle: String = "A little better, daily",
    val headerDateText: String = "TUESDAY, OCTOBER 20",
    val streakBadgeText: String = "7-day streak",
    val weekDayText: String = "Week 2 · Day 2",
    val goalLabel: String = "Daily training goal",
    val currentMinutes: Int = 14,
    val targetMinutes: Int = 20,
    val goalSubtitle: String = "One more session to keep your momentum.",
    val practiceProgressText: String = "2 of 3 done",
    val completedPracticeItems: List<PracticeItem> = listOf(
        PracticeItem(
            id = "1",
            title = "Active listening essentials",
            category = "Communication · 6 min",
            durationText = "6 min",
            xpText = "+30 XP",
            isCompleted = true
        ),
        PracticeItem(
            id = "2",
            title = "Think in patterns",
            category = "Problem solving · 8 min",
            durationText = "8 min",
            xpText = "+40 XP",
            isCompleted = true
        )
    ),
    val upNextTitle: String = "Tell your project story",
    val upNextCategory: String = "Speech training · Interview practice",
    val upNextMetaText: String = "6 min · +30 XP",
    val upNextDescription: String = "Use the STAR method to turn your experience into a compelling answer.",
    val startButtonText: String = "Start 6-minute practice"
)

sealed interface DailyTrainingUiEvent {
    object StartPracticeSession : DailyTrainingUiEvent
    object OpenCalendar : DailyTrainingUiEvent
    object NavigateBack : DailyTrainingUiEvent
}
