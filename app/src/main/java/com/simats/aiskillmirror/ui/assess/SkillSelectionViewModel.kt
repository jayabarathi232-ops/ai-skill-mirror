package com.simats.aiskillmirror.ui.assess

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SkillSelectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SkillSelectionUiState())
    val uiState: StateFlow<SkillSelectionUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SkillSelectionUiEvent>()
    val events = _eventChannel.receiveAsFlow()

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
