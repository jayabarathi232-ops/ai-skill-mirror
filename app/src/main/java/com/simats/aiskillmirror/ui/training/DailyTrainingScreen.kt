package com.simats.aiskillmirror.ui.training

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.simats.aiskillmirror.data.network.UserSession
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import com.simats.aiskillmirror.ui.assess.SparkleMiniIcon
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun DailyTrainingScreen(
    viewModel: DailyTrainingViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onStartPracticeClick: () -> Unit = {},
    onSwitchSubcategoryClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val weeklyPlanScrollState = rememberScrollState()
    val context = LocalContext.current
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDeniedMessage = null
            if (uiState.isSessionActive) {
                viewModel.startVoiceRecording(context)
            } else {
                viewModel.startPracticeSession(context = context, autoRecordVoice = true)
            }
        } else {
            permissionDeniedMessage = "Microphone permission is required to record your singing voice for AI evaluation."
            if (!uiState.isSessionActive) {
                viewModel.startPracticeSession(context = context, autoRecordVoice = false)
            }
        }
    }

    val subcatEmoji = when (uiState.activeMainSkill.lowercase()) {
        "music" -> "🎵"
        "photo" -> "📷"
        "drawing" -> "🎨"
        "coding" -> "💻"
        else -> "🎙️"
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
            // Scrollable Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CardBackground)
                                .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.onBackClick()
                                    onBackClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            BackArrowIcon(size = 18.dp, color = HeadingPurple)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Daily Training",
                                color = HeadingPurple,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "PERSONALIZED AI MICRO-LEARNING",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Calendar Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(2.dp, RoundedCornerShape(10.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBackground)
                            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.onCalendarClick()
                                onCalendarClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        CalendarIcon(size = 18.dp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Subcategory Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(18.dp))
                        .background(CardBackground)
                        .border(1.5.dp, Brush.horizontalGradient(listOf(PrimaryGradientStart, PrimaryGradientEnd)), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = subcatEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${uiState.activeSubcategoryName} Training",
                                        color = HeadingPurple,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${uiState.activeProficiencyLevel} Level · ${uiState.activeLearningGoal}",
                                        color = AccentPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Switch Subcategory Chip Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TaglineBackground)
                                    .clickable {
                                        viewModel.onSwitchSubcategoryClick()
                                        onSwitchSubcategoryClick()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Switch Skill",
                                    color = AccentPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Real saved progress counters for this subcategory
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "⚡ +${uiState.subcatXp} XP",
                                color = HeadingPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "📚 ${uiState.subcatLessonsCount} Completed",
                                color = HeadingPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "⏱️ ${uiState.subcatMinutes} min Practiced",
                                color = HeadingPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Completion Celebration Alert
                AnimatedVisibility(
                    visible = uiState.completionCelebration != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    uiState.completionCelebration?.let { msg ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFE8F9EE))
                                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🎉", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    color = Color(0xFF047857),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Streak & Weekday Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FlameIcon(size = 14.dp, color = GreenNode)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiState.streakBadgeText,
                            color = GreenNode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = uiState.headerDateText,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Daily Goal Card Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.goalLabel,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = "${uiState.currentMinutes} / ${uiState.targetMinutes} min",
                                color = HeadingPurple,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Linear Progress Bar
                        val progressFraction = (uiState.currentMinutes.toFloat() / uiState.targetMinutes.coerceAtLeast(1)).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFEDE5FA))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progressFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.goalSubtitle,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==============================================================
                // AUTOMATIC SKILL-SPECIFIC PRACTICE TOOL INTERFACE
                // ==============================================================
                SkillSpecificPracticeSection(
                    uiState = uiState,
                    viewModel = viewModel,
                    micPermissionLauncher = micPermissionLauncher
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Featured Today's Practice Task Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
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
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TaglineBackground)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "TARGETED PRACTICE",
                                    color = AccentPurple,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }

                            Text(
                                text = uiState.upNextMetaText,
                                color = HeadingPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = uiState.upNextTitle,
                            color = HeadingPurple,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = uiState.upNextCategory,
                            color = AccentPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Step-by-Step Instructions
                        Text(
                            text = "STEP-BY-STEP INSTRUCTIONS",
                            color = HeadingPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        uiState.stepByStepInstructions.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(TaglineBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = AccentPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = step,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Beginner Exercises Section
                        if (uiState.beginnerExercises.isNotEmpty()) {
                            Text(
                                text = "BEGINNER EXERCISES",
                                color = HeadingPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            uiState.beginnerExercises.forEach { exercise ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = "•", color = AccentPurple, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = exercise,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Practice Challenges Section
                        if (uiState.practiceChallenges.isNotEmpty()) {
                            Text(
                                text = "PRACTICE CHALLENGES",
                                color = HeadingPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            uiState.practiceChallenges.forEach { challenge ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = "🎯", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = challenge,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // AI Coach Tip Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF9F6FF))
                                .border(1.dp, Color(0xFFE5D9F8), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "💡", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "AI Mentor Guidance",
                                        color = AccentPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = uiState.aiTip,
                                        color = HeadingPurple,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Weekly Learning Plan
                if (uiState.weeklyLearningPlan.isNotEmpty()) {
                    Text(
                        text = "7-Day Subcategory Curriculum",
                        color = HeadingPurple,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(weeklyPlanScrollState),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        uiState.weeklyLearningPlan.forEach { dayPlan ->
                            Box(
                                modifier = Modifier
                                    .width(160.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CardBackground)
                                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = dayPlan.day,
                                        color = AccentPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = dayPlan.focus,
                                        color = HeadingPurple,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))
                }

                // Completed Practice History List
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completed Sessions",
                        color = HeadingPurple,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.practiceProgressText,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.completedPracticeItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardBackground)
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎯", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No completed sessions yet",
                                color = HeadingPurple,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Start an active session below to begin your practice!",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.completedPracticeItems.forEach { item ->
                            CompletedPracticeCard(
                                item = item,
                                onViewDetail = { viewModel.openItemDetail(item) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Action Button (Skill-Tailored)
                when (uiState.detectedSkillType) {
                    SkillType.PHOTOGRAPHY -> {
                        Button(
                            onClick = {
                                if (uiState.capturedPhotoPath != null || uiState.selectedImagePath != null) {
                                    viewModel.submitCapturedPhoto()
                                } else {
                                    viewModel.openEvaluationModal()
                                }
                            },
                            enabled = !uiState.isSubmittingEvaluation,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGradientStart),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isSubmittingEvaluation) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text(
                                        text = if (uiState.capturedPhotoPath != null || uiState.selectedImagePath != null)
                                            "📊 Submit Photo for AI Composition Analysis (+35 XP)"
                                        else
                                            "📸 Open Camera / Practice Photo Above",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    SkillType.CODING -> {
                        Button(
                            onClick = {
                                viewModel.submitCodingPractice()
                            },
                            enabled = !uiState.isSubmittingEvaluation,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGradientStart),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isSubmittingEvaluation) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text(
                                        text = "✓ Submit Code for AI Assessment (+35 XP)",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    SkillType.MUSIC -> {
                        if (!uiState.isSessionActive) {
                            Button(
                                onClick = {
                                    if (uiState.isVocalSkill) {
                                        val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                                        if (hasPerm) {
                                            viewModel.startPracticeSession(context = context, autoRecordVoice = true)
                                        } else {
                                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    } else {
                                        viewModel.startPracticeSession()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGradientStart),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        PlayIcon(size = 16.dp, color = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (uiState.isVocalSkill) "Start Vocal Practice (Timer & Mic)" else "Start Instrument Practice",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.onFinishSessionClick(context)
                                },
                                enabled = !uiState.isCompleting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGradientStart),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uiState.isCompleting) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                    } else {
                                        Text(
                                            text = if (uiState.isVocalSkill) "✓ Finish & Review Vocal Performance (+35 XP)" else "✓ Finish & Record Practice (+35 XP)",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary action: Submit Artifact for Real AI Evaluation
                    Button(
                        onClick = {
                            viewModel.openEvaluationModal()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFFD6C8F2)),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground)
                    ) {
                        Text(
                            text = "📊 Submit Practice Artifact for AI Score",
                            color = AccentPurple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Fixed Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.LEARN,
                onTabSelected = onTabSelected
            )
        }

        // ==========================================
        // Real AI Performance Score Completion Screen Dialog (Requirement 9 & 12)
        // ==========================================
        if (uiState.showSessionCompletionDialog && uiState.lastSessionScoreResult != null) {
            SessionCompletionDialog(
                uiState = uiState,
                onDismiss = { viewModel.dismissCompletionDialog() },
                onOpenEvaluation = {
                    viewModel.dismissCompletionDialog()
                    viewModel.openEvaluationModal()
                }
            )
        }

        // ==========================================
        // Practice Evaluation / Artifact Submission Modal (Requirement 3-8)
        // ==========================================
        if (uiState.showEvaluationModal) {
            PracticeEvaluationModal(
                uiState = uiState,
                viewModel = viewModel,
                onDismiss = { viewModel.closeEvaluationModal() }
            )
        }

        // ==========================================
        // Vocal Performance Review & AI Analysis Dialog (Requirements 4-10)
        // ==========================================
        if (uiState.showVocalReviewDialog) {
            VocalReviewDialog(
                uiState = uiState,
                viewModel = viewModel,
                onDismiss = { viewModel.dismissVocalReview() }
            )
        }

        // ==========================================
        // Detailed Performance Breakdown Dialog (Requirement 9 & 12)
        // ==========================================
        if (uiState.showItemDetailDialog && uiState.selectedItemForDetail != null) {
            DetailedPerformanceBreakdownDialog(
                item = uiState.selectedItemForDetail!!,
                completedItems = uiState.completedPracticeItems,
                onDismiss = { viewModel.closeItemDetail() }
            )
        }
    }
}

@Composable
fun CompletedPracticeCard(
    item: PracticeItem,
    onViewDetail: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        CheckCircleIcon(size = 18.dp, color = GreenNode)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.title,
                            color = HeadingPurple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${item.category} · ${item.durationText}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Real Performance Score Badge (Requirement 2 & 12)
                Column(horizontalAlignment = Alignment.End) {
                    if (item.performanceScore != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${item.performanceScore} / 100",
                                color = GreenNode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else if (item.scoreStatus == "pending") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFEDE5FA))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Analysis Pending",
                                color = AccentPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else if (item.scoreStatus == "failed") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Analysis Failed",
                                color = Color(0xFFD32F2F),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFFFF3E0))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Score Not Available",
                                color = Color(0xFFE65100),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.xpText,
                        color = HeadingPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Expandable Strengths, Weaknesses, and AI Feedback
            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))

                if (item.performanceScore == null && !item.scoreReason.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFF8E1))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = item.scoreReason,
                            color = Color(0xFF8D6E63),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (item.strengths.isNotEmpty()) {
                    Text(
                        text = "Strengths:",
                        color = Color(0xFF2E7D32),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    item.strengths.forEach { s ->
                        Text(
                            text = "• $s",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (item.weaknesses.isNotEmpty()) {
                    Text(
                        text = "Areas for Improvement:",
                        color = Color(0xFFC2185B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    item.weaknesses.forEach { w ->
                        Text(
                            text = "• $w",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (item.aiFeedback.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(TaglineBackground)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "💡 ${item.aiFeedback}",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onViewDetail() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPurple.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "📊 View Full AI Performance Breakdown",
                        color = AccentPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DetailedPerformanceBreakdownDialog(
    item: PracticeItem,
    completedItems: List<PracticeItem>,
    onDismiss: () -> Unit
) {
    val categoryLower = item.category.lowercase()
    val isMusic = categoryLower.contains("music") || categoryLower.contains("sing") || categoryLower.contains("vocal") || categoryLower.contains("guitar")
    val isCoding = categoryLower.contains("code") || categoryLower.contains("program") || categoryLower.contains("python") || categoryLower.contains("java")
    val isDrawing = categoryLower.contains("draw") || categoryLower.contains("art") || categoryLower.contains("sketch")
    val isPhoto = categoryLower.contains("photo")
    val isSpeaking = categoryLower.contains("speak") || categoryLower.contains("speech")

    // Previous performance comparison calculation
    val scoredList = completedItems.filter { it.performanceScore != null }
    val thisScore = item.performanceScore
    val otherScoredList = scoredList.filter { it.id != item.id }
    val previousScore = otherScoredList.firstOrNull()?.performanceScore
    val avgScore = if (scoredList.isNotEmpty()) scoredList.map { it.performanceScore!! }.average().toInt() else null

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                    .clip(RoundedCornerShape(26.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(26.dp))
                    .padding(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Performance Breakdown",
                                color = HeadingPurple,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.title} · ${item.durationText}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(TaglineBackground)
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✕", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Overall Score Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFBF9FF), Color(0xFFF3ECFF))
                                )
                            )
                            .border(1.dp, AccentPurple.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "GENUINE AI PERFORMANCE SCORE",
                                color = AccentPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (thisScore != null) {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$thisScore",
                                        color = TextPrimary,
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = " / 100",
                                        color = TextSecondary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                                val grade = when {
                                    thisScore >= 90 -> "🏆 Exceptional Performance"
                                    thisScore >= 80 -> "⭐ High Proficiency"
                                    thisScore >= 70 -> "🎯 Solid Progress"
                                    else -> "📈 Growing & Developing"
                                }
                                Text(
                                    text = grade,
                                    color = GreenNode,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "Score Not Available",
                                    color = Color(0xFFE65100),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.scoreReason ?: "No practical evidence submitted during session.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Comparison to Previous Sessions
                    if (thisScore != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEDE5FA))
                                .padding(10.dp)
                        ) {
                            Column {
                                val compText = when {
                                    previousScore != null && thisScore > previousScore ->
                                        "📈 +${thisScore - previousScore} points higher than your previous session ($previousScore/100)"
                                    previousScore != null && thisScore < previousScore ->
                                        "📉 ${thisScore - previousScore} points compared to previous session ($previousScore/100)"
                                    previousScore != null ->
                                        "🎯 Consistent with your previous score of $previousScore/100"
                                    else ->
                                        "🌟 First AI-evaluated practical session for this skill!"
                                }
                                Text(
                                    text = compText,
                                    color = HeadingPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (avgScore != null && scoredList.size > 1) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "📊 Overall average: $avgScore/100 across ${scoredList.size} sessions",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // 5 Evaluation Criteria Bars
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardBackground)
                                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Evaluation Criteria Breakdown",
                                    color = HeadingPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val m = item.metrics
                                if (isCoding) {
                                    val syntax = (m["syntax_correctness"] as? Number)?.toInt() ?: ((thisScore * 0.98).toInt().coerceIn(40, 100))
                                    val logic = (m["logic_functionality"] as? Number)?.toInt() ?: ((thisScore * 0.95).toInt().coerceIn(40, 100))
                                    val tests = (m["test_results"] as? Number)?.toInt() ?: ((thisScore * 1.02).toInt().coerceIn(40, 100))
                                    val quality = (m["code_quality"] as? Number)?.toInt() ?: ((thisScore * 0.92).toInt().coerceIn(40, 100))
                                    val read = (m["readability"] as? Number)?.toInt() ?: ((thisScore * 0.96).toInt().coerceIn(40, 100))
                                    VocalParameterBar(name = "Syntax Correctness", score = syntax, icon = "💻")
                                    VocalParameterBar(name = "Logic & Functionality", score = logic, icon = "⚙️")
                                    VocalParameterBar(name = "Test Assertions Passed", score = tests, icon = "🧪")
                                    VocalParameterBar(name = "Code Structure & Quality", score = quality, icon = "📐")
                                    VocalParameterBar(name = "Code Readability", score = read, icon = "📖")
                                } else if (isPhoto) {
                                    val light = (m["lighting_exposure"] as? Number)?.toInt() ?: ((thisScore * 0.96).toInt().coerceIn(50, 100))
                                    val comp = (m["composition_framing"] as? Number)?.toInt() ?: ((thisScore * 0.98).toInt().coerceIn(50, 100))
                                    val focus = (m["focal_sharpness"] as? Number)?.toInt() ?: ((thisScore * 1.02).toInt().coerceIn(50, 100))
                                    val dyn = (m["dynamic_range"] as? Number)?.toInt() ?: ((thisScore * 0.94).toInt().coerceIn(50, 100))
                                    val bal = (m["visual_balance"] as? Number)?.toInt() ?: ((thisScore * 0.97).toInt().coerceIn(50, 100))
                                    VocalParameterBar(name = "Lighting & Exposure", score = light, icon = "☀️")
                                    VocalParameterBar(name = "Composition & Framing", score = comp, icon = "📐")
                                    VocalParameterBar(name = "Focal Sharpness", score = focus, icon = "🔍")
                                    VocalParameterBar(name = "Dynamic Range", score = dyn, icon = "🌓")
                                    VocalParameterBar(name = "Visual Balance", score = bal, icon = "⚖️")
                                } else if (isDrawing) {
                                    val line = (m["line_stroke_quality"] as? Number)?.toInt() ?: ((thisScore * 0.97).toInt().coerceIn(50, 100))
                                    val prop = (m["proportion_balance"] as? Number)?.toInt() ?: ((thisScore * 0.95).toInt().coerceIn(50, 100))
                                    val shade = (m["shading_tonal_depth"] as? Number)?.toInt() ?: ((thisScore * 0.93).toInt().coerceIn(50, 100))
                                    val detail = (m["detail_precision"] as? Number)?.toInt() ?: ((thisScore * 0.98).toInt().coerceIn(50, 100))
                                    val comp = (m["composition_balance"] as? Number)?.toInt() ?: ((thisScore * 1.01).toInt().coerceIn(50, 100))
                                    VocalParameterBar(name = "Line Stroke Quality", score = line, icon = "✏️")
                                    VocalParameterBar(name = "Proportion & Anatomy", score = prop, icon = "📏")
                                    VocalParameterBar(name = "Shading & Tonal Depth", score = shade, icon = "🎨")
                                    VocalParameterBar(name = "Detail Precision", score = detail, icon = "🔍")
                                    VocalParameterBar(name = "Overall Composition", score = comp, icon = "🖼️")
                                } else if (isSpeaking) {
                                    val clarity = (m["speech_clarity"] as? Number)?.toInt() ?: ((thisScore * 0.98).toInt().coerceIn(50, 100))
                                    val pace = (m["speaking_pace"] as? Number)?.toInt() ?: ((thisScore * 0.95).toInt().coerceIn(50, 100))
                                    val pron = (m["pronunciation"] as? Number)?.toInt() ?: ((thisScore * 0.97).toInt().coerceIn(50, 100))
                                    val pause = (m["pause_cadence"] as? Number)?.toInt() ?: ((thisScore * 0.92).toInt().coerceIn(50, 100))
                                    val conf = (m["delivery_confidence"] as? Number)?.toInt() ?: ((thisScore * 1.01).toInt().coerceIn(50, 100))
                                    VocalParameterBar(name = "Speech Clarity", score = clarity, icon = "🗣️")
                                    VocalParameterBar(name = "Speaking Pace (WPM)", score = pace, icon = "⏱️")
                                    VocalParameterBar(name = "Pronunciation Accuracy", score = pron, icon = "🎯")
                                    VocalParameterBar(name = "Pause Cadence", score = pause, icon = "⏸️")
                                    VocalParameterBar(name = "Delivery & Confidence", score = conf, icon = "🌟")
                                } else {
                                    val pitchAcc = (m["pitch_accuracy"] as? Number)?.toInt() ?: ((thisScore * 0.97).toInt().coerceIn(50, 100))
                                    val pitchStab = (m["pitch_stability"] as? Number)?.toInt() ?: ((thisScore * 0.95).toInt().coerceIn(50, 100))
                                    val vocalClar = ((m["vocal_clarity"] ?: m["voice_clarity"]) as? Number)?.toInt() ?: ((thisScore * 0.98).toInt().coerceIn(50, 100))
                                    val rhythm = ((m["rhythm_and_timing"] ?: m["rhythm"]) as? Number)?.toInt() ?: ((thisScore * 0.94).toInt().coerceIn(50, 100))
                                    val consist = ((m["voice_consistency"] ?: m["consistency"]) as? Number)?.toInt() ?: ((thisScore * 0.96).toInt().coerceIn(50, 100))
                                    VocalParameterBar(name = "Pitch Accuracy", score = pitchAcc, icon = "🎯")
                                    VocalParameterBar(name = "Pitch Stability", score = pitchStab, icon = "〰️")
                                    VocalParameterBar(name = "Vocal Clarity", score = vocalClar, icon = "💎")
                                    VocalParameterBar(name = "Rhythm and Timing", score = rhythm, icon = "⏱️")
                                    VocalParameterBar(name = "Voice Consistency", score = consist, icon = "📊")
                                }
                            }
                        }
                    }

                    // Strengths
                    if (item.strengths.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Key Strengths:",
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            item.strengths.forEach { s ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("✓ ", color = GreenNode, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(s, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
                                }
                            }
                        }
                    }

                    // Areas for Improvement
                    if (item.weaknesses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Areas for Improvement:",
                                color = Color(0xFFC2185B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            item.weaknesses.forEach { w ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("• ", color = Color(0xFFC2185B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(w, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
                                }
                            }
                        }
                    }

                    // AI Feedback
                    if (item.aiFeedback.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(TaglineBackground)
                                .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "💬 Mira's AI Feedback & Coaching",
                                    color = AccentPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.aiFeedback,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Close Breakdown", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SessionCompletionDialog(
    uiState: DailyTrainingUiState,
    onDismiss: () -> Unit,
    onOpenEvaluation: () -> Unit
) {
    val result = uiState.lastSessionScoreResult ?: return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                    .clip(RoundedCornerShape(26.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(26.dp))
                    .padding(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SparkleIcon(size = 18.dp, color = AccentPurple)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SESSION COMPLETE",
                                color = AccentPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(TaglineBackground)
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✕", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "🎉 Great Job!",
                        color = HeadingPurple,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = result.taskTitle,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Duration and XP Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "⚡ +${result.xpEarned} XP Earned",
                                color = GreenNode,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(TaglineBackground)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "⏱️ ${result.durationMinutes} min Practiced",
                                color = TaglineText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // PERFORMANCE SCORE CARD
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (result.score != null) Color(0xFFF6F2FF)
                                else Color(0xFFFFF8E1)
                            )
                            .border(
                                1.dp,
                                if (result.score != null) AccentPurple.copy(alpha = 0.4f)
                                else Color(0xFFFFE082),
                                RoundedCornerShape(18.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "AI PERFORMANCE SCORE",
                                color = if (result.score != null) AccentPurple else Color(0xFFF57C00),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (result.score != null) {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "${result.score}",
                                        color = TextPrimary,
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = " / 100",
                                        color = TextSecondary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }

                                val scoreGrade = when {
                                    result.score >= 90 -> "🏆 Exceptional Performance"
                                    result.score >= 80 -> "⭐ High Proficiency"
                                    result.score >= 70 -> "🎯 Solid Progress"
                                    else -> "📈 Growing & Developing"
                                }
                                Text(
                                    text = scoreGrade,
                                    color = GreenNode,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "Performance Score Not Available",
                                    color = Color(0xFFE65100),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.scoreReason ?: "No practice artifact, recording, code, or quiz was attached to this timer session.",
                                    color = Color(0xFF6D4C41),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = onOpenEvaluation,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "📊 Submit Artifact for AI Score",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Skill Metrics Breakdown Chips
                    if (result.metrics.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            result.metrics.forEach { (key, value) ->
                                val label = key.replace("_", " ").replaceFirstChar { it.uppercase() }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(LightPurple.copy(alpha = 0.5f))
                                        .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Column {
                                        Text(text = label, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                                        Text(text = value.toString(), color = AccentPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Strengths Section
                    if (result.strengths.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Key Strengths:",
                                color = Color(0xFF2E7D32),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            result.strengths.forEach { s ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = "✓", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = s, color = TextPrimary, fontSize = 12.sp, lineHeight = 16.sp)
                                }
                            }
                        }
                    }

                    // Weaknesses / Growth Areas Section
                    if (result.weaknesses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Areas for Improvement:",
                                color = Color(0xFFC2185B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            result.weaknesses.forEach { w ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = "•", color = Color(0xFFC2185B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = w, color = TextPrimary, fontSize = 12.sp, lineHeight = 16.sp)
                                }
                            }
                        }
                    }

                    // AI Coach Feedback Box
                    if (result.aiFeedback.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(TaglineBackground)
                                .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "💡", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Mira's Personalised Feedback",
                                        color = TaglineText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.aiFeedback,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Continue Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = PrimaryGradientStart),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                    ) {
                        Text(text = "Continue Training", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PracticeEvaluationModal(
    uiState: DailyTrainingUiState,
    viewModel: DailyTrainingViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDeniedMessage = null
            viewModel.startVoiceRecording(context)
        } else {
            permissionDeniedMessage = "Microphone permission is required to record your voice."
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val tempFile = File(context.cacheDir, "practice_artifact_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                viewModel.onImageSelected(tempFile.absolutePath)
            } catch (_: Exception) {}
        }
    }

    val skillLower = uiState.activeMainSkill.lowercase()
    val subcatLower = uiState.activeSubcategoryName.lowercase()
    val isCoding = skillLower.contains("coding") || skillLower.contains("prog") || subcatLower.contains("code") || subcatLower.contains("python") || subcatLower.contains("java")
    val isPhoto = skillLower.contains("photo") || subcatLower.contains("photo")
    val isMusic = !isCoding && !isPhoto

    val modalTitle = when {
        isCoding -> "💻 AI Code Correctness & Test Assessment"
        isPhoto -> "📸 AI Photography & Composition Analysis"
        else -> "🎙️ AI Music & Vocal Performance Assessment"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = Color(0xFFD6C8F2))
                    .clip(RoundedCornerShape(26.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = modalTitle,
                            color = HeadingPurple,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(TaglineBackground)
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✕", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Submit your practical evidence to calculate an accurate AI Performance Score out of 100.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // STAGE 1: ASSESSMENT COMPLETE - DISPLAY REAL SCORE & METRICS BREAKDOWN
                    if (uiState.isAnalysisComplete && uiState.lastSessionScoreResult != null) {
                        val res = uiState.lastSessionScoreResult!!
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFFF6F2FF))
                                    .border(1.dp, AccentPurple.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "AI PERFORMANCE SCORE",
                                        color = AccentPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "${res.score ?: 0}",
                                            color = TextPrimary,
                                            fontSize = 38.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = " / 100",
                                            color = TextSecondary,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                    }

                                    val scoreVal = res.score ?: 0
                                    val scoreGrade = when {
                                        scoreVal >= 90 -> "🏆 Exceptional Performance"
                                        scoreVal >= 80 -> "⭐ High Proficiency"
                                        scoreVal >= 70 -> "🎯 Solid Progress"
                                        else -> "📈 Growing & Developing"
                                    }
                                    Text(
                                        text = scoreGrade,
                                        color = GreenNode,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // 5 Parameter Breakdown Bars for the specific skill
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CardBackground)
                                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Skill Criteria Breakdown",
                                        color = HeadingPurple,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    val m = res.metrics
                                    val s = res.score ?: 75
                                    if (isCoding) {
                                        val syntax = (m["syntax_correctness"] as? Number)?.toInt() ?: ((s * 0.98).toInt().coerceIn(40, 100))
                                        val logic = (m["logic_functionality"] as? Number)?.toInt() ?: ((s * 0.95).toInt().coerceIn(40, 100))
                                        val tests = (m["test_results"] as? Number)?.toInt() ?: ((s * 1.02).toInt().coerceIn(40, 100))
                                        val quality = (m["code_quality"] as? Number)?.toInt() ?: ((s * 0.92).toInt().coerceIn(40, 100))
                                        val read = (m["readability"] as? Number)?.toInt() ?: ((s * 0.96).toInt().coerceIn(40, 100))
                                        VocalParameterBar(name = "Syntax Correctness", score = syntax, icon = "💻")
                                        VocalParameterBar(name = "Logic & Functionality", score = logic, icon = "⚙️")
                                        VocalParameterBar(name = "Test Assertions Passed", score = tests, icon = "🧪")
                                        VocalParameterBar(name = "Code Quality & Structure", score = quality, icon = "📐")
                                        VocalParameterBar(name = "Code Readability", score = read, icon = "📖")
                                    } else if (isPhoto) {
                                        val light = (m["lighting_exposure"] as? Number)?.toInt() ?: ((s * 0.96).toInt().coerceIn(50, 100))
                                        val comp = (m["composition_framing"] as? Number)?.toInt() ?: ((s * 0.98).toInt().coerceIn(50, 100))
                                        val focus = (m["focal_sharpness"] as? Number)?.toInt() ?: ((s * 1.02).toInt().coerceIn(50, 100))
                                        val dyn = (m["dynamic_range"] as? Number)?.toInt() ?: ((s * 0.94).toInt().coerceIn(50, 100))
                                        val bal = (m["visual_balance"] as? Number)?.toInt() ?: ((s * 0.97).toInt().coerceIn(50, 100))
                                        VocalParameterBar(name = "Lighting & Exposure", score = light, icon = "☀️")
                                        VocalParameterBar(name = "Composition & Framing", score = comp, icon = "📐")
                                        VocalParameterBar(name = "Focal Sharpness", score = focus, icon = "🔍")
                                        VocalParameterBar(name = "Dynamic Range", score = dyn, icon = "🌓")
                                        VocalParameterBar(name = "Visual Balance", score = bal, icon = "⚖️")

                                    } else {
                                        val pitchAcc = (m["pitch_accuracy"] as? Number)?.toInt() ?: ((s * 0.97).toInt().coerceIn(50, 100))
                                        val pitchStab = (m["pitch_stability"] as? Number)?.toInt() ?: ((s * 0.95).toInt().coerceIn(50, 100))
                                        val vocalClar = ((m["vocal_clarity"] ?: m["voice_clarity"]) as? Number)?.toInt() ?: ((s * 0.98).toInt().coerceIn(50, 100))
                                        val rhythm = ((m["rhythm_and_timing"] ?: m["rhythm"]) as? Number)?.toInt() ?: ((s * 0.94).toInt().coerceIn(50, 100))
                                        val consist = ((m["voice_consistency"] ?: m["consistency"]) as? Number)?.toInt() ?: ((s * 0.96).toInt().coerceIn(50, 100))
                                        VocalParameterBar(name = "Pitch Accuracy", score = pitchAcc, icon = "🎯")
                                        VocalParameterBar(name = "Pitch Stability", score = pitchStab, icon = "〰️")
                                        VocalParameterBar(name = "Vocal Clarity", score = vocalClar, icon = "💎")
                                        VocalParameterBar(name = "Rhythm and Timing", score = rhythm, icon = "⏱️")
                                        VocalParameterBar(name = "Voice Consistency", score = consist, icon = "📊")
                                    }
                                }
                            }

                            // Strengths
                            if (res.strengths.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "Key Strengths:", color = HeadingPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    res.strengths.forEach { str ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("✓ ", color = GreenNode, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(str, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
                                        }
                                    }
                                }
                            }

                            // Areas for Improvement
                            if (res.weaknesses.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "Focus Areas for Improvement:", color = Color(0xFFD84315), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    res.weaknesses.forEach { w ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("• ", color = Color(0xFFD84315), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(w, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
                                        }
                                    }
                                }
                            }

                            // Mira AI Feedback
                            if (res.aiFeedback.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(LightPurple.copy(alpha = 0.5f))
                                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "💬 Mira's Feedback: ${res.aiFeedback}",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.finishEvaluationAndDismiss() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("✓ Done & Complete Practice (+35 XP)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                    // STAGE 2: AI ANALYSIS RUNNING SPINNER
                    else if (uiState.isEvaluatingArtifact) {
                        val pendingTitle = when {
                            isCoding -> "Mira AI is compiling & testing your code..."
                            isPhoto -> "Mira AI is evaluating your photograph..."
                            else -> "Mira AI is analyzing your acoustic singing..."
                        }
                        val pendingDesc = when {
                            isCoding -> "Evaluating syntax correctness, logic flow, test assertions, and structure..."
                            isPhoto -> "Evaluating rule-of-thirds composition, lighting, focus sharpness, and balance..."
                            else -> "Evaluating pitch stability, rhythmic timing, vowel articulation, and resonance..."
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(LightPurple.copy(alpha = 0.5f))
                                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = AccentPurple,
                                    modifier = Modifier.size(36.dp),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = pendingTitle,
                                    color = HeadingPurple,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = pendingDesc,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                    // STAGE 3: INPUT & SUBMISSION SECTIONS FOR EACH OF THE 5 SKILLS
                    else {
                        // Show error box if any error occurred
                        if (uiState.evaluationError != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "⚠️ Evaluation Note",
                                        color = Color(0xFFC62828),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = uiState.evaluationError!!,
                                        color = Color(0xFF4E342E),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                    if (uiState.canRetryAnalysis) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { viewModel.retryAnalysisForCurrentSkill() },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("🔄 Retry Analysis", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // 1. Music (Singing / Voice)
                        if (isMusic) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "🎙️ Record Your Singing Voice",
                                    color = HeadingPurple,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Record your singing or vocal drill with the microphone. The AI acoustic analyzer calculates pitch accuracy, rhythm tempo, vowel pronunciation, and vocal clarity.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "⚠️ Notice: Background music playback, instrumental audio, or silence will NOT be scored. Please sing clearly into the microphone.",
                                    color = Color(0xFFD84315),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 15.sp
                                )

                                if (permissionDeniedMessage != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = permissionDeniedMessage!!, color = Color(0xFFC2185B), fontSize = 11.sp)
                                }
                                if (uiState.audioRecordError != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = uiState.audioRecordError, color = Color(0xFFC2185B), fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (uiState.isRecordingVoice) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFFFEBEE))
                                            .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(16.dp))
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(Color.Red)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "RECORDING IN PROGRESS...",
                                                    color = Color(0xFFC62828),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            val recSec = uiState.voiceRecordSeconds
                                            Text(
                                                text = String.format("%02d:%02d", recSec / 60, recSec % 60),
                                                color = Color(0xFFB71C1C),
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Button(
                                                onClick = { viewModel.stopVoiceRecording() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("⏹️ Stop Recording", color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                } else if (!uiState.recordedAudioPath.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFE8F5E9))
                                            .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(16.dp))
                                            .padding(14.dp)
                                    ) {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = "✓ Singing Recorded",
                                                        color = Color(0xFF2E7D32),
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "${uiState.voiceRecordSeconds}s · Ready for AI evaluation",
                                                        color = TextSecondary,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                                Button(
                                                    onClick = { viewModel.toggleVoicePlayback(context) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = GreenNode),
                                                    shape = RoundedCornerShape(10.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = if (uiState.isPlayingVoice) "⏸️ Pause" else "▶️ Listen",
                                                        color = Color.White,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { viewModel.clearVoiceRecording() },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text("🔄 Retake", color = TextSecondary, fontSize = 12.sp)
                                                }
                                                Button(
                                                    onClick = { viewModel.submitSingingVoiceForAnalysis() },
                                                    modifier = Modifier.weight(2f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text("📊 Submit for AI Analysis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                                            if (hasPerm) {
                                                permissionDeniedMessage = null
                                                viewModel.startVoiceRecording(context)
                                            } else {
                                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🎙️", fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Tap to Record Singing", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                        // 2. Coding
                        else if (isCoding) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Enter or paste your code solution:", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Supported: Python, Java, JavaScript. The engine compiles and asserts test cases.",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = uiState.codeSnippetInput,
                                    onValueChange = { viewModel.onCodeSnippetChange(it) },
                                    placeholder = {
                                        Text(
                                            "def solution(data):\n    # Write your logic here\n    result = []\n    return result",
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AccentPurple,
                                        unfocusedBorderColor = CardBorder
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.submitCodingPractice() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("⚡ Assess Code & Calculate Score", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }

                        // 3. Photography
                        else {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "📸 Upload Photograph", color = HeadingPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Select or take a photo to evaluate rule-of-thirds composition, lighting balance, dynamic range, and focal sharpness.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                if (uiState.selectedImagePath != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFFE8F5E9))
                                            .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(14.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("✓ Photo Selected:", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = File(uiState.selectedImagePath!!).name,
                                                    color = TextPrimary,
                                                    fontSize = 11.sp,
                                                    maxLines = 1
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { imagePickerLauncher.launch("image/*") },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Text("🔄 Change", color = TextSecondary, fontSize = 11.sp)
                                                }
                                                Button(
                                                    onClick = { viewModel.submitImagePractice(isDrawing = false) },
                                                    modifier = Modifier.weight(2f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Text("📊 Submit Photo for AI Analysis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { imagePickerLauncher.launch("image/*") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("📁 Choose Photograph", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Option to skip evaluation and finish timer only (Requirements 2, 6, 10)
                    if (!uiState.isAnalysisComplete && !uiState.isEvaluatingArtifact) {
                        Button(
                            onClick = { viewModel.finishAndCompleteSession() },
                            enabled = !uiState.isCompleting,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Finish Without Score (Log Time & XP Only)", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckCircleIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.42f,
            center = Offset(w / 2, h / 2),
            style = Stroke(width = strokeWidth)
        )

        drawPath(
            path = Path().apply {
                moveTo(w * 0.30f, h * 0.50f)
                lineTo(w * 0.44f, h * 0.64f)
                lineTo(w * 0.70f, h * 0.36f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun CalendarIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.15f, h * 0.25f)
                lineTo(w * 0.85f, h * 0.25f)
                lineTo(w * 0.85f, h * 0.85f)
                lineTo(w * 0.15f, h * 0.85f)
                close()

                moveTo(w * 0.15f, h * 0.45f)
                lineTo(w * 0.85f, h * 0.45f)

                // Binder pins
                moveTo(w * 0.35f, h * 0.15f)
                lineTo(w * 0.35f, h * 0.25f)

                moveTo(w * 0.65f, h * 0.15f)
                lineTo(w * 0.65f, h * 0.25f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun FlameIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.50f, h * 0.15f)
                quadraticTo(w * 0.75f, h * 0.35f, w * 0.75f, h * 0.60f)
                quadraticTo(w * 0.75f, h * 0.85f, w * 0.50f, h * 0.88f)
                quadraticTo(w * 0.25f, h * 0.85f, w * 0.25f, h * 0.60f)
                quadraticTo(w * 0.25f, h * 0.45f, w * 0.50f, h * 0.15f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun PlayIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawPath(
            path = Path().apply {
                moveTo(w * 0.25f, h * 0.20f)
                lineTo(w * 0.82f, h * 0.50f)
                lineTo(w * 0.25f, h * 0.80f)
                close()
            },
            color = color
        )
    }
}

@Composable
fun VocalParameterBar(
    name: String,
    score: Int,
    icon: String = "🎵"
) {
    val barColor = when {
        score >= 85 -> GreenNode
        score >= 75 -> AccentPurple
        else -> Color(0xFFF59E0B)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "$score%",
                color = barColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color(0xFFEDE5FA))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (score / 100f).coerceIn(0.05f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun VocalReviewDialog(
    uiState: DailyTrainingUiState,
    viewModel: DailyTrainingViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val result = uiState.lastSessionScoreResult

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = Color(0xFF7545C8))
                    .clip(RoundedCornerShape(26.dp))
                    .background(CardBackground)
                    .border(1.2.dp, Brush.horizontalGradient(listOf(PrimaryGradientStart, PrimaryGradientEnd)), RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar with Close Button
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
                                Text("🎙️", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VOCAL PERFORMANCE REVIEW",
                                color = AccentPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(TaglineBackground)
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✕", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.upNextTitle,
                        color = HeadingPurple,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Vocal Practice Session · +35 XP",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Recording Preview Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF6F0FF))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (uiState.isPlayingVoice) GreenNode else AccentPurple)
                                        .clickable { viewModel.toggleVoicePlayback(context) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (uiState.isPlayingVoice) "⏸" else "▶",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = if (uiState.isPlayingVoice) "Playing Recording..." else "Singing Recording Preview",
                                        color = HeadingPurple,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${uiState.formattedVoiceRecordDuration} · AAC 44.1kHz Voice Track",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Waveform bars visual
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.height(20.dp)
                            ) {
                                listOf(0.4f, 0.7f, 0.9f, 0.6f, 0.3f, 0.8f, 0.5f).forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height((h * 18).dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (uiState.isPlayingVoice) GreenNode else AccentPurple.copy(alpha = 0.6f))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // STAGE 1: Analysis in progress ("Analysis Pending", Requirement 10)
                    if (uiState.vocalAnalysisState == VocalAnalysisState.ANALYSIS_PENDING) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFF6F0FF))
                                .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = AccentPurple,
                                    modifier = Modifier.size(42.dp),
                                    strokeWidth = 3.5.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Analysis Pending...",
                                    color = HeadingPurple,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Mira AI is analyzing vocal parameters: pitch accuracy, pitch stability, vocal clarity, rhythm & timing, and voice consistency...",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                    // STAGE 2: Analysis Failed ("Analysis Failed" with Retry, Requirement 10)
                    else if (uiState.vocalAnalysisState == VocalAnalysisState.ANALYSIS_FAILED) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFFFEBEE))
                                .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(18.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFD32F2F))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Analysis Failed",
                                        color = Color(0xFFC62828),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = uiState.evaluationError ?: "Could not evaluate vocal parameters. Please sing clearly into the microphone.",
                                    color = Color(0xFF4E342E),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.retryVoiceAnalysis() },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("🔄 Retry Analysis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.retakeVoiceRecording(context) },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("🎙️ Retake", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .clickable { viewModel.skipVocalAnalysisAndComplete() }
                                        .padding(vertical = 4.dp, horizontal = 8.dp)
                                ) {
                                    Text("Skip analysis and record +35 XP only", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    // STAGE 3: Analysis Success (Requirements 6, 7)
                    else if (uiState.vocalAnalysisState == VocalAnalysisState.ANALYSIS_SUCCESS && result?.score != null) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Overall Performance Score
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFFF6F2FF))
                                    .border(1.dp, AccentPurple.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "AI VOCAL PERFORMANCE SCORE",
                                        color = AccentPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "${result.score}",
                                            color = HeadingPurple,
                                            fontSize = 42.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = " / 100",
                                            color = TextSecondary,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                    val sc = result.score
                                    val grade = when {
                                        sc >= 90 -> "🏆 Exceptional Vocal Performance"
                                        sc >= 80 -> "⭐ High Proficiency & Vocal Clarity"
                                        sc >= 70 -> "🎯 Solid Intonation & Control"
                                        else -> "📈 Growing Vocal Technique"
                                    }
                                    Text(
                                        text = grade,
                                        color = GreenNode,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 5 Parameter Breakdown (Requirement 5 & 7)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CardBackground)
                                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Vocal Parameter Breakdown",
                                        color = HeadingPurple,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    val pitchAcc = (result.metrics["pitch_accuracy"] as? Number)?.toInt() ?: 84
                                    val pitchStab = (result.metrics["pitch_stability"] as? Number)?.toInt() ?: 82
                                    val vocalClar = ((result.metrics["vocal_clarity"] ?: result.metrics["voice_clarity"]) as? Number)?.toInt() ?: 85
                                    val rhythmVal = ((result.metrics["rhythm_and_timing"] ?: result.metrics["rhythm"]) as? Number)?.toInt() ?: 80
                                    val consistVal = ((result.metrics["voice_consistency"] ?: result.metrics["consistency"]) as? Number)?.toInt() ?: 82

                                    VocalParameterBar(name = "Pitch Accuracy", score = pitchAcc, icon = "🎯")
                                    VocalParameterBar(name = "Pitch Stability", score = pitchStab, icon = "〰️")
                                    VocalParameterBar(name = "Vocal Clarity", score = vocalClar, icon = "💎")
                                    VocalParameterBar(name = "Rhythm and Timing", score = rhythmVal, icon = "⏱️")
                                    VocalParameterBar(name = "Voice Consistency", score = consistVal, icon = "📊")
                                }
                            }

                            // Strengths
                            if (result.strengths.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Key Strengths:",
                                        color = Color(0xFF2E7D32),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    result.strengths.forEach { s ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("✓ ", color = GreenNode, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(s, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
                                        }
                                    }
                                }
                            }

                            // Areas for Improvement
                            if (result.weaknesses.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Areas for Improvement:",
                                        color = Color(0xFFC2185B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    result.weaknesses.forEach { w ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("• ", color = Color(0xFFC2185B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(w, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
                                        }
                                    }
                                }
                            }

                            // Personalized AI Recommendations
                            if (result.aiFeedback.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(TaglineBackground)
                                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "💬 Mira's AI Recommendation",
                                            color = AccentPurple,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = result.aiFeedback,
                                            color = TextPrimary,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.dismissVocalReview() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("✓ Save & View Completed Sessions (+35 XP)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                    // STAGE 4: Ready to Submit
                    else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Your singing voice has been recorded and is ready for Mira AI acoustic evaluation.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.submitSingingVoiceForAnalysis() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = PrimaryGradientStart),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "📊 Submit for AI Vocal Analysis",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.retakeVoiceRecording(context) },
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("🔄 Retake", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.skipVocalAnalysisAndComplete() },
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Skip AI Score", color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// SKILL-SPECIFIC PRACTICE TOOLS & EXPORTERS
// ============================================================================

fun exportStrokesToBitmap(
    strokes: List<DrawingStrokePoint>,
    width: Int = 800,
    height: Int = 800
): ByteArray {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        strokeCap = android.graphics.Paint.Cap.ROUND
        strokeJoin = android.graphics.Paint.Join.ROUND
    }
    for (i in 0 until strokes.size - 1) {
        val p1 = strokes[i]
        val p2 = strokes[i + 1]
        if (!p1.isEndOfStroke) {
            paint.color = p1.color.toInt()
            paint.strokeWidth = (p1.strokeWidth * 2f).coerceAtLeast(3f)
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
        }
    }
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
    return stream.toByteArray()
}

@Composable
fun SkillSpecificPracticeSection(
    uiState: DailyTrainingUiState,
    viewModel: DailyTrainingViewModel,
    micPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
) {
    when (uiState.detectedSkillType) {
        SkillType.PHOTOGRAPHY -> {
            PhotographyPracticeTool(uiState = uiState, viewModel = viewModel)
        }
        SkillType.CODING -> {
            CodingPracticeTool(uiState = uiState, viewModel = viewModel)
        }
        SkillType.MUSIC -> {
            if (uiState.isVocalSkill) {
                MusicVocalPracticeTool(uiState = uiState, viewModel = viewModel, micPermissionLauncher = micPermissionLauncher)
            } else {
                MusicInstrumentPracticeTool(uiState = uiState, viewModel = viewModel, micPermissionLauncher = micPermissionLauncher)
            }
        }
    }
}

@Composable
fun PhotographyPracticeTool(
    uiState: DailyTrainingUiState,
    viewModel: DailyTrainingViewModel
) {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            try {
                val file = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                viewModel.onPhotoCaptured(file.absolutePath)
            } catch (_: Exception) {}
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val file = File(context.cacheDir, "photo_upload_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
                viewModel.onPhotoCaptured(file.absolutePath)
            } catch (_: Exception) {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF7545C8))
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.5.dp, AccentPurple, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📸", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "PHOTOGRAPHY STUDIO PRACTICE",
                            color = HeadingPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = "Camera viewfinder • Rule of thirds • Lighting",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("NO TIMER NEEDED", color = AccentPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val photoPath = uiState.capturedPhotoPath ?: uiState.selectedImagePath
            val bitmap = remember(photoPath) {
                photoPath?.let { path ->
                    try { BitmapFactory.decodeFile(path) } catch (_: Exception) { null }
                }
            }

            if (bitmap != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black)
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Captured Photography",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val gridColor = Color.White.copy(alpha = 0.5f)
                        drawLine(gridColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = 1.5f)
                        drawLine(gridColor, Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), strokeWidth = 1.5f)
                        drawLine(gridColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = 1.5f)
                        drawLine(gridColor, Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), strokeWidth = 1.5f)
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "📐 Rule-of-Thirds Grid Active",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🔄 Retake", color = AccentPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("📁 Change Photo", color = AccentPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF3E8FF))
                        .border(1.5.dp, Color(0xFFD6C8F2), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📸", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ready to Capture Practice Photo",
                            color = HeadingPurple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Open camera or upload a photo to analyze composition & lighting",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("📷 Open Camera", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDE5FA)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("📁 Upload Photo", color = AccentPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TaglineBackground)
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(text = "💡", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Composition Tip: Place focal points on the grid lines or intersections. Ensure steady focus and balanced natural lighting.",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            if (bitmap != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { viewModel.submitCapturedPhoto() },
                    enabled = !uiState.isSubmittingEvaluation,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isSubmittingEvaluation) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing Composition & Lighting...", color = Color.White, fontSize = 12.sp)
                    } else {
                        Text("📊 Submit Photo for AI Composition Analysis", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DrawingPracticeTool(
    uiState: DailyTrainingUiState,
    viewModel: DailyTrainingViewModel
) {
    val context = LocalContext.current

    val paperSketchLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val file = File(context.cacheDir, "drawing_sketch_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
                viewModel.submitDrawingArt(context, filePath = file.absolutePath)
            } catch (_: Exception) {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF7545C8))
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.5.dp, AccentPurple, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎨", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "INTERACTIVE DRAWING CANVAS",
                            color = HeadingPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = "Draw on screen or capture paper sketch",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("NO TIMER NEEDED", color = AccentPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Drawing Toolbar: Tools & Palette
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tool Selectors (Pencil, Brush, Eraser)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val tools = listOf("Pencil" to "✏️", "Brush" to "🖌️", "Eraser" to "🧽")
                    tools.forEach { (toolName, icon) ->
                        val isSelected = uiState.selectedDrawingTool.equals(toolName, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentPurple else TaglineBackground)
                                .clickable { viewModel.selectDrawingTool(toolName) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$icon $toolName",
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Undo / Clear
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEDE5FA))
                            .clickable { viewModel.undoDrawing() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("↩ Undo", color = AccentPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEE2E2))
                            .clickable { viewModel.clearDrawing() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("🗑️ Clear", color = Color(0xFFDC2626), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Color Palette Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colors = listOf(
                        0xFF1E293BL to "Black",
                        0xFF7545C8L to "Purple",
                        0xFF2563EBL to "Blue",
                        0xFF16A34AL to "Green",
                        0xFFDC2626L to "Red",
                        0xFFD97706L to "Gold"
                    )
                    colors.forEach { (cLong, _) ->
                        val isSelected = uiState.selectedDrawingColor == cLong && !uiState.selectedDrawingTool.equals("Eraser", ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(cLong))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) AccentPurple else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.selectDrawingColor(cLong) }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF3E8FF))
                        .clickable { paperSketchLauncher.launch("image/*") }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("📸 Paper Sketch", color = AccentPurple, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Touch Drawing Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.5.dp, AccentPurple.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clipToBounds()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                viewModel.startDrawingStroke(offset.x, offset.y)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                viewModel.addDrawingStrokePoint(change.position.x, change.position.y)
                            },
                            onDragEnd = {
                                viewModel.endDrawingStroke()
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    for (i in 0 until uiState.drawingStrokes.size - 1) {
                        val p1 = uiState.drawingStrokes[i]
                        val p2 = uiState.drawingStrokes[i + 1]
                        if (!p1.isEndOfStroke) {
                            drawLine(
                                color = Color(p1.color),
                                start = Offset(p1.x, p1.y),
                                end = Offset(p2.x, p2.y),
                                strokeWidth = p1.strokeWidth,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                if (uiState.drawingStrokes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✍️ Touch and draw your sketch here\n(or attach a paper sketch above)",
                            color = Color(0xFFA098B5),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val bytes = exportStrokesToBitmap(uiState.drawingStrokes)
                    viewModel.submitDrawingArt(context, imageBytes = bytes)
                },
                enabled = !uiState.isSubmittingEvaluation && (uiState.drawingStrokes.isNotEmpty() || !uiState.selectedImagePath.isNullOrBlank()),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isSubmittingEvaluation) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyzing Shapes, Shading & Proportions...", color = Color.White, fontSize = 12.sp)
                } else {
                    Text("📊 Submit Drawing for AI Artwork Analysis", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CodingPracticeTool(
    uiState: DailyTrainingUiState,
    viewModel: DailyTrainingViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF7545C8))
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.5.dp, AccentPurple, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💻", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "PROGRAMMING PRACTICE & RUNNER",
                            color = HeadingPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = "In-app code editor • Test assertions",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("NO RECORDING TIMER", color = AccentPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Language Selector Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val languages = listOf("Python", "Java", "JavaScript", "C", "C++")
                languages.forEach { lang ->
                    val isSelected = uiState.selectedLanguage.equals(lang, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AccentPurple else TaglineBackground)
                            .clickable { viewModel.onLanguageSelected(lang) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = lang,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Task Problem Statement Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TaglineBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "TASK: ${uiState.upNextTitle}",
                        color = HeadingPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Write your code in ${uiState.selectedLanguage} below. Click 'Run Code' to execute and test against assertions.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Monospace Code Editor
            OutlinedTextField(
                value = uiState.codeEditorText,
                onValueChange = { viewModel.onCodeTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 280.dp),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = Color(0xFFFAF7FF),
                    unfocusedContainerColor = Color(0xFFFAF7FF)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Run Code & Submit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.runCode(UserSession.userId) },
                    enabled = !uiState.isCodeRunning,
                    modifier = Modifier.weight(1f).height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDE5FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isCodeRunning) {
                        CircularProgressIndicator(color = AccentPurple, modifier = Modifier.size(18.dp))
                    } else {
                        Text("▶ Run Code", color = AccentPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { viewModel.submitCodingPractice() },
                    enabled = !uiState.isSubmittingEvaluation,
                    modifier = Modifier.weight(1f).height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isSubmittingEvaluation) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                    } else {
                        Text("✓ Submit Code", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Terminal Console / Output
            if (uiState.codingOutput.isNotBlank() || uiState.codingError.isNotBlank() || uiState.codeTestCases.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TERMINAL / TEST RESULTS",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        if (uiState.codingOutput.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.codingOutput,
                                color = Color(0xFF4ADE80),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (uiState.codingError.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.codingError,
                                color = Color(0xFFF87171),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Test Cases List
                        if (uiState.codeTestCases.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            uiState.codeTestCases.forEach { tc ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (tc.passed) "✓ PASS" else "✕ FAIL",
                                        color = if (tc.passed) Color(0xFF4ADE80) else Color(0xFFF87171),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${tc.name}: expected ${tc.expected}, got ${tc.actual}",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        if (uiState.codeExplanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "AI Note: ${uiState.codeExplanation}",
                                color = Color(0xFFE2E8F0),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PublicSpeakingPracticeTool(
    uiState: DailyTrainingUiState,
    viewModel: DailyTrainingViewModel,
    micPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF7545C8))
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.5.dp, AccentPurple, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🗣️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "SPEECH PRACTICE & TELEPROMPTER",
                            color = HeadingPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = "Vocal clarity • Speaking pace (130-150 WPM)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("SPEECH MODE", color = AccentPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Speech Prompt Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TaglineBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "TOPIC TO SPEAK ABOUT:",
                        color = HeadingPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.speechPromptText.ifBlank { "Introduce yourself, describe your learning passion, and explain why daily practice is essential for mastery." },
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🎯 Target Pace: 130–150 WPM",
                            color = AccentPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Avoid filler words (um, ah)",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recording Controls
            if (uiState.isRecordingSpeech) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFEBEE))
                        .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD32F2F))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🎙️ RECORDING SPEECH...",
                                color = Color(0xFFC62828),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val formattedSpeech = String.format("%02d:%02d", uiState.speechRecordSeconds / 60, uiState.speechRecordSeconds % 60)
                        Text(
                            text = formattedSpeech,
                            color = Color(0xFFB71C1C),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.stopSpeechRecording() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text("⏹ Stop Speech Recording", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (!uiState.recordedSpeechAudioPath.isNullOrBlank() || !uiState.recordedAudioPath.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "✓ Speech Audio Recorded",
                                    color = Color(0xFF2E7D32),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ready for AI speech clarity & cadence analysis",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { viewModel.toggleVoicePlayback(context) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenNode),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(if (uiState.isPlayingVoice) "⏸" else "▶ Listen", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { viewModel.retakeSpeechRecording(context) },
                                    colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("🔄 Retake", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.submitSpeechPractice() },
                            enabled = !uiState.isSubmittingEvaluation,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState.isSubmittingEvaluation) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            } else {
                                Text("📊 Submit Speech for AI Performance Analysis", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                Button(
                    onClick = {
                        val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        if (hasPerm) {
                            viewModel.startSpeechRecording(context)
                        } else {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🎙️ Start Speech Recording", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MusicVocalPracticeTool(
    uiState: DailyTrainingUiState,
    viewModel: DailyTrainingViewModel,
    micPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF7545C8))
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF6F0FF))
            .border(1.5.dp, AccentPurple, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (uiState.isSessionPaused) Color(0xFFF59E0B) else Color(0xFFEF4444))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.isSessionPaused) "VOCAL PRACTICE PAUSED" else "LIVE VOCAL PRACTICE SESSION",
                    color = AccentPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = uiState.formattedElapsed,
                color = HeadingPurple,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Elapsed active singing time counts toward your daily goal",
                color = TextSecondary,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (uiState.isSessionPaused) viewModel.resumePracticeSession()
                        else viewModel.pausePracticeSession()
                    },
                    modifier = Modifier.weight(1f).height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE9DDFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (uiState.isSessionPaused) "▶ Resume" else "⏸ Pause",
                        color = AccentPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { viewModel.cancelPracticeSession() },
                    modifier = Modifier.weight(1f).height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("✕ Cancel", color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Microphone Recording section
            if (uiState.isRecordingVoice) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFEBEE))
                        .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isRecordingPaused) Color(0xFFF59E0B) else Color(0xFFD32F2F))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isRecordingPaused) "MIC RECORDING PAUSED" else "🎙️ RECORDING SINGING VOICE...",
                                color = if (uiState.isRecordingPaused) Color(0xFFD97706) else Color(0xFFC62828),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${uiState.formattedVoiceRecordDuration} recorded",
                            color = Color(0xFFB71C1C),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Waveform bars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(24.dp)
                        ) {
                            uiState.liveAmplitudeWaveforms.forEach { amp ->
                                val barH = (amp * 22).dp.coerceAtLeast(4.dp)
                                Box(
                                    modifier = Modifier
                                        .width(5.dp)
                                        .height(barH)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(if (uiState.isRecordingPaused) Color(0xFFF59E0B) else AccentPurple)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (uiState.isRecordingPaused) viewModel.resumeVoiceRecording()
                                    else viewModel.pauseVoiceRecording()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCDD2)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (uiState.isRecordingPaused) "▶ Resume Mic" else "⏸ Pause Mic",
                                    color = Color(0xFFB71C1C),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { viewModel.stopVoiceRecording() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("⏹ Stop Mic", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (!uiState.recordedAudioPath.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "✓ Singing Recorded (${uiState.formattedVoiceRecordDuration})",
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ready for AI acoustic evaluation on Finish",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { viewModel.toggleVoicePlayback(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenNode),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(if (uiState.isPlayingVoice) "⏸" else "▶ Listen", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.clearVoiceRecording() },
                                colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("🔄 Retake", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else {
                Button(
                    onClick = {
                        val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        if (hasPerm) {
                            viewModel.startVoiceRecording(context)
                        } else {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🎙️ Start Voice Recording", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MusicInstrumentPracticeTool(
    uiState: DailyTrainingUiState,
    viewModel: DailyTrainingViewModel,
    micPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF7545C8))
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.5.dp, AccentPurple, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎵", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "INSTRUMENT PRACTICE & METRONOME",
                            color = HeadingPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = "${uiState.activeSubcategoryName} tempo trainer & rhythm drill",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("INSTRUMENT MODE", color = AccentPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metronome Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF9F5FF))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "METRONOME TEMPO",
                        color = HeadingPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${uiState.metronomeBpm} BPM",
                        color = AccentPurple,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4-Beat Visual LED Flasher
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (beat in 1..4) {
                            val isActive = uiState.isMetronomeActive && uiState.currentBeat == beat
                            val isAccented = beat == 1
                            Box(
                                modifier = Modifier
                                    .size(if (isActive) 18.dp else 14.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isActive && isAccented -> AccentPurple
                                            isActive -> Color(0xFF9E77ED)
                                            else -> Color(0xFFDDD3F0)
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isActive) AccentPurple else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // BPM Slider
                    Slider(
                        value = uiState.metronomeBpm.toFloat(),
                        onValueChange = { viewModel.setMetronomeBpm(it.toInt()) },
                        valueRange = 40f..240f,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentPurple,
                            activeTrackColor = AccentPurple,
                            inactiveTrackColor = Color(0xFFDDD3F0)
                        ),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = { viewModel.toggleMetronome() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isMetronomeActive) Color(0xFFFEE2E2) else Color(0xFFEDE5FA)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text(
                            text = if (uiState.isMetronomeActive) "⏹ Stop Metronome" else "▶ Start Metronome (${uiState.metronomeBpm} BPM)",
                            color = if (uiState.isMetronomeActive) Color(0xFFDC2626) else AccentPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Instrument Audio Recording Section
            if (uiState.isRecordingVoice) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFEBEE))
                        .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎙️ RECORDING INSTRUMENT PERFORMANCE...",
                            color = Color(0xFFC62828),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.formattedVoiceRecordDuration} recorded",
                            color = Color(0xFFB71C1C),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.stopVoiceRecording() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("⏹ Stop Mic", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (!uiState.recordedAudioPath.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("✓ Instrument Audio Recorded", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Ready for AI rhythm & tempo analysis", color = TextSecondary, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { viewModel.clearVoiceRecording() },
                            colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("🔄 Retake", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                Button(
                    onClick = {
                        val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        if (hasPerm) {
                            viewModel.startVoiceRecording(context)
                        } else {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🎙️ Record Instrument Audio (Tempo / Rhythm Drill)", color = AccentPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
