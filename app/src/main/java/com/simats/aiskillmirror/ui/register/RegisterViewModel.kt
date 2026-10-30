package com.simats.aiskillmirror.ui.register

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

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<RegisterUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onFullNameChange(name: String) {
        _uiState.value = _uiState.value.copy(fullName = name, errorMessage = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        val strength = if (password.length >= 10) "Strong password · ${password.length} characters"
        else if (password.length >= 6) "Medium password · ${password.length} characters"
        else "Weak password"
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordStrengthText = strength,
            errorMessage = null
        )
    }

    fun onTogglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun onTermsAcceptedChange(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(isTermsAccepted = accepted)
    }

    fun onCreateAccountClick() {
        val name = _uiState.value.fullName.trim()
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (name.isBlank() || email.isBlank() || password.length < 8) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter name, valid email, and at least 8 characters password")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = ApiClient.signup(name, email, password)
            _uiState.value = _uiState.value.copy(isLoading = false)

            if (result.isSuccess && result.data != null) {
                UserSession.update(result.data.id, result.data.name, result.data.email)
                _eventChannel.send(RegisterUiEvent.NavigateToHome)
            } else {
                val err = result.message ?: "Registration failed"
                _uiState.value = _uiState.value.copy(errorMessage = err)
            }
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
