package com.simats.aiskillmirror.ui.onboarding

data class OnboardingFeature(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconType: OnboardingIconType
)

enum class OnboardingIconType {
    DISCOVER,
    LEARN,
    PROGRESS
}

data class OnboardingUiState(
    val headlineLine1: String = "A clearer path to",
    val headlineLine2: String = "a more capable you.",
    val description: String = "One intelligent space to discover, practice, and grow the skills that matter.",
    val features: List<OnboardingFeature> = listOf(
        OnboardingFeature(
            id = "1",
            title = "Discover your strengths",
            subtitle = "Adaptive assessments, meaningful insights",
            iconType = OnboardingIconType.DISCOVER
        ),
        OnboardingFeature(
            id = "2",
            title = "Learn your way",
            subtitle = "An AI plan built around your goals",
            iconType = OnboardingIconType.LEARN
        ),
        OnboardingFeature(
            id = "3",
            title = "Watch your progress",
            subtitle = "Small steps. Measurable growth.",
            iconType = OnboardingIconType.PROGRESS
        )
    ),
    val currentStep: Int = 0,
    val totalSteps: Int = 3,
    val buttonText: String = "Create my account"
)

sealed interface OnboardingUiEvent {
    object NavigateToCreateAccount : OnboardingUiEvent
    object SkipOnboarding : OnboardingUiEvent
}
