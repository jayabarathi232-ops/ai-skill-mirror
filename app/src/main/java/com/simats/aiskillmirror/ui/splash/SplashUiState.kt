package com.simats.aiskillmirror.ui.splash

data class SplashUiState(
    val appTitle: String = "AI SKILL MIRROR",
    val tagline: String = "Discover Your Skills. Improve Your Future.",
    val headlineLine1: String = "Discover Your Skills.",
    val headlineLine2: String = "Improve Your Future.",
    val description: String = "Meet your intelligent guide to skill assessment, personalized learning, and real progress.",
    val getStartedButtonText: String = "Get Started",
    val loginPromptText: String = "Already have an account? ",
    val loginLinkText: String = "Log in",
    val isLoading: Boolean = true
)

sealed interface SplashUiEvent {
    object NavigateToHome : SplashUiEvent
    object NavigateToLogin : SplashUiEvent
}
