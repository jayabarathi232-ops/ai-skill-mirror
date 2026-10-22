package com.simats.aiskillmirror.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SplashUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onGetStartedClick() {
        viewModelScope.launch {
            _eventChannel.send(SplashUiEvent.NavigateToHome)
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _eventChannel.send(SplashUiEvent.NavigateToLogin)
        }
    }
}
