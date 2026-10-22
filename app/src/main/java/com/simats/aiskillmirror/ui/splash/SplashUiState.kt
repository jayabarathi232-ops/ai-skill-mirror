package com.simats.aiskillmirror.ui.splash

data class SplashUiState(
    val appTitle: String = "AI Skills Mirror",
    val tagline: String = "YOUR POTENTIAL, REFLECTED",
    val headlineLine1: String = "See your skills.",
    val headlineLine2: String = "Shape your future.",
    val description: String = "Meet your intelligent guide to skill assessment, personalized learning, and real progress.",
    val getStartedButtonText: String = "Get Started",
    val loginPromptText: String = "Already have an account? ",
    val loginLinkText: String = "Log in",
    val isLoading: Boolean = false
)

sealed interface SplashUiEvent {
    object NavigateToHome : SplashUiEvent
    object NavigateToLogin : SplashUiEvent
}
