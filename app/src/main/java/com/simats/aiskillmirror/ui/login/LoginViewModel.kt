package com.simats.aiskillmirror.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<LoginUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onTogglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun onRememberMeChange(checked: Boolean) {
        _uiState.value = _uiState.value.copy(isRememberMeChecked = checked)
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _eventChannel.send(LoginUiEvent.NavigateToHome)
        }
    }

    fun onForgotPasswordClick() {
        viewModelScope.launch {
            _eventChannel.send(LoginUiEvent.NavigateToForgotPassword)
        }
    }

    fun onGoogleSignInClick() {
        viewModelScope.launch {
            _eventChannel.send(LoginUiEvent.ContinueWithGoogle)
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _eventChannel.send(LoginUiEvent.NavigateToSignUp)
        }
    }
}
