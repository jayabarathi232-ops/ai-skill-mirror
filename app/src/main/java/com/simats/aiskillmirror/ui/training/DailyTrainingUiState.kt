package com.simats.aiskillmirror.ui.training

data class PracticeItem(
    val id: String,
    val title: String,
    val category: String,
    val durationText: String,
    val xpText: String,
    val isCompleted: Boolean = false
)

data class WeeklyPlanItem(
    val day: String,
    val focus: String
)

data class DailyTrainingUiState(
    val headerTitle: String = "Daily AI Training",
    val headerCategory: String = "MICRO-LEARNING SESSIONS",
    val headerDateText: String = "TODAY",
    val streakBadgeText: String = "7-day streak",
    val weekDayText: String = "Week 2 · Day 2",
    val goalLabel: String = "Daily training goal",
    val currentMinutes: Int = 14,
    val targetMinutes: Int = 20,
    val totalXp: Int = 1240,
    val goalSubtitle: String = "Complete today's session to keep your momentum.",
    val practiceProgressText: String = "2 of 3 done",
    val activeSubcategoryName: String = "Guitar",
    val activeMainSkill: String = "music",
    val activeProficiencyLevel: String = "Beginner",
    val activeLearningGoal: String = "Master Core Chords & Rhythms",
    val subcatXp: Int = 140,
    val subcatLessonsCount: Int = 4,
    val subcatMinutes: Int = 35,
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
    val pendingPracticeItems: List<PracticeItem> = emptyList(),
    val upNextTitle: String = "Daily Guitar Practice · Fretboard Transition",
    val upNextCategory: String = "Music · Guitar Practice",
    val upNextMetaText: String = "10 min · +35 XP",
    val upNextDescription: String = "Keep your fretting wrist slightly curved. Press right behind the fret wire for minimum finger fatigue and zero fret buzz.",
    val startButtonText: String = "Start 10-minute practice",
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
    val weeklyLearningPlan: List<WeeklyPlanItem> = listOf(
        WeeklyPlanItem("Day 1", "Fretboard Familiarity & Tuning Accuracy"),
        WeeklyPlanItem("Day 2", "Open Chords (Em, G, C, D) Clean Fretting"),
        WeeklyPlanItem("Day 3", "Strumming Patterns & Down-Up Accents"),
        WeeklyPlanItem("Day 4", "Metronome Synchronization at 80 BPM"),
        WeeklyPlanItem("Day 5", "Fingerstyle Basics (P-I-M-A Technique)"),
        WeeklyPlanItem("Day 6", "Transition Speed Drills"),
        WeeklyPlanItem("Day 7", "Full Acoustic Song Performance Review")
    ),
    val aiTip: String = "Keep your fretting wrist slightly curved. Press right behind the fret wire for minimum finger fatigue and zero fret buzz.",
    val isCompleting: Boolean = false,
    val completionCelebration: String? = null
)

sealed interface DailyTrainingUiEvent {
    object StartPracticeSession : DailyTrainingUiEvent
    object OpenCalendar : DailyTrainingUiEvent
    object SwitchSubcategory : DailyTrainingUiEvent
    object NavigateBack : DailyTrainingUiEvent
}
