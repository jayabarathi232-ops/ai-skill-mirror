package com.simats.aiskillmirror.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.PracticeHistoryItemDto
import com.simats.aiskillmirror.data.network.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerformanceHistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PerformanceHistoryUiState())
    val uiState: StateFlow<PerformanceHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = ApiClient.fetchPracticeHistory(userId = UserSession.userId, skill = "all")
            if (result.isSuccess && result.data != null) {
                val data = result.data
                val filtered = applyFilter(data.items, _uiState.value.selectedSkillFilter)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totalSessions = data.totalSessions,
                    totalMinutes = data.totalMinutes,
                    averageScore = data.averageScore,
                    countsBySkill = data.countsBySkill,
                    allItems = data.items,
                    filteredItems = filtered
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message ?: "Failed to load practice history"
                )
            }
        }
    }

    fun setFilter(skill: String) {
        val normalized = skill.lowercase()
        val filtered = applyFilter(_uiState.value.allItems, normalized)
        _uiState.value = _uiState.value.copy(
            selectedSkillFilter = normalized,
            filteredItems = filtered
        )
    }

    private fun applyFilter(items: List<PracticeHistoryItemDto>, filter: String): List<PracticeHistoryItemDto> {
        if (filter == "all" || filter.isBlank()) return items
        return items.filter { item ->
            val g = item.skillGroup.lowercase()
            val n = item.skillName.lowercase()
            when (filter) {
                "coding" -> g.contains("cod") || n.contains("cod")
                "photography", "photo" -> g.contains("photo") || n.contains("photo")
                "music" -> g.contains("music") || n.contains("music")
                else -> true
            }
        }
    }
}
