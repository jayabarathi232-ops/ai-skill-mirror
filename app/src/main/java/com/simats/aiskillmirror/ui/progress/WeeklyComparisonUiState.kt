package com.simats.aiskillmirror.ui.progress

import com.simats.aiskillmirror.data.network.WeeklyComparisonData

data class WeeklyComparisonUiState(
    val comparisonData: WeeklyComparisonData = WeeklyComparisonData(),
    val currentWeekOffset: Int = 0,
    val isLoading: Boolean = false,
    val isAiRetrying: Boolean = false,
    val errorMessage: String? = null
)

sealed interface WeeklyComparisonUiEvent {
    object NavigateBack : WeeklyComparisonUiEvent
    data class ShowToast(val message: String) : WeeklyComparisonUiEvent
}
