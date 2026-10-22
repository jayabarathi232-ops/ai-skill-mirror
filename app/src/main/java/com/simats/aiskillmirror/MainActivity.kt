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
import com.simats.aiskillmirror.ui.onboarding.OnboardingScreen
import com.simats.aiskillmirror.ui.onboarding.OnboardingViewModel
import com.simats.aiskillmirror.ui.splash.SplashScreen
import com.simats.aiskillmirror.ui.splash.SplashViewModel
import com.simats.aiskillmirror.ui.theme.AISkillsMirrorTheme
import com.simats.aiskillmirror.ui.theme.DarkBackground

enum class AppScreen {
    SPLASH,
    ONBOARDING
}

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val onboardingViewModel: OnboardingViewModel by viewModels()

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
                                        Toast.makeText(this@MainActivity, "Navigating to Login", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                            AppScreen.ONBOARDING -> {
                                OnboardingScreen(
                                    viewModel = onboardingViewModel,
                                    onCreateAccountClick = {
                                        Toast.makeText(this@MainActivity, "Create Account Clicked", Toast.LENGTH_SHORT).show()
                                    },
                                    onSkipClick = {
                                        Toast.makeText(this@MainActivity, "Skipped Onboarding", Toast.LENGTH_SHORT).show()
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
