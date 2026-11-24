package com.simats.aiskillmirror.ui.dashboard

import com.simats.aiskillmirror.data.network.PracticeHistoryItemDto

data class PerformanceHistoryUiState(
    val isLoading: Boolean = false,
    val selectedSkillFilter: String = "all", // "all", "coding", "photography", "music"
    val totalSessions: Int = 0,
    val totalMinutes: Int = 0,
    val averageScore: Int = 0,
    val countsBySkill: Map<String, Int> = emptyMap(),
    val allItems: List<PracticeHistoryItemDto> = emptyList(),
    val filteredItems: List<PracticeHistoryItemDto> = emptyList(),
    val errorMessage: String? = null
)
