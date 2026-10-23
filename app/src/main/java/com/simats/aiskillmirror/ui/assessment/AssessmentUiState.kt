package com.simats.aiskillmirror.ui.assessment

data class AssessmentOption(
    val key: String,
    val text: String
)

data class AssessmentUiState(
    val categoryTitle: String = "Communication",
    val currentQuestionIndex: Int = 4,
    val totalQuestions: Int = 10,
    val progressPercentage: Int = 40,
    val topicBadgeText: String = "ACTIVE LISTENING",
    val questionHeadline: String = "A teammate disagrees with your approach and asks for your first response.",
    val questionScenario: String = "Your teammate says, \"I don't think this will scale. We should do it the way we did the last project.\" How do you respond?",
    val options: List<AssessmentOption> = listOf(
        AssessmentOption("A", "Interrupt them and explain why your way is better immediately."),
        AssessmentOption("B", "Ask them to explain their concerns and listen before responding."),
        AssessmentOption("C", "Tell them to trust your expertise and move forward without discussion."),
        AssessmentOption("D", "Dismiss their input and continue with your original plan.")
    ),
    val selectedOptionKey: String = "B",
    val isLastQuestion: Boolean = false
)

sealed interface AssessmentUiEvent {
    object NextQuestion : AssessmentUiEvent
    object FinishAssessment : AssessmentUiEvent
    object NavigateBack : AssessmentUiEvent
}
