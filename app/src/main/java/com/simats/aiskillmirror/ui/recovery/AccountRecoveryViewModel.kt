package com.simats.aiskillmirror.ui.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AccountRecoveryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AccountRecoveryUiState())
    val uiState: StateFlow<AccountRecoveryUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<AccountRecoveryUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onSendResetLinkClick() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLinkSent = true)
            _eventChannel.send(AccountRecoveryUiEvent.ResetLinkSent)
        }
    }

    fun onBackToLoginClick() {
        viewModelScope.launch {
            _eventChannel.send(AccountRecoveryUiEvent.NavigateBackToLogin)
        }
    }
}
