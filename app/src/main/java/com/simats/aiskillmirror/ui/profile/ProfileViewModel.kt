package com.simats.aiskillmirror.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<ProfileUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onEditPersonalInfoClick() {
        viewModelScope.launch {
            _eventChannel.send(ProfileUiEvent.EditPersonalInfo)
        }
    }

    fun onNextMilestoneClick() {
        viewModelScope.launch {
            _eventChannel.send(ProfileUiEvent.OpenNextMilestone)
        }
    }
}
