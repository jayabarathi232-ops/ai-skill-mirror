package com.simats.aiskillmirror.ui.speech

data class SpeechMetric(
    val name: String,
    val score: Int,
    val maxScore: Int = 100
)

data class SpeechAnalysisUiState(
    val headerTitle: String = "Your voice, understood",
    val headerCategory: String = "SPEECH ANALYSIS",
    val speechScore: Int = 72,
    val baselineGainText: String = "+8 pts from baseline",
    val headlineLine1: String = "Clear ideas.",
    val headlineLine2: String = "Growing confidence.",
    val sessionDurationText: String = "Session 03 · 58 seconds",
    val metrics: List<SpeechMetric> = listOf(
        SpeechMetric("Pronunciation", 76),
        SpeechMetric("Fluency", 70),
        SpeechMetric("Clarity", 70)
    ),
    val transcriptPart1: String = "\"I built a ",
    val transcriptHighlightPronunciation: String = "collaborative",
    val transcriptPart2: String = " study app. My role was to, ",
    val transcriptHighlightFiller: String = "um",
    val transcriptPart3: String = ", design the API and improve the user experience.\"",
    val aiTipText: String = "Try a brief pause instead of \"um.\" Break \"collaborative\" into five syllables: co-lab-o-ra-tive."
)

sealed interface SpeechAnalysisUiEvent {
    object PracticeAgain : SpeechAnalysisUiEvent
    object PlayRecording : SpeechAnalysisUiEvent
    object ShareAnalysis : SpeechAnalysisUiEvent
    object NavigateBack : SpeechAnalysisUiEvent
}
