package com.simats.aiskillmirror.ui.milestones

data class AchievementItem(
    val id: String,
    val title: String,
    val earnedDateText: String,
    val iconType: AchievementIconType
)

enum class AchievementIconType {
    REFLECTION,
    WARRIOR,
    VOICE
}

data class MilestonesUiState(
    val headerTitle: String = "Milestones that matter",
    val headerCategory: String = "GOALS & ACHIEVEMENTS",
    val mainGoalTitle: String = "Reach a skill score of 85",
    val currentScore: Int = 78,
    val targetScore: Int = 85,
    val statusText: String = "On track",
    val targetDateText: String = "Target date: November 15 · 7 points to go",
    val streakTitle: String = "7 days of showing up",
    val streakSubtitle: String = "Next milestone: a 14-day streak",
    val streakProgressText: String = "7 / 14",
    val earnedCountText: String = "3 earned",
    val achievements: List<AchievementItem> = listOf(
        AchievementItem("1", "First reflection", "Sep 29", AchievementIconType.REFLECTION),
        AchievementItem("2", "Week warrior", "Oct 20", AchievementIconType.WARRIOR),
        AchievementItem("3", "Voice unlocked", "Oct 18", AchievementIconType.VOICE)
    ),
    val nextRewardTitle: String = "Rising expert",
    val nextRewardSubtitle: String = "Reach 85 to unlock your next badge",
    val nextRewardTarget: Int = 85,
    val totalXpText: String = "You've earned 1,240 XP. Keep your momentum."
)

sealed interface MilestonesUiEvent {
    object AddCustomGoal : MilestonesUiEvent
    object NavigateBack : MilestonesUiEvent
}
