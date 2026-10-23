package com.simats.aiskillmirror.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<RegisterUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onFullNameChange(name: String) {
        _uiState.value = _uiState.value.copy(fullName = name)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        val strength = if (password.length >= 10) "Strong password · ${password.length} characters"
        else if (password.length >= 6) "Medium password · ${password.length} characters"
        else "Weak password"
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordStrengthText = strength
        )
    }

    fun onTogglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun onTermsAcceptedChange(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(isTermsAccepted = accepted)
    }

    fun onCreateAccountClick() {
        viewModelScope.launch {
            _eventChannel.send(RegisterUiEvent.NavigateToHome)
        }
    }

    fun onGoogleSignInClick() {
        viewModelScope.launch {
            _eventChannel.send(RegisterUiEvent.ContinueWithGoogle)
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _eventChannel.send(RegisterUiEvent.NavigateToLogin)
        }
    }
}
