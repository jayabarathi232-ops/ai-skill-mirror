package com.simats.aiskillmirror.ui.roadmap

import com.simats.aiskillmirror.data.network.CompleteTopicResultData
import com.simats.aiskillmirror.data.network.RoadmapAiFeedbackData
import com.simats.aiskillmirror.data.network.RoadmapStageData
import com.simats.aiskillmirror.data.network.SubcategoryItemData

data class UpcomingStepItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val lessonCountText: String,
    val iconType: RoadmapIconType
)

enum class RoadmapIconType {
    LOGIC,
    INTERVIEW,
    SPEECH
}

data class GrowthRoadmapUiState(
    val headerTitle: String = "Your Growth Roadmap",
    val headerCategory: String = "PERSONALIZED BY AI",
    val activeSubcategory: SubcategoryItemData? = null,
    val availableSubcategories: List<SubcategoryItemData> = emptyList(),
    val stages: List<RoadmapStageData> = emptyList(),
    val activeStage: RoadmapStageData? = null,
    val completedStagesCount: Int = 0,
    val totalStagesCount: Int = 5,
    val roadmapProgressFraction: Float = 0.0f,
    val roadmapProgressPercentage: Int = 0,
    val roadmapProgressText: String = "0 of 5 Stages Completed · 0%",
    val totalPracticeMinutes: Int = 0,
    val aiFeedback: RoadmapAiFeedbackData? = null,
    val isLoading: Boolean = false,
    val statusMessage: String? = null,

    // Stage Assessment Quiz Modal State
    val isQuizDialogOpen: Boolean = false,
    val quizStage: RoadmapStageData? = null,
    val quizAnswers: Map<Int, Int> = emptyMap(),
    val quizResult: CompleteTopicResultData? = null,
    val isSubmittingQuiz: Boolean = false,
    val quizError: String? = null,

    // Legacy backward-compatibility fields
    val currentWeekBadgeText: String = "BEGINNER LEVEL",
    val planTitle: String = "Personalized Learning Plan",
    val planSubtitle: String = "Built for you · 0 min practiced",
    val planProgress: Float = 0.0f,
    val planProgressText: String = "0 of 5 lessons completed",
    val activeStepTitle: String = "Stage 1 Practice",
    val activeStepSubtitle: String = "Beginner · 15 mins",
    val activeStepProgressPercentage: String = "0%",
    val activeStepDescription: String = "Start your structured learning path.",
    val activeStepTags: List<String> = listOf("AI Guided", "Beginner Track"),
    val upcomingSteps: List<UpcomingStepItem> = emptyList(),
    val continueButtonText: String = "Start Learning"
)

sealed interface GrowthRoadmapUiEvent {
    object ContinueActiveWeek : GrowthRoadmapUiEvent
    data class StartPracticeStage(val stage: RoadmapStageData) : GrowthRoadmapUiEvent
    object OpenFilterSettings : GrowthRoadmapUiEvent
    object NavigateBack : GrowthRoadmapUiEvent
    data class ShowToast(val message: String) : GrowthRoadmapUiEvent
}
