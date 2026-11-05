package com.simats.aiskillmirror.ui.dashboard

data class SkillItem(
    val id: String,
    val name: String,
    val category: String,
    val score: Int,
    val level: String,
    val iconEmoji: String,
    val accentColor: Long,
    val progress: Float
)

data class PerformanceHistoryItem(
    val id: String,
    val skillName: String,
    val activity: String,
    val score: Int,
    val dateText: String,
    val iconEmoji: String,
    val feedback: String
)

data class WeeklyDayProgress(
    val day: String,
    val hours: Float,
    val isToday: Boolean = false
)

data class RecommendedPracticeItem(
    val id: String,
    val title: String,
    val skill: String,
    val durationText: String,
    val difficulty: String,
    val iconEmoji: String
)

data class DashboardUiState(
    val currentDateText: String = "Wednesday, November 4",
    val userName: String = "Maya",
    val profilePicture: String = "",
    val skillScore: Int = 84,
    val monthlyPtsGain: Int = 14,
    val headline: String = "Multi-Skill Competency Mirror",
    val learnerStatus: String = "Master Level 4 · Top 5%",
    val assessedSkillsCount: Int = 5,
    val currentStreakDays: Int = 7,
    val streakSubtitle: String = "Your best week yet",
    val minutesToday: Int = 45,
    val goalMinutesToday: Int = 60,
    val minutesSubtitle: String = "15 min to daily target",
    val currentLessonTitle: String = "Dynamic Algorithm Pacing",
    val currentLessonCategory: String = "Coding · Module 4",
    val currentLessonDuration: String = "15 min",
    val currentLessonProgress: Float = 0.75f,
    val aiCoachTitle: String = "Coach Aria is ready",
    val aiCoachSubtitle: String = "\"Your speech clarity reached 88%! Let's focus on structured presentation delivery next.\"",
    val greeting: String = "Welcome back,",
    val currentPlanTitle: String = "Multi-Skill Mastery",
    val selectedTab: DashboardTab = DashboardTab.HOME,
    
    // My Core Skills
    val mySkills: List<SkillItem> = listOf(
        SkillItem("speaking", "Public Speaking", "Speech & Articulation", 86, "Advanced", "🎙️", 0xFF8B5CF6, 0.86f),
        SkillItem("coding", "Coding", "Algorithms & Logic", 92, "Master", "💻", 0xFF6366F1, 0.92f),
        SkillItem("music", "Music", "Pitch & Cadence", 88, "Pro", "🎵", 0xFFA855F7, 0.88f),
        SkillItem("photo", "Photography", "Composition & Light", 75, "Intermediate", "📷", 0xFF06B6D4, 0.75f)
    ),

    // Recent Performance History
    val recentHistory: List<PerformanceHistoryItem> = listOf(
        PerformanceHistoryItem("1", "Public Speaking", "Impromptu Speech Clarity Drill", 86, "Today, 11:15 AM", "🎙️", "WPM: 142 · Clarity: 88% · Minimal fillers"),
        PerformanceHistoryItem("2", "Coding", "Tree Traversal Algorithm Test", 92, "Yesterday", "💻", "Optimal complexity achieved"),
        PerformanceHistoryItem("3", "Music", "Vocal Pitch & Harmonic Scale", 88, "Nov 2", "🎵", "98% note accuracy achieved"),
        PerformanceHistoryItem("4", "Photography", "Rule of Thirds & Lighting Study", 75, "Nov 1", "📷", "Golden hour framing accuracy 94%")
    ),

    // Weekly Progress Chart Data
    val weeklyProgress: List<WeeklyDayProgress> = listOf(
        WeeklyDayProgress("M", 1.8f),
        WeeklyDayProgress("T", 2.4f),
        WeeklyDayProgress("W", 3.2f, isToday = true),
        WeeklyDayProgress("T", 1.5f),
        WeeklyDayProgress("F", 2.8f),
        WeeklyDayProgress("S", 3.6f),
        WeeklyDayProgress("S", 2.1f)
    ),

    // Today's Recommended Practice
    val recommendedPractice: List<RecommendedPracticeItem> = listOf(
        RecommendedPracticeItem("1", "60s Elevator Pitch Practice", "Public Speaking", "6 min", "Medium", "🎙️"),
        RecommendedPracticeItem("2", "Dynamic Programming Sprint", "Coding", "15 min", "Hard", "💻"),
        RecommendedPracticeItem("3", "Interval Ear Training & Scales", "Music", "8 min", "Easy", "🎵")
    ),

    // Multi-modal Upload & Analysis
    val uploadedMediaName: String? = null,
    val uploadedMediaType: String? = null,
    val isAnalyzing: Boolean = false,
    val uploadFeedbackMessage: String? = null,

    // Notifications
    val unreadNotificationsCount: Int = 3
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
    data class ShowToast(val message: String) : DashboardUiEvent
}
