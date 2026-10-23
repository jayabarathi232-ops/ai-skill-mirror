package com.simats.aiskillmirror.ui.speech

data class SpeechStudioUiState(
    val title: String = "Speak with confidence",
    val subtitle: String = "AI SPEECH STUDIO",
    val categoryBadgeText: String = "INTERVIEW PRACTICE",
    val sessionText: String = "Session 03",
    val promptHeader: String = "YOUR SPEAKING PROMPT",
    val promptQuote: String = "\"Tell me about a project you're proud of.\"",
    val promptInstruction: String = "Use the STAR method. Aim for a clear, focused response in 60 seconds.",
    val statusText: String = "Listening to you",
    val timerText: String = "00:24",
    val isRecording: Boolean = true,
    val isPaused: Boolean = false,
    val feedbackHelpText: String = "Tap stop when you're ready for feedback",
    val privacyNoticeText: String = "A safe space to practice. Only you can access your recordings."
)

sealed interface SpeechStudioUiEvent {
    object StopAndGetFeedback : SpeechStudioUiEvent
    object RestartRecording : SpeechStudioUiEvent
    object TogglePauseRecording : SpeechStudioUiEvent
    object NavigateBack : SpeechStudioUiEvent
}
