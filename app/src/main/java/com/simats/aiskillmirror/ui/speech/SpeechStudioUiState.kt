package com.simats.aiskillmirror.ui.speech

enum class SpeechTab {
    DASHBOARD,
    STUDIO,
    ANALYSIS
}

enum class PublicSpeakingModule(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val badge: String
) {
    SPEECH_PRACTICE(
        "speech_practice",
        "Speech Practice",
        "Practise speaking on diverse AI-generated topics",
        "🎙️",
        "CORE DRILL"
    ),
    VOICE_ANALYSIS(
        "voice_analysis",
        "Voice Analysis",
        "Analyse speaking speed, pauses, clarity & pitch",
        "📊",
        "ACOUSTIC AI"
    ),
    CONFIDENCE_TRAINING(
        "confidence",
        "Confidence Training",
        "Beginner-friendly exercises & vocal projection",
        "💪",
        "FOUNDATION"
    ),
    VOCABULARY_IMPROVEMENT(
        "vocabulary",
        "Vocabulary Booster",
        "Elevate words, eliminate fillers & refine structure",
        "📚",
        "EXPRESSION"
    ),
    PRONUNCIATION_PRACTICE(
        "pronunciation",
        "Pronunciation Practice",
        "Clear phonetic articulation & syllable drills",
        "🗣️",
        "ARTICULATION"
    ),
    PRESENTATION_TRAINING(
        "presentation",
        "Presentation Training",
        "Delivery pacing, hook mastery & story structure",
        "🎯",
        "DELIVERY"
    ),
    INTERVIEW_PRACTICE(
        "interview",
        "Interview Practice",
        "Mock interview questions & STAR methodology",
        "💼",
        "CAREER READY"
    ),
    PROGRESS_TRACKING(
        "progress_tracking",
        "Progress Tracking",
        "Improvement across previous practice sessions",
        "📈",
        "ANALYTICS"
    )
}

data class SpeechPracticeSessionItem(
    val id: String,
    val moduleName: String,
    val topicTitle: String,
    val topicQuote: String,
    val durationSeconds: Int,
    val overallScore: Int,
    val pronunciationScore: Int,
    val fluencyScore: Int,
    val clarityScore: Int,
    val wpm: Int,
    val transcript: String,
    val feedback: String,
    val dateText: String
)

data class SpeechSessionAnalysis(
    val wordsCount: Int = 86,
    val wpm: Int = 142,
    val paceRating: String = "Optimal Pacing (130-155 WPM)",
    val fillerCount: Int = 1,
    val detectedFillers: List<String> = listOf("um"),
    val clarityScore: Int = 88,
    val pronunciationScore: Int = 84,
    val fluencyScore: Int = 82,
    val overallScore: Int = 86,
    val transcript: String = "I built an intelligent multi-modal tracking app. My focus was to design the asynchronous real-time architecture, optimize state handling, and improve responsiveness.",
    val personalizedFeedback: String = "Exceptional articulation! Your conversational pacing was steady at 142 WPM with only one minor pause filler. The structural delivery clearly emphasized impact and ownership.",
    val strengths: List<String> = listOf(
        "Consistent steady cadence (142 WPM)",
        "Crisp consonant pronunciation on technical terms",
        "Clear STAR methodology transition"
    ),
    val areasForImprovement: List<String> = listOf(
        "Allow a 1.5-second conscious silence instead of saying 'um'",
        "Inflect upward on key metric numbers for emphasis"
    ),
    val suggestedDailyTask: String = "Practise the 30-Second Elevator Pitch with conscious 2-second micro-pauses.",
    val comparisonWithPrevious: String = "+4% Clarity · -2 Filler Words compared to previous session"
)

data class SpeechStudioUiState(
    val currentTab: SpeechTab = SpeechTab.DASHBOARD,
    val currentModule: PublicSpeakingModule = PublicSpeakingModule.SPEECH_PRACTICE,

    // Public Speaking Dashboard
    val totalPracticeSessions: Int = 18,
    val speakingProgress: Int = 86,
    val speechClarityFeedback: String = "88% · Crisp articulation & structured flow",
    val speakingSpeed: String = "142 WPM (Optimal Conversational Range)",
    val pronunciationFeedback: String = "84% · Clear vowel balance",
    val recommendedNextExercise: String = "60-Second Elevator Pitch with Zero Filler Words",
    val recommendedNextCategory: String = "Interview Practice · Module 7",
    val recommendedNextDuration: String = "60 sec",

    // Practice Sessions History
    val historyList: List<SpeechPracticeSessionItem> = listOf(
        SpeechPracticeSessionItem(
            id = "1",
            moduleName = "Interview Practice",
            topicTitle = "Tell me about a challenging project",
            topicQuote = "Explain how you solved a scalable architecture race condition.",
            durationSeconds = 58,
            overallScore = 88,
            pronunciationScore = 86,
            fluencyScore = 84,
            clarityScore = 90,
            wpm = 144,
            transcript = "I led the backend refactoring for our streaming pipeline, eliminating thread contentions.",
            feedback = "Superb STAR delivery. Pacing was in the golden 140-150 WPM zone.",
            dateText = "Today, 11:20 AM"
        ),
        SpeechPracticeSessionItem(
            id = "2",
            moduleName = "Speech Practice",
            topicTitle = "The Power of Daily Micro-Habits",
            topicQuote = "Why consistent 15-minute practice beats sporadic marathon sessions.",
            durationSeconds = 45,
            overallScore = 84,
            pronunciationScore = 82,
            fluencyScore = 80,
            clarityScore = 86,
            wpm = 138,
            transcript = "Small 1% daily increments compound into profound mastery over time.",
            feedback = "Compelling emotional hook and zero filler words detected.",
            dateText = "Yesterday"
        ),
        SpeechPracticeSessionItem(
            id = "3",
            moduleName = "Presentation Training",
            topicTitle = "Opening Hook Architecture",
            topicQuote = "Capture audience attention in the first 15 seconds.",
            durationSeconds = 60,
            overallScore = 82,
            pronunciationScore = 80,
            fluencyScore = 78,
            clarityScore = 84,
            wpm = 132,
            transcript = "Did you know that 85% of audiences decide their impression within 20 seconds?",
            feedback = "Strong opening question. Work on louder vocal projection at the end.",
            dateText = "Nov 3"
        )
    ),

    // Active Recording Studio
    val activePromptTitle: String = "Tell me about a project you're proud of.",
    val activePromptInstruction: String = "Use the STAR method: Situation, Task, Action, Result. Aim for a clear, focused response in 60 seconds.",
    val targetDurationSeconds: Int = 60,
    val elapsedSeconds: Int = 0,
    val timerText: String = "00:00",
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val isAnalyzing: Boolean = false,
    val isGeneratingTopic: Boolean = false,
    val liveTranscript: String = "",
    val audioRmsLevel: Float = 0f,
    val privacyNoticeText: String = "A safe space to practice. Audio recordings are private and analyzed on-device.",

    // Analysis Result
    val latestAnalysis: SpeechSessionAnalysis = SpeechSessionAnalysis(),
    val errorMessage: String? = null
)

sealed interface SpeechStudioUiEvent {
    data class ShowToast(val message: String) : SpeechStudioUiEvent
    object PracticeFinished : SpeechStudioUiEvent
    object NavigateBack : SpeechStudioUiEvent
}
