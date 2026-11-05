package com.simats.aiskillmirror.ui.coach

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.dashboard.MicIcon
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.splash.MirrorLogoIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*
import kotlin.math.sin

@Composable
fun AiCoachScreen(
    viewModel: AiCoachViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onStartPracticeFromCoachClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Voice Manager instance
    val voiceManager = remember {
        VoiceCoachManager(
            context = context,
            onStateChanged = { state -> viewModel.setVoiceState(state) },
            onPartialText = { text -> viewModel.setUserSpokenText(text) },
            onFinalText = { text ->
                if (text.isNotBlank()) {
                    viewModel.setUserSpokenText(text)
                    if (!viewModel.uiState.value.isEditingSpokenText) {
                        viewModel.sendVoiceMessage(text) { reply ->
                            viewModel.setVoiceErrorMessage(null)
                        }
                    }
                }
            },
            onRmsChanged = { rms -> viewModel.setAudioRmsLevel(rms) },
            onError = { err -> viewModel.setVoiceErrorMessage(err) },
            onTtsFinished = {
                if (viewModel.uiState.value.isVoiceModeActive && viewModel.uiState.value.isContinuousMode) {
                    // Automatically listen again for continuous conversation
                    viewModel.setVoiceErrorMessage(null)
                }
            }
        )
    }

    // Keep voiceManager settings in sync
    LaunchedEffect(uiState.voiceLanguage) {
        voiceManager.setLanguage(uiState.voiceLanguage)
    }
    LaunchedEffect(uiState.isContinuousMode) {
        voiceManager.isContinuousMode = uiState.isContinuousMode
    }
    LaunchedEffect(uiState.isAudioMuted) {
        voiceManager.isMuted = uiState.isAudioMuted
    }

    // Speak AI response when latest response changes and voice mode is active
    LaunchedEffect(uiState.latestAiVoiceResponse) {
        if (uiState.isVoiceModeActive && uiState.latestAiVoiceResponse.isNotBlank() && !uiState.isAudioMuted) {
            voiceManager.speak(uiState.latestAiVoiceResponse)
        }
    }

    // Handle Toast events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AiCoachUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    // Cleanup voice manager on dispose
    DisposableEffect(Unit) {
        onDispose {
            voiceManager.destroy()
        }
    }

    // Microphone Permission Launcher
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setVoiceMode(true)
            voiceManager.startListening()
        } else {
            Toast.makeText(context, "Microphone permission is required for voice chat", Toast.LENGTH_SHORT).show()
        }
    }

    fun openVoiceChat() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.setVoiceMode(true)
            voiceManager.startListening()
        } else {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
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
            // Scrollable Chat Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
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
                                    onBackClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            BackArrowIcon(size = 18.dp, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = uiState.coachName,
                                color = HeadingPurple,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.statusText,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Action Controls: Voice Mode Pill & Language Switcher
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Voice Chat Launch Pill
                        Box(
                            modifier = Modifier
                                .shadow(3.dp, RoundedCornerShape(100.dp), spotColor = PrimaryGradientStart)
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                    )
                                )
                                .clickable { openVoiceChat() }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MicIcon(size = 14.dp, color = Color.White)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Voice Chat",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Clear Conversation Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .shadow(2.dp, RoundedCornerShape(10.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                                .clip(RoundedCornerShape(10.dp))
                                .background(CardBackground)
                                .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                                .clickable { viewModel.clearConversation() },
                            contentAlignment = Alignment.Center
                        ) {
                            TrashBinIcon(size = 16.dp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sub-header Notice Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            SparkleIcon(size = 16.dp, color = TaglineText)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.bannerNoticeText,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Language Toggle Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(TaglineBackground)
                                .clickable {
                                    val next = if (uiState.voiceLanguage == VoiceLanguage.ENGLISH) VoiceLanguage.TAMIL else VoiceLanguage.ENGLISH
                                    viewModel.setVoiceLanguage(next)
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (uiState.voiceLanguage == VoiceLanguage.ENGLISH) "🌐 EN" else "🌐 தமிழ்",
                                color = TaglineText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Timestamp Header
                Text(
                    text = uiState.timestampHeader,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Chat Messages List
                uiState.messages.forEachIndexed { index, message ->
                    val isLastMessage = index == uiState.messages.size - 1

                    if (message.isUser) {
                        // User Right-Aligned Message Bubble
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .shadow(2.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                                    .background(TaglineBackground)
                                    .border(1.dp, CardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = message.text,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    } else {
                        // AI Coach Left-Aligned Message Bubble
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            // Mira Avatar Icon
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(CenterCardGradientStart, CenterCardGradientEnd)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                MirrorLogoIcon(size = 18.dp, color = Color.White)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 290.dp)
                                        .shadow(2.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
                                        .background(CardBackground)
                                        .border(1.dp, CardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = message.text,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 19.sp,
                                            fontWeight = FontWeight.Normal
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Play audio response icon button
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        voiceManager.speak(message.text)
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    SpeakerIcon(size = 14.dp, color = AccentPurple)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "Listen",
                                                        color = AccentPurple,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Suggestion Chips below last AI message
                                if (isLastMessage) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        uiState.suggestionChips.forEach { chipText ->
                                            Box(
                                                modifier = Modifier
                                                    .shadow(1.dp, RoundedCornerShape(100.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                                                    .clip(RoundedCornerShape(100.dp))
                                                    .background(CardBackground)
                                                    .border(1.dp, CardBorder, RoundedCornerShape(100.dp))
                                                    .clickable {
                                                        if (chipText.contains("practice", ignoreCase = true)) {
                                                            onStartPracticeFromCoachClick()
                                                        } else {
                                                            viewModel.onSuggestionChipClick(chipText)
                                                        }
                                                    }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = chipText,
                                                    color = AccentPurple,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Input Bar with Mic button & Send button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(26.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                    .clip(RoundedCornerShape(26.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(26.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.currentInputText.isEmpty()) {
                            Text(
                                text = if (uiState.voiceLanguage == VoiceLanguage.TAMIL) "மீராவிடம் கேளுங்கள்..." else "Ask Mira anything...",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                        BasicTextField(
                            value = uiState.currentInputText,
                            onValueChange = { viewModel.onInputChange(it) },
                            textStyle = TextStyle(color = TextPrimary, fontSize = 13.sp),
                            cursorBrush = SolidColor(AccentPurple),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Interactive Mic Button to trigger voice chat mode
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(TaglineBackground)
                                .clickable { openVoiceChat() },
                            contentAlignment = Alignment.Center
                        ) {
                            MicIcon(size = 18.dp, color = AccentPurple)
                        }

                        // Send Up-Arrow Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .shadow(8.dp, CircleShape, spotColor = PrimaryGradientStart)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                    )
                                )
                                .clickable { viewModel.onSendMessage() },
                            contentAlignment = Alignment.Center
                        ) {
                            UpArrowIcon(size = 16.dp, color = Color.White)
                        }
                    }
                }
            }

            // Fixed Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.LEARN,
                onTabSelected = onTabSelected
            )
        }

        // ==========================================
        // FULL-SCREEN VOICE CONVERSATION OVERLAY
        // ==========================================
        AnimatedVisibility(
            visible = uiState.isVoiceModeActive,
            enter = fadeIn(tween(250)) + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            FullScreenVoiceChatOverlay(
                uiState = uiState,
                onClose = {
                    voiceManager.stopSpeaking()
                    voiceManager.cancelListening()
                    viewModel.setVoiceMode(false)
                },
                onStartListening = {
                    voiceManager.startListening()
                },
                onStopListening = {
                    voiceManager.stopListening()
                },
                onPause = {
                    voiceManager.pauseConversation()
                },
                onResume = {
                    voiceManager.resumeConversation()
                },
                onToggleContinuousMode = {
                    viewModel.setContinuousMode(!uiState.isContinuousMode)
                },
                onToggleMute = {
                    val newMute = !uiState.isAudioMuted
                    viewModel.setAudioMuted(newMute)
                    if (newMute) {
                        voiceManager.stopSpeaking()
                    }
                },
                onReplayAudio = {
                    if (uiState.latestAiVoiceResponse.isNotBlank()) {
                        voiceManager.speak(uiState.latestAiVoiceResponse)
                    }
                },
                onLanguageToggle = {
                    val nextLang = if (uiState.voiceLanguage == VoiceLanguage.ENGLISH) VoiceLanguage.TAMIL else VoiceLanguage.ENGLISH
                    viewModel.setVoiceLanguage(nextLang)
                },
                onEditSpokenText = { newText ->
                    viewModel.setUserSpokenText(newText)
                },
                onToggleEditing = { isEditing ->
                    viewModel.setEditingSpokenText(isEditing)
                },
                onSendEditedSpokenText = { text ->
                    voiceManager.stopListening()
                    viewModel.sendVoiceMessage(text) {
                        // Handled automatically by LaunchedEffect(uiState.latestAiVoiceResponse)
                    }
                },
                onClearChat = {
                    viewModel.clearConversation()
                }
            )
        }
    }
}

/**
 * Full-Screen Interactive Voice Chat Interface
 */
@Composable
fun FullScreenVoiceChatOverlay(
    uiState: AiCoachUiState,
    onClose: () -> Unit,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onToggleContinuousMode: () -> Unit,
    onToggleMute: () -> Unit,
    onReplayAudio: () -> Unit,
    onLanguageToggle: () -> Unit,
    onEditSpokenText: (String) -> Unit,
    onToggleEditing: (Boolean) -> Unit,
    onSendEditedSpokenText: (String) -> Unit,
    onClearChat: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Status Label & Color
    val (statusLabel, statusColor, statusBadgeBg) = when (uiState.voiceState) {
        VoiceCoachState.LISTENING -> Triple(
            if (uiState.voiceLanguage == VoiceLanguage.TAMIL) "கேட்கிறது... (பேசுங்கள்)" else "Listening... (Speak now)",
            Color(0xFF16A34A),
            Color(0xFFE8F5E9)
        )
        VoiceCoachState.PROCESSING -> Triple(
            if (uiState.voiceLanguage == VoiceLanguage.TAMIL) "சிந்திக்கிறது..." else "Thinking...",
            AccentPurple,
            Color(0xFFE9DDFF)
        )
        VoiceCoachState.SPEAKING -> Triple(
            if (uiState.voiceLanguage == VoiceLanguage.TAMIL) "பேசுகிறது..." else "Speaking...",
            Color(0xFF9353E2),
            Color(0xFFF3E8FF)
        )
        VoiceCoachState.PAUSED -> Triple(
            if (uiState.voiceLanguage == VoiceLanguage.TAMIL) "இடைநிறுத்தப்பட்டது" else "Paused",
            Color(0xFFD97706),
            Color(0xFFFEF3C7)
        )
        VoiceCoachState.ERROR -> Triple(
            if (uiState.voiceLanguage == VoiceLanguage.TAMIL) "பிழை" else "Error",
            Color(0xFFDC2626),
            Color(0xFFFEE2E2)
        )
        VoiceCoachState.IDLE -> Triple(
            if (uiState.voiceLanguage == VoiceLanguage.TAMIL) "தயார் (மைக் தொடவும்)" else "Ready (Tap Mic to Speak)",
            TextSecondary,
            Color(0xFFEDE5FA)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // TOP CONTROLS BAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exit Voice Mode button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    BackArrowIcon(size = 18.dp, color = TextPrimary)
                }

                // Status Badge in the center
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(statusBadgeBg)
                        .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Language Selector Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .clickable { onLanguageToggle() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (uiState.voiceLanguage == VoiceLanguage.ENGLISH) "EN ▾" else "தமிழ் ▾",
                        color = AccentPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // ANIMATED VOICE ORB & AUDIO WAVEFORM
            // ==========================================
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                VoiceOrbGraphic(
                    voiceState = uiState.voiceState,
                    rmsLevel = uiState.audioRmsLevel,
                    onOrbClick = {
                        when (uiState.voiceState) {
                            VoiceCoachState.LISTENING -> onStopListening()
                            VoiceCoachState.SPEAKING -> onPause()
                            else -> onStartListening()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Voice Waveform Visualizer
                VoiceWaveformBars(
                    isActive = uiState.voiceState == VoiceCoachState.LISTENING || uiState.voiceState == VoiceCoachState.SPEAKING,
                    rmsLevel = uiState.audioRmsLevel
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // REAL-TIME CONVERSATION CARDS
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // User Spoken Words Card
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(TaglineBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    MicIcon(size = 14.dp, color = AccentPurple)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.voiceLanguage == VoiceLanguage.TAMIL) "உங்கள் பேச்சு (User)" else "You said",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Edit text toggle button
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (uiState.isEditingSpokenText) "Cancel" else "Edit",
                                    color = TaglineText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clickable { onToggleEditing(!uiState.isEditingSpokenText) }
                                        .padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (uiState.isEditingSpokenText) {
                            OutlinedTextField(
                                value = uiState.userSpokenText,
                                onValueChange = onEditSpokenText,
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentPurple,
                                    unfocusedBorderColor = CardBorder,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onSendEditedSpokenText(uiState.userSpokenText) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                            ) {
                                Text(
                                    text = "Send to AI Coach",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            val displayText = uiState.userSpokenText.ifBlank {
                                if (uiState.voiceState == VoiceCoachState.LISTENING) {
                                    if (uiState.voiceLanguage == VoiceLanguage.TAMIL) "உங்கள் குரலைக் கேட்கிறது..." else "Listening to your voice..."
                                } else {
                                    if (uiState.voiceLanguage == VoiceLanguage.TAMIL) "மைக் பொத்தானைத் தட்டி பேசத் தொடங்குங்கள்." else "Tap the microphone below and speak naturally."
                                }
                            }
                            Text(
                                text = displayText,
                                color = if (uiState.userSpokenText.isBlank()) TextSecondary else TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = if (uiState.userSpokenText.isNotBlank()) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }

                // AI Coach Response Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(18.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp)
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
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(CenterCardGradientStart, CenterCardGradientEnd)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    MirrorLogoIcon(size = 14.dp, color = Color.White)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Mira (AI Coach)",
                                    color = HeadingPurple,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Replay audio button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(TaglineBackground)
                                    .clickable { onReplayAudio() }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    SpeakerIcon(size = 12.dp, color = AccentPurple)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Replay",
                                        color = AccentPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = uiState.latestAiVoiceResponse,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                // Error Notification Banner
                if (!uiState.voiceErrorMessage.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEE2E2))
                            .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Notice: ${uiState.voiceErrorMessage}",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // PRIMARY VOICE CONTROLS ROW
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Audio Toggle
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(2.dp, CircleShape, spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                            .clip(CircleShape)
                            .background(if (uiState.isAudioMuted) Color(0xFFFEE2E2) else CardBackground)
                            .border(1.dp, if (uiState.isAudioMuted) Color(0xFFFCA5A5) else CardBorder, CircleShape)
                            .clickable { onToggleMute() },
                        contentAlignment = Alignment.Center
                    ) {
                        SpeakerMuteIcon(size = 20.dp, color = if (uiState.isAudioMuted) Color(0xFFDC2626) else TextSecondary)
                    }

                    // Main Primary Mic / Action Button
                    val isListening = uiState.voiceState == VoiceCoachState.LISTENING
                    val isSpeaking = uiState.voiceState == VoiceCoachState.SPEAKING

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(12.dp, CircleShape, spotColor = PrimaryGradientStart)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = if (isListening) {
                                        listOf(Color(0xFF16A34A), Color(0xFF22C55E))
                                    } else {
                                        listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                    }
                                )
                            )
                            .clickable {
                                when {
                                    isListening -> onStopListening()
                                    isSpeaking -> onPause()
                                    else -> onStartListening()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSpeaking) {
                            PauseIcon(size = 28.dp, color = Color.White)
                        } else {
                            MicIcon(size = 28.dp, color = Color.White)
                        }
                    }

                    // Pause / Resume Toggle
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(2.dp, CircleShape, spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                            .clip(CircleShape)
                            .background(CardBackground)
                            .border(1.dp, CardBorder, CircleShape)
                            .clickable {
                                if (uiState.voiceState == VoiceCoachState.PAUSED) onResume() else onPause()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.voiceState == VoiceCoachState.PAUSED) {
                            PlayMiniIcon(size = 20.dp, color = AccentPurple)
                        } else {
                            PauseIcon(size = 20.dp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Continuous Two-Way Conversation Switch Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Continuous Conversation",
                            color = HeadingPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Automatically listens after Mira answers",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = uiState.isContinuousMode,
                        onCheckedChange = { onToggleContinuousMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentPurple,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = Color(0xFFEDE5FA)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Buttons: End Voice Chat & Clear Chat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onClearChat,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(
                            text = "Clear Voice Chat",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                    ) {
                        Text(
                            text = "Switch to Text",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated Central Pulsing Orb for Voice Chat
 */
@Composable
fun VoiceOrbGraphic(
    voiceState: VoiceCoachState,
    rmsLevel: Float,
    onOrbClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice_orb_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_pulse"
    )

    val normalizedRms = ((rmsLevel + 2f) / 12f).coerceIn(0f, 1f)
    val dynamicScale = if (voiceState == VoiceCoachState.LISTENING) {
        pulseScale + (normalizedRms * 0.15f)
    } else if (voiceState == VoiceCoachState.SPEAKING) {
        pulseScale + 0.05f
    } else {
        1.0f
    }

    Box(
        modifier = Modifier
            .size(170.dp)
            .scale(dynamicScale)
            .clickable { onOrbClick() },
        contentAlignment = Alignment.Center
    ) {
        // Concentric Glowing Rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.width / 2

            // Outer Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PrimaryGradientStart.copy(alpha = if (voiceState == VoiceCoachState.LISTENING) 0.25f else 0.14f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius
                ),
                radius = maxRadius,
                center = center
            )

            // Outer Ring
            drawCircle(
                color = if (voiceState == VoiceCoachState.LISTENING) Color(0xFF22C55E).copy(alpha = 0.4f) else CardBorder,
                radius = maxRadius * 0.90f,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Inner Ring
            drawCircle(
                color = if (voiceState == VoiceCoachState.LISTENING) Color(0xFF22C55E).copy(alpha = 0.6f) else TaglineBackground,
                radius = maxRadius * 0.72f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Central Gradient Core Card
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(12.dp, CircleShape, spotColor = PrimaryGradientStart)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = if (voiceState == VoiceCoachState.LISTENING) {
                            listOf(Color(0xFF16A34A), Color(0xFF22C55E))
                        } else {
                            listOf(PrimaryGradientStart, PrimaryGradientEnd)
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            MirrorLogoIcon(size = 36.dp, color = Color.White)
        }
    }
}

/**
 * Animated Audio Waveform Visualizer Bars
 */
@Composable
fun VoiceWaveformBars(
    isActive: Boolean,
    rmsLevel: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val barCount = 7
    val normalizedRms = ((rmsLevel + 2f) / 12f).coerceIn(0.1f, 1.0f)

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(42.dp)
    ) {
        for (i in 0 until barCount) {
            val sineFactor = (sin(phase + (i * 0.8f)) + 1f) / 2f
            val barHeightFraction = if (isActive) {
                (0.25f + (sineFactor * 0.50f * normalizedRms)).coerceIn(0.2f, 1.0f)
            } else {
                0.20f
            }

            Box(
                modifier = Modifier
                    .width(6.dp)
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

// ==========================================
// CUSTOM VECTOR ICONS
// ==========================================
@Composable
fun SpeakerIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.15f, h * 0.38f)
                lineTo(w * 0.35f, h * 0.38f)
                lineTo(w * 0.60f, h * 0.18f)
                lineTo(w * 0.60f, h * 0.82f)
                lineTo(w * 0.35f, h * 0.62f)
                lineTo(w * 0.15f, h * 0.62f)
                close()

                // Sound waves
                moveTo(w * 0.72f, h * 0.35f)
                quadraticTo(w * 0.84f, h * 0.50f, w * 0.72f, h * 0.65f)

                moveTo(w * 0.82f, h * 0.25f)
                quadraticTo(w * 0.98f, h * 0.50f, w * 0.82f, h * 0.75f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun SpeakerMuteIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.15f, h * 0.38f)
                lineTo(w * 0.35f, h * 0.38f)
                lineTo(w * 0.60f, h * 0.18f)
                lineTo(w * 0.60f, h * 0.82f)
                lineTo(w * 0.35f, h * 0.62f)
                lineTo(w * 0.15f, h * 0.62f)
                close()

                // Diagonal strike through
                moveTo(w * 0.70f, h * 0.35f)
                lineTo(w * 0.92f, h * 0.65f)
                moveTo(w * 0.92f, h * 0.35f)
                lineTo(w * 0.70f, h * 0.65f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun PauseIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = w * 0.18f

        drawLine(
            color = color,
            start = Offset(w * 0.36f, h * 0.22f),
            end = Offset(w * 0.36f, h * 0.78f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = Offset(w * 0.64f, h * 0.22f),
            end = Offset(w * 0.64f, h * 0.78f),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun PlayMiniIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawPath(
            path = Path().apply {
                moveTo(w * 0.28f, h * 0.20f)
                lineTo(w * 0.82f, h * 0.50f)
                lineTo(w * 0.28f, h * 0.80f)
                close()
            },
            color = color
        )
    }
}

@Composable
fun TrashBinIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                // Lid
                moveTo(w * 0.20f, h * 0.25f)
                lineTo(w * 0.80f, h * 0.25f)
                moveTo(w * 0.40f, h * 0.25f)
                lineTo(w * 0.40f, h * 0.15f)
                lineTo(w * 0.60f, h * 0.15f)
                lineTo(w * 0.60f, h * 0.25f)

                // Can
                moveTo(w * 0.25f, h * 0.25f)
                lineTo(w * 0.28f, h * 0.85f)
                lineTo(w * 0.72f, h * 0.85f)
                lineTo(w * 0.75f, h * 0.25f)

                // Vertical lines
                moveTo(w * 0.42f, h * 0.35f)
                lineTo(w * 0.42f, h * 0.75f)
                moveTo(w * 0.58f, h * 0.35f)
                lineTo(w * 0.58f, h * 0.75f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun UpArrowIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 2.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.50f, h * 0.82f)
                lineTo(w * 0.50f, h * 0.18f)
                moveTo(w * 0.22f, h * 0.45f)
                lineTo(w * 0.50f, h * 0.18f)
                lineTo(w * 0.78f, h * 0.45f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AiCoachScreenPreview() {
    AISkillsMirrorTheme {
        AiCoachScreen(
            viewModel = AiCoachViewModel()
        )
    }
}
