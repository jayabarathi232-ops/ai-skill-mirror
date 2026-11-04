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
    val aiCoachSubtitle: String = "\"Your dance tempo improved by 14%! Let's focus on coding dynamic programming next.\"",
    val greeting: String = "Welcome back,",
    val currentPlanTitle: String = "Multi-Skill Mastery",
    val selectedTab: DashboardTab = DashboardTab.HOME,
    
    // My 5 Core Skills
    val mySkills: List<SkillItem> = listOf(
        SkillItem("sports", "Sports", "Agility & Reflexes", 82, "Advanced", "⚽", 0xFF10B981, 0.82f),
        SkillItem("dance", "Dance", "Rhythm & Posture", 78, "Intermediate", "💃", 0xFFEC4899, 0.78f),
        SkillItem("music", "Music", "Pitch & Cadence", 88, "Pro", "🎵", 0xFFA855F7, 0.88f),
        SkillItem("photo", "Photography", "Composition & Light", 75, "Intermediate", "📷", 0xFF06B6D4, 0.75f),
        SkillItem("coding", "Coding", "Algorithms & Logic", 92, "Master", "💻", 0xFF6366F1, 0.92f)
    ),

    // Recent Performance History
    val recentHistory: List<PerformanceHistoryItem> = listOf(
        PerformanceHistoryItem("1", "Coding", "Tree Traversal Algorithm Test", 92, "Today, 10:30 AM", "💻", "Optimal complexity achieved"),
        PerformanceHistoryItem("2", "Dance", "Choreography Balance & Posture", 78, "Yesterday", "💃", "Great fluid body alignment"),
        PerformanceHistoryItem("3", "Music", "Vocal Pitch & Harmonic Scale", 88, "Nov 2", "🎵", "98% note accuracy achieved"),
        PerformanceHistoryItem("4", "Sports", "Sprint Agility & Footwork Drill", 82, "Nov 1", "⚽", "Reaction time improved +12%")
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
        RecommendedPracticeItem("1", "Dynamic Programming Sprint", "Coding", "15 min", "Hard", "💻"),
        RecommendedPracticeItem("2", "Cadence & Rhythm Sync", "Dance", "10 min", "Medium", "💃"),
        RecommendedPracticeItem("3", "Interval Ear Training & Scales", "Music", "8 min", "Easy", "🎵")
    ),

    // Multi-modal Upload & Analysis
    val uploadedMediaName: String? = null,
    val uploadedMediaType: String? = null,
    val isAnalyzing: Boolean = false,
    val uploadFeedbackMessage: String? = null
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
