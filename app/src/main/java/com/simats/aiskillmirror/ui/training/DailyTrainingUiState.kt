package com.simats.aiskillmirror.ui.training

data class PracticeItem(
    val id: String,
    val title: String,
    val category: String,
    val durationText: String,
    val xpText: String,
    val isCompleted: Boolean = false,
    val performanceScore: Int? = null,
    val scoreStatus: String = "not_available",
    val scoreReason: String? = null,
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val aiFeedback: String = ""
)

data class WeeklyPlanItem(
    val day: String,
    val focus: String
)

data class PerformanceScoreResult(
    val score: Int?,
    val scoreStatus: String, // "available" or "not_available"
    val scoreReason: String?,
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val aiFeedback: String = "",
    val metrics: Map<String, Any> = emptyMap(),
    val assessmentType: String = "none",
    val taskTitle: String = "",
    val durationMinutes: Int = 0,
    val xpEarned: Int = 35
)

data class DailyTrainingUiState(
    val headerTitle: String = "Daily AI Training",
    val headerCategory: String = "MICRO-LEARNING SESSIONS",
    val headerDateText: String = "TODAY",
    val streakBadgeText: String = "0-day streak",
    val weekDayText: String = "Week 1 · Day 1",
    val goalLabel: String = "Daily training goal",
    val currentMinutes: Int = 0,
    val targetMinutes: Int = 20,
    val totalXp: Int = 0,
    val goalSubtitle: String = "Start today's session to build your streak and earn XP.",
    val practiceProgressText: String = "0 of 0 done",
    val activeSubcategoryName: String = "Guitar",
    val activeMainSkill: String = "music",
    val activeProficiencyLevel: String = "Beginner",
    val activeLearningGoal: String = "Master Core Fundamentals",
    val subcatXp: Int = 0,
    val subcatLessonsCount: Int = 0,
    val subcatMinutes: Int = 0,
    val completedPracticeItems: List<PracticeItem> = emptyList(),
    val pendingPracticeItems: List<PracticeItem> = emptyList(),
    val upNextTitle: String = "Daily Practice · Fretboard Transition",
    val upNextCategory: String = "Music · Guitar Practice",
    val upNextMetaText: String = "10 min · +35 XP",
    val upNextDescription: String = "Keep your fretting wrist slightly curved. Press right behind the fret wire for minimum finger fatigue and zero fret buzz.",
    val startButtonText: String = "Start Practice Session",
    val stepByStepInstructions: List<String> = listOf(
        "Tune all 6 strings (E-A-D-G-B-e) using a standard 440Hz reference.",
        "Warm up finger tips with 1-2-3-4 chromatic crawl across frets 1 to 4.",
        "Practice switching between Em and C major chords with steady downward strums.",
        "Maintain thumb position behind the neck to avoid muting adjacent strings."
    ),
    val beginnerExercises: List<String> = listOf(
        "Open String Pacing (5 mins): Alternate picking at 70 BPM without looking down.",
        "Smooth Chord Shift (5 mins): Transition G to D to Em without breaking cadence."
    ),
    val practiceChallenges: List<String> = listOf(
        "60-Second Strum Sprint: Execute 40 clean, unmuted chord switches in 60 seconds.",
        "Rhythm Recording Test: Record a 4-bar rhythm track and submit for AI tempo analysis."
    ),
    val weeklyLearningPlan: List<WeeklyPlanItem> = emptyList(),
    val aiTip: String = "Focus on slow, perfect repetitions. Speed and fluency are natural side-effects of precision.",
    
    // Active Live Practice Session Timer
    val isSessionActive: Boolean = false,
    val isSessionPaused: Boolean = false,
    val elapsedSeconds: Int = 0,
    val formattedElapsed: String = "00:00",
    val isCompleting: Boolean = false,
    val completionCelebration: String? = null,

    // Real Performance Score & Session Completion Dialog State
    val lastSessionScoreResult: PerformanceScoreResult? = null,
    val showSessionCompletionDialog: Boolean = false,
    val showEvaluationModal: Boolean = false,
    val isEvaluatingArtifact: Boolean = false,
    val codeSnippetInput: String = "",
    val speechTranscriptInput: String = "",
    val selectedItemForDetail: PracticeItem? = null
)

sealed interface DailyTrainingUiEvent {
    object StartPracticeSession : DailyTrainingUiEvent
    object OpenCalendar : DailyTrainingUiEvent
    object SwitchSubcategory : DailyTrainingUiEvent
    object NavigateBack : DailyTrainingUiEvent
}
