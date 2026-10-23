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
import com.simats.aiskillmirror.ui.login.LoginScreen
import com.simats.aiskillmirror.ui.login.LoginViewModel
import com.simats.aiskillmirror.ui.onboarding.OnboardingScreen
import com.simats.aiskillmirror.ui.onboarding.OnboardingViewModel
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
    LOGIN
}

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

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
                                        currentScreen = AppScreen.REGISTER
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
                                    },
                                    onForgotPasswordClick = {
                                        Toast.makeText(this@MainActivity, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
                                    },
                                    onGoogleSignInClick = {
                                        Toast.makeText(this@MainActivity, "Google Sign In Clicked", Toast.LENGTH_SHORT).show()
                                    },
                                    onSignUpClick = {
                                        currentScreen = AppScreen.REGISTER
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
