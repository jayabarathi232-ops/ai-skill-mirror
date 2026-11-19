package com.simats.aiskillmirror.ui.training

import com.simats.aiskillmirror.data.network.CodeTestCaseResult

enum class SkillType {
    MUSIC,
    PHOTOGRAPHY,
    CODING
}

enum class VocalAnalysisState {
    IDLE,
    ANALYSIS_PENDING,
    ANALYSIS_SUCCESS,
    ANALYSIS_FAILED
}

data class DrawingStrokePoint(
    val x: Float,
    val y: Float,
    val isEndOfStroke: Boolean = false,
    val color: Long = 0xFF7545C8,
    val strokeWidth: Float = 6f
)

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
    val aiFeedback: String = "",
    val metrics: Map<String, Any> = emptyMap(),
    val assessmentType: String = ""
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
    
    // Active Live Practice Session Timer (Used for Music & Public Speaking)
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
    val selectedImagePath: String? = null,
    val selectedItemForDetail: PracticeItem? = null,
    val showItemDetailDialog: Boolean = false,

    // 1. Music Vocal Practice
    val isRecordingVoice: Boolean = false,
    val isRecordingPaused: Boolean = false,
    val recordedAudioPath: String? = null,
    val voiceRecordSeconds: Int = 0,
    val isPlayingVoice: Boolean = false,
    val audioRecordError: String? = null,
    val evaluationError: String? = null,
    val canRetryAnalysis: Boolean = false,
    val isAnalysisComplete: Boolean = false,
    val showVocalReviewDialog: Boolean = false,
    val vocalAnalysisState: VocalAnalysisState = VocalAnalysisState.IDLE,
    val liveAmplitudeWaveforms: List<Float> = listOf(0.2f, 0.5f, 0.8f, 0.4f, 0.9f, 0.6f, 0.3f, 0.7f),

    // 1b. Music Instrument Practice (Metronome & Recording)
    val metronomeBpm: Int = 80,
    val isMetronomeActive: Boolean = false,
    val currentBeat: Int = 1,
    val isRecordingInstrument: Boolean = false,

    // 2. Photography Practice
    val capturedPhotoPath: String? = null,
    val isCameraActive: Boolean = false,

    // 3. Drawing Practice
    val drawingStrokes: List<DrawingStrokePoint> = emptyList(),
    val selectedDrawingTool: String = "PENCIL", // "PENCIL", "BRUSH", "ERASER"
    val selectedDrawingColor: Long = 0xFF25213AL, // Charcoal Black
    val drawingStrokeWidth: Float = 6f,
    val savedDrawingPath: String? = null,

    // 4. Coding Practice
    val selectedLanguage: String = "python", // "python", "java", "javascript", "cpp", "c"
    val codeEditorText: String = "def solution():\n    # Write your code here\n    print(\"Hello World\")\n    return True\n\nsolution()\n",
    val codingOutput: String = "",
    val codingError: String = "",
    val isCodeRunning: Boolean = false,
    val codeTestCases: List<CodeTestCaseResult> = emptyList(),
    val codeExplanation: String = "",

    // 5. Public Speaking Practice
    val isRecordingSpeech: Boolean = false,
    val speechRecordSeconds: Int = 0,
    val recordedSpeechAudioPath: String? = null,
    val speechPromptText: String = "Deliver a 60-second elevator pitch introducing your core passion, an achieved project milestone, and your future ambition."
) {
    val isSubmittingEvaluation: Boolean
        get() = isEvaluatingArtifact

    val detectedSkillType: SkillType
        get() {
            val skill = activeMainSkill.lowercase().trim()
            val sub = activeSubcategoryName.lowercase().trim()
            val cat = upNextCategory.lowercase().trim()
            val title = upNextTitle.lowercase().trim()

            return when {
                skill.contains("photo") || sub.contains("photo") || cat.contains("photo") || title.contains("photo") ||
                sub.contains("camera") || sub.contains("portrait_photo") || sub.contains("landscape_photo") -> SkillType.PHOTOGRAPHY

                skill.contains("cod") || sub.contains("cod") || cat.contains("cod") || title.contains("cod") ||
                skill.contains("program") || sub.contains("program") || sub.contains("python") || sub.contains("java") ||
                sub.contains("script") || sub.contains("c++") || sub.contains("c_prog") || sub.contains("algo") -> SkillType.CODING

                else -> SkillType.MUSIC
            }
        }

    val isVocalSkill: Boolean
        get() {
            val skill = activeMainSkill.lowercase().trim()
            val sub = activeSubcategoryName.lowercase().trim()
            val title = upNextTitle.lowercase().trim()
            val cat = upNextCategory.lowercase().trim()
            val isMusic = skill.contains("music") || cat.contains("music")
            val hasVocal = sub.contains("sing") || sub.contains("vocal") || (sub.contains("voice") && isMusic) ||
                    title.contains("sing") || title.contains("vocal") ||
                    cat.contains("sing") || cat.contains("vocal")
            return (isMusic && hasVocal) || sub == "singing"
        }

    val formattedVoiceRecordDuration: String
        get() {
            val m = voiceRecordSeconds / 60
            val s = voiceRecordSeconds % 60
            return String.format("%02d:%02d", m, s)
        }

    val formattedSpeechRecordDuration: String
        get() {
            val m = speechRecordSeconds / 60
            val s = speechRecordSeconds % 60
            return String.format("%02d:%02d", m, s)
        }
}

sealed interface DailyTrainingUiEvent {
    object StartPracticeSession : DailyTrainingUiEvent
    object OpenCalendar : DailyTrainingUiEvent
    object SwitchSubcategory : DailyTrainingUiEvent
    object NavigateBack : DailyTrainingUiEvent
}
