package com.simats.aiskillmirror.ui.milestones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MilestonesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MilestonesUiState())
    val uiState: StateFlow<MilestonesUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<MilestonesUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onAddGoalClick() {
        viewModelScope.launch {
            _eventChannel.send(MilestonesUiEvent.AddCustomGoal)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(MilestonesUiEvent.NavigateBack)
        }
    }
}
