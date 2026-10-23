package com.simats.aiskillmirror.ui.dashboard

data class DashboardUiState(
    val currentDateText: String = "Tuesday, October 20",
    val userName: String = "Maya",
    val skillScore: Int = 78,
    val monthlyPtsGain: Int = 14,
    val headline: String = "Your future is taking shape.",
    val learnerStatus: String = "Advanced learner · Top 24%",
    val assessedSkillsCount: Int = 4,
    val currentStreakDays: Int = 7,
    val streakSubtitle: String = "Your best week yet",
    val minutesToday: Int = 14,
    val goalMinutesToday: Int = 20,
    val minutesSubtitle: String = "6 min to your goal",
    val currentLessonTitle: String = "Speak with confidence",
    val currentLessonCategory: String = "Communication · Lesson 3 of 5",
    val currentLessonDuration: String = "6 min",
    val currentLessonProgress: Float = 0.6f,
    val aiCoachTitle: String = "Your AI coach is ready",
    val aiCoachSubtitle: String = "\"Let's build on your interview skills.\"",
    val selectedTab: DashboardTab = DashboardTab.HOME
)

enum class DashboardTab {
    HOME,
    ASSESS,
    LEARN,
    PROGRESS,
    PROFILE
}

sealed interface DashboardUiEvent {
    object ViewSkillMirror : DashboardUiEvent
    object ViewPlan : DashboardUiEvent
    object ContinueTraining : DashboardUiEvent
    object OpenAiCoach : DashboardUiEvent
    object OpenNotifications : DashboardUiEvent
}
