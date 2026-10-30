package com.simats.aiskillmirror.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.UserSession
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
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onTogglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun onRememberMeChange(checked: Boolean) {
        _uiState.value = _uiState.value.copy(isRememberMeChecked = checked)
    }

    fun onLoginClick() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter both email and password")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = ApiClient.login(email, password)
            _uiState.value = _uiState.value.copy(isLoading = false)

            if (result.isSuccess && result.data != null) {
                UserSession.update(result.data.id, result.data.name, result.data.email)
                _eventChannel.send(LoginUiEvent.NavigateToHome)
            } else {
                val error = result.message ?: "Invalid email or password"
                _uiState.value = _uiState.value.copy(errorMessage = error)
                _eventChannel.send(LoginUiEvent.ShowError(error))
            }
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
