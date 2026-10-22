package com.simats.aiskillmirror.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<OnboardingUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onCreateAccountClick() {
        viewModelScope.launch {
            _eventChannel.send(OnboardingUiEvent.NavigateToCreateAccount)
        }
    }

    fun onSkipClick() {
        viewModelScope.launch {
            _eventChannel.send(OnboardingUiEvent.SkipOnboarding)
        }
    }
}
