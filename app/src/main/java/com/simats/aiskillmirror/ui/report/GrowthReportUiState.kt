package com.simats.aiskillmirror.ui.report

data class SkillComparisonItem(
    val name: String,
    val baselineScore: Int,
    val currentScore: Int,
    val pointsGained: Int,
    val maxScore: Int = 100
)

data class GrowthReportUiState(
    val headerTitle: String = "Your growth report",
    val headerDateRange: String = "SEP 29 – OCT 20, 2026",
    val snapshotBadgeText: String = "MONTHLY SNAPSHOT",
    val headline: String = "14 points stronger.",
    val snapshotSubtitle: String = "Maya, your consistency is translating into measurable skill growth.",
    val comparisonItems: List<SkillComparisonItem> = listOf(
        SkillComparisonItem("Technical skills", baselineScore = 74, currentScore = 86, pointsGained = 12),
        SkillComparisonItem("Problem solving", baselineScore = 66, currentScore = 80, pointsGained = 14),
        SkillComparisonItem("Interview readiness", baselineScore = 58, currentScore = 74, pointsGained = 16),
        SkillComparisonItem("Communication", baselineScore = 58, currentScore = 72, pointsGained = 14)
    ),
    val breakthroughTitle: String = "Your biggest breakthrough",
    val breakthroughDescription: String = "Interview readiness improved by 16 points. Keep practicing project stories to reach your target score of 85.",
    val exportButtonText: String = "Export report as PDF"
)

sealed interface GrowthReportUiEvent {
    object ExportPdf : GrowthReportUiEvent
    object NavigateBack : GrowthReportUiEvent
}
