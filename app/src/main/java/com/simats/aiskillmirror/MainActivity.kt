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
import com.simats.aiskillmirror.ui.milestones.MilestonesScreen
import com.simats.aiskillmirror.ui.milestones.MilestonesViewModel
import com.simats.aiskillmirror.ui.notifications.NotificationsScreen
import com.simats.aiskillmirror.ui.notifications.NotificationsViewModel
import com.simats.aiskillmirror.ui.onboarding.OnboardingScreen
import com.simats.aiskillmirror.ui.onboarding.OnboardingViewModel
import com.simats.aiskillmirror.ui.profile.ProfileScreen
import com.simats.aiskillmirror.ui.profile.ProfileViewModel
import com.simats.aiskillmirror.ui.progress.ProgressAnalyticsScreen
import com.simats.aiskillmirror.ui.progress.ProgressAnalyticsViewModel
import com.simats.aiskillmirror.ui.recovery.AccountRecoveryScreen
import com.simats.aiskillmirror.ui.recovery.AccountRecoveryViewModel
import com.simats.aiskillmirror.ui.register.RegisterScreen
import com.simats.aiskillmirror.ui.register.RegisterViewModel
import com.simats.aiskillmirror.ui.report.GrowthReportScreen
import com.simats.aiskillmirror.ui.report.GrowthReportViewModel
import com.simats.aiskillmirror.ui.results.SkillMirrorScreen
import com.simats.aiskillmirror.ui.results.SkillMirrorViewModel
import com.simats.aiskillmirror.ui.roadmap.GrowthRoadmapScreen
import com.simats.aiskillmirror.ui.roadmap.GrowthRoadmapViewModel
import com.simats.aiskillmirror.ui.speech.SpeechAnalysisScreen
import com.simats.aiskillmirror.ui.speech.SpeechAnalysisViewModel
import com.simats.aiskillmirror.ui.speech.SpeechStudioScreen
import com.simats.aiskillmirror.ui.speech.SpeechStudioViewModel
import com.simats.aiskillmirror.ui.splash.SplashScreen
import com.simats.aiskillmirror.ui.splash.SplashViewModel
import com.simats.aiskillmirror.ui.theme.AISkillsMirrorTheme
import com.simats.aiskillmirror.ui.theme.DarkBackground
import com.simats.aiskillmirror.ui.training.DailyTrainingScreen
import com.simats.aiskillmirror.ui.training.DailyTrainingViewModel

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
    SKILL_MIRROR,
    ROADMAP,
    DAILY_TRAINING,
    SPEECH_STUDIO,
    SPEECH_ANALYSIS,
    PROGRESS_ANALYTICS,
    GROWTH_REPORT,
    MILESTONES,
    NOTIFICATIONS
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
    private val roadmapViewModel: GrowthRoadmapViewModel by viewModels()
    private val dailyTrainingViewModel: DailyTrainingViewModel by viewModels()
    private val speechStudioViewModel: SpeechStudioViewModel by viewModels()
    private val speechAnalysisViewModel: SpeechAnalysisViewModel by viewModels()
    private val progressAnalyticsViewModel: ProgressAnalyticsViewModel by viewModels()
    private val growthReportViewModel: GrowthReportViewModel by viewModels()
    private val milestonesViewModel: MilestonesViewModel by viewModels()
    private val notificationsViewModel: NotificationsViewModel by viewModels()

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
                                    onNotificationsClick = {
                                        currentScreen = AppScreen.NOTIFICATIONS
                                    },
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            DashboardTab.LEARN -> currentScreen = AppScreen.ROADMAP
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
                                            else -> {}
                                        }
                                    },
                                    onViewSkillMirrorClick = {
                                        currentScreen = AppScreen.SKILL_MIRROR
                                    },
                                    onViewPlanClick = {
                                        currentScreen = AppScreen.ROADMAP
                                    },
                                    onContinueTrainingClick = {
                                        currentScreen = AppScreen.DAILY_TRAINING
                                    },
                                    onOpenAiCoachClick = {
                                        currentScreen = AppScreen.SPEECH_STUDIO
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
                                            DashboardTab.LEARN -> currentScreen = AppScreen.ROADMAP
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
                                            else -> {}
                                        }
                                    },
                                    onEditPersonalInfoClick = {
                                        Toast.makeText(this@MainActivity, "Edit Personal Info Clicked", Toast.LENGTH_SHORT).show()
                                    },
                                    onNextMilestoneClick = {
                                        currentScreen = AppScreen.MILESTONES
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
                                            DashboardTab.LEARN -> currentScreen = AppScreen.ROADMAP
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
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
                                            DashboardTab.LEARN -> currentScreen = AppScreen.ROADMAP
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
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
                                            DashboardTab.LEARN -> currentScreen = AppScreen.ROADMAP
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
                                            else -> {}
                                        }
                                    },
                                    onViewLearningPlanClick = {
                                        currentScreen = AppScreen.ROADMAP
                                    },
                                    onShareResultsClick = {
                                        Toast.makeText(this@MainActivity, "Sharing Skill Mirror Results...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.SKILL_SELECTION
                                    }
                                )
                            }
                            AppScreen.ROADMAP -> {
                                GrowthRoadmapScreen(
                                    viewModel = roadmapViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
                                            else -> {}
                                        }
                                    },
                                    onContinueActiveWeekClick = {
                                        currentScreen = AppScreen.DAILY_TRAINING
                                    },
                                    onFilterClick = {
                                        Toast.makeText(this@MainActivity, "Opening Filter Settings...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.DASHBOARD
                                    }
                                )
                            }
                            AppScreen.DAILY_TRAINING -> {
                                DailyTrainingScreen(
                                    viewModel = dailyTrainingViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
                                            else -> {}
                                        }
                                    },
                                    onStartPracticeClick = {
                                        currentScreen = AppScreen.SPEECH_STUDIO
                                    },
                                    onCalendarClick = {
                                        Toast.makeText(this@MainActivity, "Opening Calendar...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.ROADMAP
                                    }
                                )
                            }
                            AppScreen.SPEECH_STUDIO -> {
                                SpeechStudioScreen(
                                    viewModel = speechStudioViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
                                            else -> {}
                                        }
                                    },
                                    onStopClick = {
                                        Toast.makeText(this@MainActivity, "Recording saved! Analysis ready.", Toast.LENGTH_SHORT).show()
                                        currentScreen = AppScreen.SPEECH_ANALYSIS
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.DAILY_TRAINING
                                    }
                                )
                            }
                            AppScreen.SPEECH_ANALYSIS -> {
                                SpeechAnalysisScreen(
                                    viewModel = speechAnalysisViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
                                            else -> {}
                                        }
                                    },
                                    onPracticeAgainClick = {
                                        currentScreen = AppScreen.SPEECH_STUDIO
                                    },
                                    onPlayRecordingClick = {
                                        Toast.makeText(this@MainActivity, "Playing audio recording...", Toast.LENGTH_SHORT).show()
                                    },
                                    onShareClick = {
                                        Toast.makeText(this@MainActivity, "Sharing Speech Analysis...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.PROGRESS_ANALYTICS
                                    }
                                )
                            }
                            AppScreen.PROGRESS_ANALYTICS -> {
                                ProgressAnalyticsScreen(
                                    viewModel = progressAnalyticsViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            DashboardTab.LEARN -> currentScreen = AppScreen.ROADMAP
                                            else -> {}
                                        }
                                    },
                                    onViewReportClick = {
                                        currentScreen = AppScreen.GROWTH_REPORT
                                    },
                                    onCalendarClick = {
                                        Toast.makeText(this@MainActivity, "Opening Calendar...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.DASHBOARD
                                    }
                                )
                            }
                            AppScreen.GROWTH_REPORT -> {
                                GrowthReportScreen(
                                    viewModel = growthReportViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            DashboardTab.LEARN -> currentScreen = AppScreen.ROADMAP
                                            else -> {}
                                        }
                                    },
                                    onExportPdfClick = {
                                        Toast.makeText(this@MainActivity, "Exporting report as PDF...", Toast.LENGTH_SHORT).show()
                                        currentScreen = AppScreen.MILESTONES
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.PROGRESS_ANALYTICS
                                    }
                                )
                            }
                            AppScreen.MILESTONES -> {
                                MilestonesScreen(
                                    viewModel = milestonesViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            DashboardTab.LEARN -> currentScreen = AppScreen.ROADMAP
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
                                            else -> {}
                                        }
                                    },
                                    onAddGoalClick = {
                                        currentScreen = AppScreen.NOTIFICATIONS
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.GROWTH_REPORT
                                    }
                                )
                            }
                            AppScreen.NOTIFICATIONS -> {
                                NotificationsScreen(
                                    viewModel = notificationsViewModel,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            DashboardTab.HOME -> currentScreen = AppScreen.DASHBOARD
                                            DashboardTab.PROFILE -> currentScreen = AppScreen.PROFILE
                                            DashboardTab.ASSESS -> currentScreen = AppScreen.SKILL_SELECTION
                                            DashboardTab.LEARN -> currentScreen = AppScreen.ROADMAP
                                            DashboardTab.PROGRESS -> currentScreen = AppScreen.PROGRESS_ANALYTICS
                                            else -> {}
                                        }
                                    },
                                    onMarkAllAsReadClick = {
                                        Toast.makeText(this@MainActivity, "All notifications marked as read", Toast.LENGTH_SHORT).show()
                                    },
                                    onSettingsClick = {
                                        Toast.makeText(this@MainActivity, "Notification Settings", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        currentScreen = AppScreen.MILESTONES
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
