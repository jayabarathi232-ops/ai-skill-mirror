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
import com.simats.aiskillmirror.ui.splash.SplashScreen
import com.simats.aiskillmirror.ui.splash.SplashViewModel
import com.simats.aiskillmirror.ui.theme.AISkillsMirrorTheme
import com.simats.aiskillmirror.ui.theme.DarkBackground

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    REGISTER,
    LOGIN,
    RECOVERY,
    DASHBOARD,
    PROFILE
}

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val recoveryViewModel: AccountRecoveryViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

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
                                        if (tab == DashboardTab.PROFILE) {
                                            currentScreen = AppScreen.PROFILE
                                        }
                                    },
                                    onViewSkillMirrorClick = {
                                        currentScreen = AppScreen.PROFILE
                                    },
                                    onViewPlanClick = {
                                        Toast.makeText(this@MainActivity, "Opening Plan...", Toast.LENGTH_SHORT).show()
                                    },
                                    onContinueTrainingClick = {
                                        Toast.makeText(this@MainActivity, "Continuing Training...", Toast.LENGTH_SHORT).show()
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
                                        if (tab == DashboardTab.HOME) {
                                            currentScreen = AppScreen.DASHBOARD
                                        }
                                    },
                                    onEditPersonalInfoClick = {
                                        Toast.makeText(this@MainActivity, "Edit Personal Info Clicked", Toast.LENGTH_SHORT).show()
                                    },
                                    onNextMilestoneClick = {
                                        Toast.makeText(this@MainActivity, "Opening Next Milestone...", Toast.LENGTH_SHORT).show()
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
