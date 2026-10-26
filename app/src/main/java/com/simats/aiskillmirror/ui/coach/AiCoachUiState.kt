package com.simats.aiskillmirror.ui.coach

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "9:41 AM"
)

data class AiCoachUiState(
    val coachName: String = "Mira, your AI coach",
    val statusText: String = "ONLINE · PERSONALISED FOR MAYA",
    val bannerNoticeText: String = "Guided by your skills, goals, and learning plan",
    val timestampHeader: String = "Today · 9:41 AM",
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "1",
            text = "Morning, Maya! Your technical score is 86. Let's help your communication catch up. What would you like to work on?",
            isUser = false
        ),
        ChatMessage(
            id = "2",
            text = "How can I explain my project better in an interview?",
            isUser = true
        ),
        ChatMessage(
            id = "3",
            text = "Try the STAR method. It keeps your answer focused and easy to follow:\n\nS - Set the scene\nT - Explain your responsibility\nA - Describe what you did\nR - Share a measurable result\n\nFor your study app, lead with the problem it solved. Want to practice a 60-second answer?",
            isUser = false
        )
    ),
    val suggestionChips: List<String> = listOf("Let's practice", "Show me an example"),
    val currentInputText: String = ""
)

sealed interface AiCoachUiEvent {
    object SendMessage : AiCoachUiEvent
    object StartPracticeFromCoach : AiCoachUiEvent
    object NavigateBack : AiCoachUiEvent
}
