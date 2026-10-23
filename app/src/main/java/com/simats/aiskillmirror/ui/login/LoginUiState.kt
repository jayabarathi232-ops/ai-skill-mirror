package com.simats.aiskillmirror.ui.login

data class LoginUiState(
    val email: String = "maya.chen@university.edu",
    val password: String = "1234567890",
    val isPasswordVisible: Boolean = false,
    val isRememberMeChecked: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LoginUiEvent {
    object NavigateToHome : LoginUiEvent
    object NavigateToSignUp : LoginUiEvent
    object NavigateToForgotPassword : LoginUiEvent
    object ContinueWithGoogle : LoginUiEvent
}
