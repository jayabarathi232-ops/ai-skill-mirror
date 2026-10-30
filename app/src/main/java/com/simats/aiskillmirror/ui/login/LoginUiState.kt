package com.simats.aiskillmirror.ui.login

data class LoginUiState(
    val email: String = "teststudent@example.com",
    val password: String = "12345678",
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
    data class ShowError(val message: String) : LoginUiEvent
}
