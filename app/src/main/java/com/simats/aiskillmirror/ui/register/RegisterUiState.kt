package com.simats.aiskillmirror.ui.register

data class RegisterUiState(
    val fullName: String = "Maya Chen",
    val email: String = "maya.chen@university.edu",
    val password: String = "123456789012",
    val isPasswordVisible: Boolean = false,
    val isTermsAccepted: Boolean = true,
    val passwordStrengthText: String = "Strong password · 12 characters",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface RegisterUiEvent {
    object NavigateToLogin : RegisterUiEvent
    object NavigateToHome : RegisterUiEvent
    object ContinueWithGoogle : RegisterUiEvent
}
