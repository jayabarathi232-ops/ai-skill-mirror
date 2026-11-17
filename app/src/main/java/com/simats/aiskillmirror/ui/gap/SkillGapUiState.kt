package com.simats.aiskillmirror.ui.gap

import com.simats.aiskillmirror.data.network.AvailableSkillItem
import com.simats.aiskillmirror.data.network.SkillGapHistoryItem
import com.simats.aiskillmirror.data.network.SkillGapReportData

data class SkillGapUiState(
    val isLoading: Boolean = false,
    val isAnalyzing: Boolean = false,
    val errorMessage: String? = null,
    val report: SkillGapReportData? = null,
    val availableSkills: List<AvailableSkillItem> = emptyList(),
    val history: List<SkillGapHistoryItem> = emptyList(),
    val selectedSkillId: String? = null,
    val isSkillMenuExpanded: Boolean = false,
    val analysisSuccessToast: String? = null
)

