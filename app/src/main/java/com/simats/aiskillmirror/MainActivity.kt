package com.simats.aiskillmirror

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.notifications.SmartNotificationManager
import com.simats.aiskillmirror.ui.assess.SkillSelectionScreen
import com.simats.aiskillmirror.ui.assess.SkillSelectionViewModel
import com.simats.aiskillmirror.ui.assessment.AssessmentScreen
import com.simats.aiskillmirror.ui.assessment.AssessmentViewModel
import com.simats.aiskillmirror.ui.coach.AiCoachScreen
import com.simats.aiskillmirror.ui.coach.AiCoachViewModel
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
import com.simats.aiskillmirror.ui.profile.EditProfileScreen
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
import com.simats.aiskillmirror.ui.settings.SettingsScreen
import com.simats.aiskillmirror.ui.settings.SettingsViewModel
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
import com.simats.aiskillmirror.ui.upload.LiveCameraMode
import com.simats.aiskillmirror.ui.upload.MediaTypeTab
import com.simats.aiskillmirror.ui.upload.PerformanceUploadScreen
import com.simats.aiskillmirror.ui.upload.PerformanceUploadViewModel

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    LOGIN,
    REGISTER,
    RECOVERY,
    DASHBOARD,
    PROFILE,
    EDIT_PROFILE,
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
    NOTIFICATIONS,
    AI_COACH,
    SETTINGS,
    PERFORMANCE_UPLOAD
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
    private val aiCoachViewModel: AiCoachViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val performanceUploadViewModel: PerformanceUploadViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        notificationsViewModel.checkPermission(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserSession.init(this)
        SmartNotificationManager.initNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        enableEdgeToEdge()
        setContent {
            AISkillsMirrorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    val navigateToExtra = intent?.getStringExtra("navigateTo")
                    val initialScreen = when (navigateToExtra) {
                        "SPEECH_STUDIO" -> AppScreen.SPEECH_STUDIO
                        "NOTIFICATIONS" -> AppScreen.NOTIFICATIONS
                        else -> AppScreen.SPLASH
                    }
                    val backStack = remember { mutableStateListOf(initialScreen) }
                    val currentScreen = backStack.lastOrNull() ?: AppScreen.DASHBOARD

                    fun navigateTo(screen: AppScreen) {
                        if (backStack.lastOrNull() != screen) {
                            backStack.add(screen)
                        }
                    }

                    fun navigateBack() {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.size - 1)
                        }
                    }

                    fun replaceWith(screen: AppScreen) {
                        backStack.clear()
                        backStack.add(screen)
                    }

                    fun selectMainTab(screen: AppScreen) {
                        backStack.clear()
                        backStack.add(AppScreen.DASHBOARD)
                        if (screen != AppScreen.DASHBOARD) {
                            backStack.add(screen)
                        }
                    }

                    fun handleTabSelected(tab: DashboardTab) {
                        when (tab) {
                            DashboardTab.HOME -> selectMainTab(AppScreen.DASHBOARD)
                            DashboardTab.PROFILE -> selectMainTab(AppScreen.PROFILE)
                            DashboardTab.ASSESS -> selectMainTab(AppScreen.SKILL_SELECTION)
                            DashboardTab.LEARN -> selectMainTab(AppScreen.ROADMAP)
                            DashboardTab.PROGRESS -> selectMainTab(AppScreen.PROGRESS_ANALYTICS)
                        }
                    }

                    // System Back Button Handling
                    BackHandler(enabled = true) {
                        if (backStack.size > 1) {
                            navigateBack()
                        } else if (currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.LOGIN && currentScreen != AppScreen.SPLASH) {
                            selectMainTab(AppScreen.DASHBOARD)
                        } else {
                            finish()
                        }
                    }

                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(300),
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.SPLASH -> {
                                SplashScreen(
                                    viewModel = splashViewModel,
                                    onNavigateToHome = {
                                        replaceWith(AppScreen.DASHBOARD)
                                    },
                                    onNavigateToLogin = {
                                        navigateTo(AppScreen.LOGIN)
                                    }
                                )
                            }
                            AppScreen.ONBOARDING -> {
                                OnboardingScreen(
                                    viewModel = onboardingViewModel,
                                    onCreateAccountClick = {
                                        navigateTo(AppScreen.REGISTER)
                                    },
                                    onSkipClick = {
                                        navigateTo(AppScreen.LOGIN)
                                    }
                                )
                            }
                            AppScreen.REGISTER -> {
                                RegisterScreen(
                                    viewModel = registerViewModel,
                                    onCreateAccountClick = {
                                        Toast.makeText(this@MainActivity, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                                        navigateTo(AppScreen.LOGIN)
                                    },
                                    onGoogleSignInClick = {
                                        replaceWith(AppScreen.DASHBOARD)
                                    },
                                    onLoginClick = {
                                        navigateTo(AppScreen.LOGIN)
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.LOGIN -> {
                                LoginScreen(
                                    viewModel = loginViewModel,
                                    onLoginClick = {
                                        Toast.makeText(this@MainActivity, "Logged in Successfully!", Toast.LENGTH_SHORT).show()
                                        profileViewModel.loadProfile(this@MainActivity)
                                        dashboardViewModel.loadDashboard()
                                        replaceWith(AppScreen.DASHBOARD)
                                    },
                                    onForgotPasswordClick = {
                                        navigateTo(AppScreen.RECOVERY)
                                    },
                                    onGoogleSignInClick = {
                                        profileViewModel.loadProfile(this@MainActivity)
                                        dashboardViewModel.loadDashboard()
                                        replaceWith(AppScreen.DASHBOARD)
                                    },
                                    onSignUpClick = {
                                        navigateTo(AppScreen.REGISTER)
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.RECOVERY -> {
                                AccountRecoveryScreen(
                                    viewModel = recoveryViewModel,
                                    onSendResetLinkClick = {
                                        Toast.makeText(this@MainActivity, "Reset link sent!", Toast.LENGTH_SHORT).show()
                                        navigateBack()
                                    },
                                    onBackToLoginClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.DASHBOARD -> {
                                DashboardScreen(
                                    viewModel = dashboardViewModel,
                                    onNotificationsClick = {
                                        navigateTo(AppScreen.NOTIFICATIONS)
                                    },
                                    onTabSelected = ::handleTabSelected,
                                    onViewSkillMirrorClick = {
                                        navigateTo(AppScreen.SKILL_MIRROR)
                                    },
                                    onViewPlanClick = {
                                        navigateTo(AppScreen.ROADMAP)
                                    },
                                    onContinueTrainingClick = {
                                        navigateTo(AppScreen.DAILY_TRAINING)
                                    },
                                    onOpenAiCoachClick = {
                                        navigateTo(AppScreen.AI_COACH)
                                    },
                                    onUploadPerformanceClick = { type ->
                                        when (type.lowercase()) {
                                            "video" -> performanceUploadViewModel.selectTab(MediaTypeTab.VIDEO)
                                            "audio" -> performanceUploadViewModel.selectTab(MediaTypeTab.AUDIO)
                                            "live", "camera" -> {
                                                performanceUploadViewModel.selectTab(MediaTypeTab.LIVE_CAPTURE)
                                                performanceUploadViewModel.openLiveCamera(LiveCameraMode.PHOTO)
                                            }
                                            else -> performanceUploadViewModel.selectTab(MediaTypeTab.PHOTO)
                                        }
                                        navigateTo(AppScreen.PERFORMANCE_UPLOAD)
                                    },
                                    onOpenPublicSpeakingClick = {
                                        skillSelectionViewModel.selectCategory("speaking")
                                        navigateTo(AppScreen.SKILL_SELECTION)
                                    },
                                    onOpenSkillCategory = { catId ->
                                        skillSelectionViewModel.selectCategory(catId)
                                        navigateTo(AppScreen.SKILL_SELECTION)
                                    }
                                )
                            }
                            AppScreen.EDIT_PROFILE -> {
                                EditProfileScreen(
                                    viewModel = profileViewModel,
                                    onBackClick = {
                                        navigateBack()
                                    },
                                    onProfileSaved = {
                                        profileViewModel.loadProfile(this@MainActivity)
                                        dashboardViewModel.loadDashboard()
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.PROFILE -> {
                                ProfileScreen(
                                    viewModel = profileViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onEditPersonalInfoClick = {
                                        navigateTo(AppScreen.EDIT_PROFILE)
                                    },
                                    onNextMilestoneClick = {
                                        navigateTo(AppScreen.MILESTONES)
                                    },
                                    onSettingsClick = {
                                        navigateTo(AppScreen.SETTINGS)
                                    }
                                )
                            }
                            AppScreen.SKILL_SELECTION -> {
                                SkillSelectionScreen(
                                    viewModel = skillSelectionViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onStartTraining = { mainSkill, subcatId ->
                                        dailyTrainingViewModel.loadTraining(subcatId)
                                        navigateTo(AppScreen.DAILY_TRAINING)
                                    },
                                    onStartAssessmentClick = {
                                        navigateTo(AppScreen.ASSESSMENT)
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.ASSESSMENT -> {
                                AssessmentScreen(
                                    viewModel = assessmentViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onNextQuestionClick = {
                                        Toast.makeText(this@MainActivity, "Next Question", Toast.LENGTH_SHORT).show()
                                    },
                                    onFinishAssessmentClick = {
                                        Toast.makeText(this@MainActivity, "Assessment Complete!", Toast.LENGTH_SHORT).show()
                                        dashboardViewModel.loadDashboard()
                                        navigateTo(AppScreen.SKILL_MIRROR)
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.SKILL_MIRROR -> {
                                SkillMirrorScreen(
                                    viewModel = skillMirrorViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onViewLearningPlanClick = {
                                        navigateTo(AppScreen.ROADMAP)
                                    },
                                    onShareResultsClick = {
                                        Toast.makeText(this@MainActivity, "Sharing Skill Mirror Results...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.ROADMAP -> {
                                GrowthRoadmapScreen(
                                    viewModel = roadmapViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onContinueActiveWeekClick = {
                                        navigateTo(AppScreen.DAILY_TRAINING)
                                    },
                                    onFilterClick = {
                                        Toast.makeText(this@MainActivity, "Opening Filter Settings...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.DAILY_TRAINING -> {
                                DailyTrainingScreen(
                                    viewModel = dailyTrainingViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onStartPracticeClick = {
                                        Toast.makeText(this@MainActivity, "Daily practice session recorded! +35 XP", Toast.LENGTH_SHORT).show()
                                    },
                                    onSwitchSubcategoryClick = {
                                        navigateTo(AppScreen.SKILL_SELECTION)
                                    },
                                    onCalendarClick = {
                                        Toast.makeText(this@MainActivity, "Opening Calendar...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.SPEECH_STUDIO -> {
                                SpeechStudioScreen(
                                    viewModel = speechStudioViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onStopClick = {
                                        Toast.makeText(this@MainActivity, "Recording saved! Analysis ready.", Toast.LENGTH_SHORT).show()
                                        navigateTo(AppScreen.SPEECH_ANALYSIS)
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.SPEECH_ANALYSIS -> {
                                SpeechAnalysisScreen(
                                    viewModel = speechAnalysisViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onPracticeAgainClick = {
                                        navigateTo(AppScreen.SPEECH_STUDIO)
                                    },
                                    onPlayRecordingClick = {
                                        Toast.makeText(this@MainActivity, "Playing audio recording...", Toast.LENGTH_SHORT).show()
                                    },
                                    onShareClick = {
                                        Toast.makeText(this@MainActivity, "Sharing Speech Analysis...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.PROGRESS_ANALYTICS -> {
                                ProgressAnalyticsScreen(
                                    viewModel = progressAnalyticsViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onViewReportClick = {
                                        navigateTo(AppScreen.GROWTH_REPORT)
                                    },
                                    onCalendarClick = {
                                        Toast.makeText(this@MainActivity, "Opening Calendar...", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.GROWTH_REPORT -> {
                                GrowthReportScreen(
                                    viewModel = growthReportViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onExportPdfClick = {
                                        Toast.makeText(this@MainActivity, "Report exported to PDF successfully!", Toast.LENGTH_SHORT).show()
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.MILESTONES -> {
                                MilestonesScreen(
                                    viewModel = milestonesViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onAddGoalClick = {
                                        navigateTo(AppScreen.NOTIFICATIONS)
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.NOTIFICATIONS -> {
                                NotificationsScreen(
                                    viewModel = notificationsViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onRequestPermission = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    },
                                    onMarkAllAsReadClick = {
                                        notificationsViewModel.onMarkAllAsRead()
                                    },
                                    onSettingsClick = {
                                        notificationsViewModel.openSettingsDialog()
                                    },
                                    onStartPracticeClick = {
                                        navigateTo(AppScreen.SPEECH_STUDIO)
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.AI_COACH -> {
                                AiCoachScreen(
                                    viewModel = aiCoachViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onStartPracticeFromCoachClick = {
                                        navigateTo(AppScreen.SPEECH_STUDIO)
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.SETTINGS -> {
                                SettingsScreen(
                                    viewModel = settingsViewModel,
                                    onTabSelected = ::handleTabSelected,
                                    onPersonalInformationClick = {
                                        navigateTo(AppScreen.EDIT_PROFILE)
                                    },
                                    onLogoutClick = {
                                        UserSession.clear(this@MainActivity)
                                        Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
                                        replaceWith(AppScreen.LOGIN)
                                    },
                                    onBackClick = {
                                        navigateBack()
                                    }
                                )
                            }
                            AppScreen.PERFORMANCE_UPLOAD -> {
                                PerformanceUploadScreen(
                                    viewModel = performanceUploadViewModel,
                                    onBackClick = {
                                        navigateBack()
                                    },
                                    onTabSelected = ::handleTabSelected
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
