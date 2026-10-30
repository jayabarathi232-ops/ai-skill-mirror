package com.simats.aiskillmirror.ui.assess

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.repository.AssessmentRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SkillSelectionViewModel(
    private val assessmentRepository: AssessmentRepository = AssessmentRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillSelectionUiState())
    val uiState: StateFlow<SkillSelectionUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SkillSelectionUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadSkills()
    }

    fun loadSkills() {
        viewModelScope.launch {
            val result = assessmentRepository.getSkills(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val list = result.data.map { s ->
                    val icon = when (s.iconType.uppercase()) {
                        "COMMUNICATION" -> SkillIconType.COMMUNICATION
                        "PROBLEM_SOLVING" -> SkillIconType.PROBLEM_SOLVING
                        "INTERVIEW" -> SkillIconType.INTERVIEW
                        else -> SkillIconType.TECHNICAL
                    }
                    SkillCategory(
                        id = s.id,
                        name = s.name,
                        description = s.description,
                        lastScore = s.lastScore,
                        maxScore = s.maxScore,
                        iconType = icon
                    )
                }
                _uiState.value = _uiState.value.copy(
                    skills = list,
                    selectedSkillId = list.firstOrNull()?.id ?: _uiState.value.selectedSkillId
                )
            }
        }
    }

    fun onSelectSkill(skillId: String) {
        _uiState.value = _uiState.value.copy(selectedSkillId = skillId)
    }

    fun onStartAssessmentClick() {
        viewModelScope.launch {
            _eventChannel.send(SkillSelectionUiEvent.StartAssessment)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(SkillSelectionUiEvent.NavigateBack)
        }
    }
}
