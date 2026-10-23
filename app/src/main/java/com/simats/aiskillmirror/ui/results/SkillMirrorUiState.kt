package com.simats.aiskillmirror.ui.results

data class SkillScoreItem(
    val name: String,
    val score: Int,
    val maxScore: Int = 100,
    val isTopTier: Boolean = false
)

data class SkillMirrorUiState(
    val overallScore: Int = 78,
    val statusBadge: String = "ADVANCED",
    val headlineLine1: String = "Strong",
    val headlineLine2: String = "foundation.",
    val headlineLine3: String = "Real potential.",
    val pointsGainSubtitle: String = "+14 points since your first assessment",
    val skillCountText: String = "4 skills",
    val skillScores: List<SkillScoreItem> = listOf(
        SkillScoreItem("Technical skills", 86, isTopTier = true),
        SkillScoreItem("Problem solving", 80, isTopTier = true),
        SkillScoreItem("Interview readiness", 74, isTopTier = false),
        SkillScoreItem("Communication", 72, isTopTier = false)
    ),
    val nextMoveTitle: String = "Your next best move",
    val nextMoveDescription: String = "Your technical thinking is a strength. Practice concise, structured responses to bring your communication up to match.",
    val lastUpdatedText: String = "Updated Oct 20 · Based on your latest assessments"
)

sealed interface SkillMirrorUiEvent {
    object ViewLearningPlan : SkillMirrorUiEvent
    object ShareResults : SkillMirrorUiEvent
    object NavigateBack : SkillMirrorUiEvent
}
