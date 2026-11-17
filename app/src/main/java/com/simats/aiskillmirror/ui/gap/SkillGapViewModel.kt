package com.simats.aiskillmirror.ui.gap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SkillGapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SkillGapUiState(isLoading = true))
    val uiState: StateFlow<SkillGapUiState> = _uiState.asStateFlow()

    init {
        loadGapData()
    }

    fun loadGapData(subcategoryId: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = ApiClient.fetchSkillGapData(UserSession.userId, subcategoryId)
            if (result.isSuccess && result.data != null) {
                val data = result.data
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    report = data.currentReport,
                    availableSkills = data.availableSkills,
                    history = data.history,
                    selectedSkillId = data.currentReport.subcategoryId
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message ?: "Failed to load skill gap data"
                )
            }
        }
    }

    fun analyzeSkills() {
        val currentReport = _uiState.value.report ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true, errorMessage = null)
            val result = ApiClient.analyzeSkillGap(
                userId = UserSession.userId,
                mainSkill = currentReport.mainSkill,
                subcategoryId = currentReport.subcategoryId
            )
            if (result.isSuccess && result.data != null) {
                val refresh = ApiClient.fetchSkillGapData(UserSession.userId, currentReport.subcategoryId)
                val rep = result.data
                val hist = refresh.data?.history ?: _uiState.value.history
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    report = rep,
                    history = hist,
                    analysisSuccessToast = "Analysis complete! ${rep.overallCompetencyScore}% overall score."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    errorMessage = result.message ?: "Failed to analyze skill gaps"
                )
            }
        }
    }

    fun selectSkill(subcategoryId: String) {
        _uiState.value = _uiState.value.copy(selectedSkillId = subcategoryId, isSkillMenuExpanded = false)
        loadGapData(subcategoryId)
    }

    fun toggleSkillMenu(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(isSkillMenuExpanded = expanded)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(analysisSuccessToast = null)
    }
}

