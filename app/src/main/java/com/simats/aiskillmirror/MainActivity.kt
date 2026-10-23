package com.simats.aiskillmirror

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.simats.aiskillmirror.ui.assess.SkillSelectionScreen
import com.simats.aiskillmirror.ui.assess.SkillSelectionViewModel
import com.simats.aiskillmirror.ui.assessment.AssessmentScreen
import com.simats.aiskillmirror.ui.assessment.AssessmentViewModel
import com.simats.aiskillmirror.ui.dashboard.DashboardScreen
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.dashboard.DashboardViewModel
import com.simats.aiskillmirror.ui.login.LoginScreen
import com.simats.aiskillmirror.ui.login.LoginViewModel
import com.simats.aiskillmirror.ui.onboarding.OnboardingScreen
import com.simats.aiskillmirror.ui.onboarding.OnboardingViewModel
import com.simats.aiskillmirror.ui.profile.ProfileScreen
import com.simats.aiskillmirror.ui.profile.ProfileViewModel
import com.simats.aiskillmirror.ui.recovery.AccountRecoveryScreen
import com.simats.aiskillmirror.ui.recovery.AccountRecoveryViewModel
import com.simats.aiskillmirror.ui.register.RegisterScreen
import com.simats.aiskillmirror.ui.register.RegisterViewModel
import com.simats.aiskillmirror.ui.results.SkillMirrorScreen
import com.simats.aiskillmirror.ui.results.SkillMirrorViewModel
import com.simats.aiskillmirror.ui.splash.SplashScreen
import com.simats.aiskillmirror.ui.splash.SplashViewModel
import com.simats.aiskillmirror.ui.theme.AISkillsMirrorTheme
import com.simats.aiskillmirror.ui.theme.DarkBackground

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    LOGIN,
    REGISTER,
    RECOVERY,
    DASHBOARD,
    PROFILE,
    SKILL_SELECTION,
    ASSESSMENT,
    SKILL_MIRROR
}

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val recoveryViewModel: AccountRecoveryViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val skillSelectionViewModel: SkillSelectionViewModel by viewModels()
    private val assessmentViewModel: AssessmentViewModel by viewModels()
    private val skillMirrorViewModel: SkillMirrorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AISkillsMirrorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

                    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                        when (screen) {
                            AppScreen.SPLASH -> {
                                SplashScreen(
                                    viewModel = splashViewModel,
                                    onNavigateToHome = {
                                        currentScreen = AppScreen.ONBOARDING
                                    },
                                    onNavigateToLogin = {
                                        currentScreen = AppScreen.LOGIN
                                    }
                                )
                            }
                            AppScreen.ONBOARDING -> {
                                OnboardingScreen(
                                    viewModel = onboardingViewModel,
                                    onCreateAccountClick = {
                                        currentScreen = AppScreen.REGISTER
                                    },
                                    onSkipClick = {
                                        currentScreen = AppScreen.LOGIN
                                    }
                                )
                            }
                            AppScreen.REGISTER -> {
                                RegisterScreen(
                                    viewModel = registerViewModel,
                                    onCreateAccountClick = {
                                        Toast.makeText(this@MainActivity, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                                        currentScreen = AppScreen.LOGIN
                                    },
                                    onGoogleSignInClick = {
                                        Toast.makeText(this@MainActivity, "Google Sign In Clicked", Toast.LENGTH_SHORT).show()
                                        currentScreen = AppScreen.DASHBOARD
                                    },
                                    onLoginClick = {
                                        currentScreen = AppScreen.LOGIN
                                    }
                                )
                            }
                            AppScreen.LOGIN -> {
                                LoginScreen(
                                    viewModel = loginViewModel,
                                    onLoginClick = {
                                        Toast.makeText(this@MainActivity, "Logged in Successfully!", Toast.LENGTH_SHORT).show()
                                        currentScreen = AppScreen.DASHBOARD
                                    },
                                    onForgotPasswordClick = {
                                        currentScreen = AppScreen.RECOVERY
                                    },
                                    onGoogleSignInClick = {
                                        Toast.makeText(this@MainActivity, "Google Sign In Clicked", Toast.LENGTH_SHORT).show()
                                        currentScreen = AppScreen.DASHBOARD
                                    },
                                    onSignUpClick = {
                                        currentScreen = AppScreen.REGISTER
                                    }
                                )
                            }
                            AppScreen.RECOVERY -> {
                                AccountRecoveryScreen(
                                    viewModel = recoveryViewModel,
                                    onSendResetLinkClick = {
                                        Toast.makeText(this@MainActivity, "Reset link sent!", Toast.LENGTH_SHORT).show()
                                        currentScreen = AppScreen.LOGIN
                                    },
                                    onBackToLoginClick = {
                                        currentScreen = AppScreen.LOGIN
                                    }
                                )
                            }
                            AppScreen.DASHBOARD -> {
                                DashboardScreen(
                                    viewModel = dashboardViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            else -> {}
                                        }
                                    },
                                    onViewSkillMirrorClick = {
                                        currentScreen = AppScreen.SKILL_MIRROR
                                    },
                                    onViewPlanClick = {
                                        Toast.makeText(this@MainActivity, "Opening Plan...", Toast.LENGTH_SHORT).show()
                                    },
                                    onContinueTrainingClick = {
                                        currentScreen = AppScreen.ASSESSMENT
                                    },
                                    onOpenAiCoachClick = {
                                        Toast.makeText(this@MainActivity, "Opening AI Coach...", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                            AppScreen.PROFILE -> {
                                ProfileScreen(
                                    viewModel = profileViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            else -> {}
                                        }
                                    },
                                    onEditPersonalInfoClick = {
                                        Toast.makeText(this@MainActivity, "Edit Personal Info Clicked", Toast.LENGTH_SHORT).show()
                                    },
                                    onNextMilestoneClick = {
                                        currentScreen = AppScreen.SKILL_SELECTION
                                    }
                                )
                            }
                            AppScreen.SKILL_SELECTION -> {
                                SkillSelectionScreen(
                                    viewModel = skillSelectionViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            else -> {}
                                        }
                                    },
                                    onStartAssessmentClick = {
                                        currentScreen = AppScreen.ASSESSMENT
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.DASHBOARD
                                    }
                                )
                            }
                            AppScreen.ASSESSMENT -> {
                                AssessmentScreen(
                                    viewModel = assessmentViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            else -> {}
                                        }
                                    },
                                    onNextQuestionClick = {
                                        Toast.makeText(this@MainActivity, "Next Question", Toast.LENGTH_SHORT).show()
                                    },
                                    onFinishAssessmentClick = {
                                        Toast.makeText(this@MainActivity, "Assessment Complete!", Toast.LENGTH_SHORT).show()
                                        currentScreen = AppScreen.SKILL_MIRROR
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.SKILL_SELECTION
                                    }
                                )
                            }
                            AppScreen.SKILL_MIRROR -> {
                                SkillMirrorScreen(
                                    viewModel = skillMirrorViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            else -> {}
                                        }
                                    },
                                    onViewLearningPlanClick = {
                                        currentScreen = AppScreen.DASHBOARD
                                    },
                                    onShareResultsClick = {
                                        Toast.makeText(this@MainActivity, "Sharing Skill Mirror Results...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.SKILL_SELECTION
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
