package com.simats.aiskillmirror

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.simats.aiskillmirror.ui.splash.SplashScreen
import com.simats.aiskillmirror.ui.splash.SplashViewModel
import com.simats.aiskillmirror.ui.theme.AISkillsMirrorTheme
import com.simats.aiskillmirror.ui.theme.DarkBackground

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AISkillsMirrorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    SplashScreen(
                        viewModel = splashViewModel,
                        onNavigateToHome = {
                            Toast.makeText(this, "Navigating to Home", Toast.LENGTH_SHORT).show()
                        },
                        onNavigateToLogin = {
                            Toast.makeText(this, "Navigating to Login", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
