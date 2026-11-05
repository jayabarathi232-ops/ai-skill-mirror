package com.simats.aiskillmirror.ui.speech

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.simats.aiskillmirror.ui.coach.VoiceCoachManager
import com.simats.aiskillmirror.ui.coach.VoiceCoachState
import com.simats.aiskillmirror.ui.coach.VoiceLanguage
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.dashboard.DottedSkillScoreArc
import com.simats.aiskillmirror.ui.dashboard.MicIcon
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*
import kotlin.math.sin

/**
 * Public Speaking Training & Dashboard Hub
 *
 * Theme:
 * - Background: Soft lavender (#F5F0FF)
 * - Cards: Pure white (#FFFFFF)
 * - Primary: Deep purple (#7545C8)
 * - Accent: Violet gradient (#7545C8 -> #9353E2)
 * - Text: Dark charcoal (#25213A)
 */
@Composable
fun SpeechStudioScreen(
    viewModel: SpeechStudioViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onStopClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Voice Coach Manager for Live Speech-to-Text & RMS Waveform
    val voiceManager = remember {
        VoiceCoachManager(
            context = context,
            onStateChanged = { /* state handled in UI */ },
            onPartialText = { text -> viewModel.updateLiveTranscript(text) },
            onFinalText = { text -> viewModel.updateLiveTranscript(text) },
            onRmsChanged = { rms -> viewModel.updateAudioRms(rms) },
            onError = { /* handled gracefully */ },
            onTtsFinished = { /* not needed in practice studio */ }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceManager.destroy()
        }
    }

    // Permission Launcher
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startRecording()
            voiceManager.startListening()
        } else {
            Toast.makeText(context, "Microphone permission required for speech practice", Toast.LENGTH_SHORT).show()
        }
    }

    fun startLivePractice() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.startRecording()
            voiceManager.startListening()
        } else {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun stopAndFinishPractice() {
        voiceManager.stopListening()
        viewModel.stopAndAnalyze()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SpeechStudioUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is SpeechStudioUiEvent.NavigateBack -> {
                    onBackClick()
                }
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .shadow(2.dp, RoundedCornerShape(10.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBackground)
                            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.onBackClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        BackArrowIcon(size = 18.dp, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (uiState.currentTab == SpeechTab.DASHBOARD) "Public Speaking" else uiState.currentModule.title,
                            color = HeadingPurple,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AI SPEECH & VOICE STUDIO",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Tab Switcher Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(TaglineBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(100.dp))
                        .clickable {
                            val nextTab = if (uiState.currentTab == SpeechTab.DASHBOARD) SpeechTab.STUDIO else SpeechTab.DASHBOARD
                            viewModel.switchTab(nextTab)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (uiState.currentTab == SpeechTab.DASHBOARD) "🎙️ Practice Studio" else "📊 Dashboard",
                        color = TaglineText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                when (uiState.currentTab) {
                    SpeechTab.DASHBOARD -> {
                        PublicSpeakingDashboardView(
                            uiState = uiState,
                            onStartRecommended = {
                                viewModel.selectModule(PublicSpeakingModule.INTERVIEW_PRACTICE)
                            },
                            onSelectModule = { module ->
                                viewModel.selectModule(module)
                            }
                        )
                    }
                    SpeechTab.STUDIO -> {
                        PublicSpeakingStudioView(
                            uiState = uiState,
                            onStartRecording = { startLivePractice() },
                            onStopRecording = { stopAndFinishPractice() },
                            onTogglePause = {
                                viewModel.togglePauseRecording()
                                if (uiState.isPaused) voiceManager.resumeConversation() else voiceManager.pauseConversation()
                            },
                            onGenerateTopic = { viewModel.generateNewAiTopic() }
                        )
                    }
                    SpeechTab.ANALYSIS -> {
                        PublicSpeakingAnalysisView(
                            uiState = uiState,
                            onPracticeAgain = {
                                viewModel.switchTab(SpeechTab.STUDIO)
                            },
                            onReturnDashboard = {
                                viewModel.switchTab(SpeechTab.DASHBOARD)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Fixed Bottom Navigation
            BottomNavigationBar(
                selectedTab = DashboardTab.LEARN,
                onTabSelected = onTabSelected
            )
        }
    }
}

/**
 * 1. PUBLIC SPEAKING DASHBOARD VIEW
 */
@Composable
fun PublicSpeakingDashboardView(
    uiState: SpeechStudioUiState,
    onStartRecommended: () -> Unit,
    onSelectModule: (PublicSpeakingModule) -> Unit
) {
    // 1. Stats Overview Card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(22.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "PUBLIC SPEAKING METRICS",
                        color = TaglineText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFFDCFCE7))
                        .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(100.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "▲ +8% this week",
                        color = GreenNode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score Gauge
                Box(
                    modifier = Modifier.size(112.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DottedSkillScoreArc(score = uiState.speakingProgress)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${uiState.speakingProgress}",
                            color = TextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "SCORE",
                            color = TextSecondary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Stats List
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatMetricRow(label = "Total Sessions", value = "${uiState.totalPracticeSessions} Completed")
                    StatMetricRow(label = "Speaking Speed", value = uiState.speakingSpeed)
                    StatMetricRow(label = "Speech Clarity", value = uiState.speechClarityFeedback)
                    StatMetricRow(label = "Pronunciation", value = uiState.pronunciationFeedback)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 2. Recommended Next Exercise Banner
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = PrimaryGradientStart)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                )
            )
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.22f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "RECOMMENDED NEXT EXERCISE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                }
                Text(
                    text = uiState.recommendedNextDuration,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = uiState.recommendedNextExercise,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Target zero filler words using deliberate 2-second micro-pauses.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStartRecommended,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Start Recommended Drill →",
                    color = AccentPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(22.dp))

    // 3. The 8 Training Modules Section Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "8 Public Speaking Modules",
                color = AccentPurple,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Interactive AI-powered voice & speech training",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Grid of Modules (2 Columns)
    val modules = PublicSpeakingModule.values()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (i in modules.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModuleCard(
                    modifier = Modifier.weight(1f),
                    module = modules[i],
                    onClick = { onSelectModule(modules[i]) }
                )
                if (i + 1 < modules.size) {
                    ModuleCard(
                        modifier = Modifier.weight(1f),
                        module = modules[i + 1],
                        onClick = { onSelectModule(modules[i + 1]) }
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 4. Previous Practice History Section
    Text(
        text = "Previous Practice History",
        color = AccentPurple,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Track your session cadence, WPM and feedback over time",
        color = TextSecondary,
        fontSize = 12.sp
    )

    Spacer(modifier = Modifier.height(12.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        uiState.historyList.forEach { session ->
            PracticeHistoryCard(session = session)
        }
    }
}

/**
 * 2. PUBLIC SPEAKING ACTIVE STUDIO VIEW
 */
@Composable
fun PublicSpeakingStudioView(
    uiState: SpeechStudioUiState,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onTogglePause: () -> Unit,
    onGenerateTopic: () -> Unit
) {
    // Active Module Card & Prompt
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(22.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = uiState.currentModule.iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.currentModule.title,
                        color = HeadingPurple,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = uiState.currentModule.badge,
                        color = TaglineText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "YOUR SPEAKING PROMPT",
                color = TaglineText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"${uiState.activePromptTitle}\"",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.activePromptInstruction,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // AI Topic Generator Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(TaglineBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                    .clickable { onGenerateTopic() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SparkleIcon(size = 14.dp, color = AccentPurple)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.isGeneratingTopic) "Generating Topic..." else "✨ Generate New AI Topic",
                        color = AccentPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // LIVE MICROPHONE RECORDING ORB & TIMER
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isRecording && !uiState.isPaused) Color(0xFF16A34A) else if (uiState.isPaused) Color(0xFFF59E0B) else Color(0xFF94A3B8))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when {
                    uiState.isAnalyzing -> "Analyzing speech acoustic metrics..."
                    uiState.isRecording && !uiState.isPaused -> "Listening to your voice live..."
                    uiState.isPaused -> "Recording Paused"
                    else -> "Tap microphone below to start practice"
                },
                color = if (uiState.isRecording && !uiState.isPaused) Color(0xFF16A34A) else TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Timer
        Text(
            text = "${uiState.timerText} / ${String.format("%02d:00", uiState.targetDurationSeconds / 60)}",
            color = TextPrimary,
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Waveform Visualizer
        StudioWaveformBars(
            isRecording = uiState.isRecording && !uiState.isPaused,
            rmsLevel = uiState.audioRmsLevel
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Live Speech-to-Text Transcription Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE SPEECH TRANSCRIPT",
                        color = TaglineText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${uiState.liveTranscript.split("\\s+".toRegex()).filter { it.isNotBlank() }.size} words",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.liveTranscript.ifBlank { "Speak naturally into your device microphone. Your spoken words will appear here in real-time." },
                    color = if (uiState.liveTranscript.isBlank()) TextSecondary else TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontWeight = if (uiState.liveTranscript.isBlank()) FontWeight.Normal else FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // RECORDING CONTROLS ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause / Resume Button
            if (uiState.isRecording) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(2.dp, CircleShape, spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(CircleShape)
                        .background(CardBackground)
                        .border(1.dp, CardBorder, CircleShape)
                        .clickable { onTogglePause() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (uiState.isPaused) "▶️" else "⏸️", fontSize = 18.sp)
                }
            }

            // Primary Mic / Stop Button
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .shadow(12.dp, CircleShape, spotColor = PrimaryGradientStart)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (uiState.isRecording) {
                                listOf(Color(0xFFDC2626), Color(0xFFEF4444))
                            } else {
                                listOf(PrimaryGradientStart, PrimaryGradientEnd)
                            }
                        )
                    )
                    .clickable {
                        if (uiState.isRecording) {
                            onStopRecording()
                        } else {
                            onStartRecording()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isRecording) {
                    // Stop Square
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White)
                    )
                } else {
                    MicIcon(size = 32.dp, color = Color.White)
                }
            }

            if (uiState.isRecording) {
                // Done / Finish Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(2.dp, CircleShape, spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7))
                        .border(1.dp, Color(0xFF86EFAC), CircleShape)
                        .clickable { onStopRecording() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✓", color = GreenNode, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (uiState.isRecording) "Tap red button to stop and view AI analysis" else "Tap microphone to begin speaking session",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 3. PUBLIC SPEAKING PERFORMANCE ANALYSIS VIEW
 */
@Composable
fun PublicSpeakingAnalysisView(
    uiState: SpeechStudioUiState,
    onPracticeAgain: () -> Unit,
    onReturnDashboard: () -> Unit
) {
    val analysis = uiState.latestAnalysis

    // Top Summary Card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(22.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "AI SPEECH ANALYSIS",
                        color = TaglineText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFFDCFCE7))
                        .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(100.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = analysis.comparisonWithPrevious,
                        color = GreenNode,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DottedSkillScoreArc(score = analysis.overallScore)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${analysis.overallScore}",
                            color = TextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "OVERALL",
                            color = TextSecondary,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatMetricRow(label = "Speaking Speed", value = "${analysis.wpm} WPM · ${analysis.paceRating}")
                    StatMetricRow(label = "Speech Clarity", value = "${analysis.clarityScore}%")
                    StatMetricRow(label = "Pronunciation", value = "${analysis.pronunciationScore}%")
                    StatMetricRow(label = "Fillers Detected", value = "${analysis.fillerCount} (${if (analysis.detectedFillers.isEmpty()) "None" else analysis.detectedFillers.joinToString(", ")})")
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Transcript with Feedback
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "SPEECH TRANSCRIPT",
                color = TaglineText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"${analysis.transcript}\"",
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Personalized AI Coach Feedback
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "PERSONALIZED AI FEEDBACK",
                color = TaglineText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = analysis.personalizedFeedback,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Strengths
            Text(
                text = "Key Strengths:",
                color = GreenNode,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            analysis.strengths.forEach { s ->
                Text(text = "• $s", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Areas for improvement
            Text(
                text = "Areas for Improvement:",
                color = Color(0xFFD97706),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            analysis.areasForImprovement.forEach { imp ->
                Text(text = "• $imp", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Suggested daily task
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TaglineBackground)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "🎯 Daily Recommended Speaking Task",
                        color = HeadingPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = analysis.suggestedDailyTask,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Actions
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onPracticeAgain,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
        ) {
            Text(
                text = "Practice Again 🎙️",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onReturnDashboard,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Text(
                text = "View Dashboard 📊",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * REUSABLE COMPONENTS
 */
@Composable
fun StatMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ModuleCard(
    modifier: Modifier = Modifier,
    module: PublicSpeakingModule,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = module.iconEmoji, fontSize = 22.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = module.badge,
                        color = TaglineText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = module.title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = module.subtitle,
                color = TextSecondary,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Start Drill →",
                color = AccentPurple,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PracticeHistoryCard(session: SpeechPracticeSessionItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(TaglineBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        MicIcon(size = 14.dp, color = AccentPurple)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = session.moduleName,
                            color = HeadingPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = session.dateText,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${session.overallScore}%",
                        color = GreenNode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = session.topicTitle,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = session.feedback,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(text = "Speed: ${session.wpm} WPM", color = TaglineText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "Clarity: ${session.clarityScore}%", color = TaglineText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "Duration: ${session.durationSeconds}s", color = TextSecondary, fontSize = 10.sp)
            }
        }
    }
}

/**
 * 12-Bar Animated Audio Waveform Visualizer
 */
@Composable
fun StudioWaveformBars(
    isRecording: Boolean,
    rmsLevel: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "studio_waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val barCount = 12
    val normalizedRms = ((rmsLevel + 2f) / 12f).coerceIn(0.1f, 1.0f)

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(44.dp)
    ) {
        for (i in 0 until barCount) {
            val sineFactor = (sin(phase + (i * 0.6f)) + 1f) / 2f
            val barHeightFraction = if (isRecording) {
                (0.20f + (sineFactor * 0.65f * normalizedRms)).coerceIn(0.15f, 1.0f)
            } else {
                0.20f
            }

            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight(barHeightFraction)
                    .clip(RoundedCornerShape(100.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                        )
                    )
            )
        }
    }
}
