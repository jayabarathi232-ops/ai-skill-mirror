package com.simats.aiskillmirror.ui.assess

data class SkillCategory(
    val id: String,
    val name: String,
    val description: String,
    val lastScore: Int,
    val maxScore: Int = 100,
    val iconType: SkillIconType
)

enum class SkillIconType {
    COMMUNICATION,
    TECHNICAL,
    PROBLEM_SOLVING,
    INTERVIEW
}

data class SkillSelectionUiState(
    val headerTitle: String = "Explore your skills",
    val headerCategory: String = "ASSESSMENT CENTER",
    val tagline: String = "GROW AT YOUR PACE",
    val headline: String = "What will you discover?",
    val description: String = "Choose a skill for a focused AI assessment.",
    val skills: List<SkillCategory> = listOf(
        SkillCategory(
            id = "comm",
            name = "Communication",
            description = "Clarity, expression & active listening",
            lastScore = 72,
            iconType = SkillIconType.COMMUNICATION
        ),
        SkillCategory(
            id = "tech",
            name = "Technical skills",
            description = "Coding, concepts & practical thinking",
            lastScore = 81,
            iconType = SkillIconType.TECHNICAL
        ),
        SkillCategory(
            id = "prob",
            name = "Problem solving",
            description = "Logic, reasoning & decision-making",
            lastScore = 80,
            iconType = SkillIconType.PROBLEM_SOLVING
        ),
        SkillCategory(
            id = "interview",
            name = "Interview readiness",
            description = "Confidence, structure & professional presence",
            lastScore = 77,
            iconType = SkillIconType.INTERVIEW
        )
    ),
    val selectedSkillId: String = "comm",
    val assessmentMetaText: String = "10 questions · About 8 minutes · AI evaluated"
)

sealed interface SkillSelectionUiEvent {
    object StartAssessment : SkillSelectionUiEvent
    object NavigateBack : SkillSelectionUiEvent
}
