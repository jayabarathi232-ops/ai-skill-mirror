package com.simats.aiskillmirror.ui.coach

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "9:41 AM"
)

data class AiCoachUiState(
    val coachName: String = "Mira, your AI coach",
    val statusText: String = "ONLINE · PERSONALISED AI COACH",
    val bannerNoticeText: String = "Guided by your skills, goals, and learning plan",
    val timestampHeader: String = "Today",
    val messages: List<ChatMessage> = emptyList(),
    val suggestionChips: List<String> = listOf(
        "How can I improve my skills?",
        "Give me a daily practice task",
        "Explain Python basics",
        "How can I improve my photography?",
        "Tamil Coaching"
    ),
    val currentInputText: String = "",
    val isTyping: Boolean = false,
    val errorMessage: String? = null,
    val lastFailedMessage: String? = null,
    // Voice Chat State
    val isVoiceModeActive: Boolean = false,
    val voiceState: VoiceCoachState = VoiceCoachState.IDLE,
    val voiceLanguage: VoiceLanguage = VoiceLanguage.ENGLISH,
    val userSpokenText: String = "",
    val isEditingSpokenText: Boolean = false,
    val latestAiVoiceResponse: String = "Hi, I'm Mira! Ask me anything about your coding, photography, drawing, music, or public speaking progress.",
    val audioRmsLevel: Float = 0f,
    val isContinuousMode: Boolean = true,
    val isAudioMuted: Boolean = false,
    val voiceErrorMessage: String? = null,
    val isSendingVoiceMessage: Boolean = false
)

sealed interface AiCoachUiEvent {
    object SendMessage : AiCoachUiEvent
    object StartPracticeFromCoach : AiCoachUiEvent
    object NavigateBack : AiCoachUiEvent
    data class ShowToast(val message: String) : AiCoachUiEvent
}
