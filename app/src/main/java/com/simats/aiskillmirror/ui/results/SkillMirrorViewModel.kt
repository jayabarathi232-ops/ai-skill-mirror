package com.simats.aiskillmirror.ui.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SkillMirrorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SkillMirrorUiState())
    val uiState: StateFlow<SkillMirrorUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SkillMirrorUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onViewLearningPlanClick() {
        viewModelScope.launch {
            _eventChannel.send(SkillMirrorUiEvent.ViewLearningPlan)
        }
    }

    fun onShareResultsClick() {
        viewModelScope.launch {
            _eventChannel.send(SkillMirrorUiEvent.ShareResults)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(SkillMirrorUiEvent.NavigateBack)
        }
    }
}
