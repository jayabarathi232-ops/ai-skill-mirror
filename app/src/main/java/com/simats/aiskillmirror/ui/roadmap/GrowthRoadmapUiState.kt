package com.simats.aiskillmirror.ui.roadmap

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
    val headerTitle: String = "Your growth roadmap",
    val headerCategory: String = "PERSONALIZED BY AI",
    val currentWeekBadgeText: String = "WEEK 2 OF 4",
    val planTitle: String = "Your career-ready plan.",
    val planSubtitle: String = "Built for Maya · 20 min/day · Target score 85",
    val planProgress: Float = 0.3f,
    val planProgressText: String = "Week 1 complete · 6 of 20 lessons done",
    val activeStepTitle: String = "Communicate with impact",
    val activeStepSubtitle: String = "Week 2 · 3 of 5 lessons complete",
    val activeStepProgressPercentage: String = "60%",
    val activeStepDescription: String = "Build clarity with structured answers.",
    val activeStepTags: List<String> = listOf("AI recommended", "Communication 72"),
    val upcomingSteps: List<UpcomingStepItem> = listOf(
        UpcomingStepItem(
            id = "w3",
            title = "Think through challenges",
            subtitle = "Week 3 · Logic & problem solving",
            lessonCountText = "5 lessons",
            iconType = RoadmapIconType.LOGIC
        ),
        UpcomingStepItem(
            id = "w4",
            title = "Own your interview",
            subtitle = "Week 4 · Mock interviews & review",
            lessonCountText = "5 lessons",
            iconType = RoadmapIconType.INTERVIEW
        )
    ),
    val continueButtonText: String = "Continue week 2"
)

sealed interface GrowthRoadmapUiEvent {
    object ContinueActiveWeek : GrowthRoadmapUiEvent
    object OpenFilterSettings : GrowthRoadmapUiEvent
    object NavigateBack : GrowthRoadmapUiEvent
}
