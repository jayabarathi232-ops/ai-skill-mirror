package com.simats.aiskillmirror.ui.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GrowthRoadmapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthRoadmapUiState())
    val uiState: StateFlow<GrowthRoadmapUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<GrowthRoadmapUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onContinueActiveWeekClick() {
        viewModelScope.launch {
            _eventChannel.send(GrowthRoadmapUiEvent.ContinueActiveWeek)
        }
    }

    fun onFilterClick() {
        viewModelScope.launch {
            _eventChannel.send(GrowthRoadmapUiEvent.OpenFilterSettings)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(GrowthRoadmapUiEvent.NavigateBack)
        }
    }
}
