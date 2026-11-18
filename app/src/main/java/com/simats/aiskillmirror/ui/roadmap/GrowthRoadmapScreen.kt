package com.simats.aiskillmirror.ui.roadmap

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.simats.aiskillmirror.data.network.RoadmapStageData
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun GrowthRoadmapScreen(
    viewModel: GrowthRoadmapViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onContinueActiveWeekClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is GrowthRoadmapUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is GrowthRoadmapUiEvent.StartPracticeStage -> {
                    Toast.makeText(context, "Opening practice for ${event.stage.topicName}", Toast.LENGTH_SHORT).show()
                    onContinueActiveWeekClick()
                }
                GrowthRoadmapUiEvent.ContinueActiveWeek -> {
                    onContinueActiveWeekClick()
                }
                GrowthRoadmapUiEvent.OpenFilterSettings -> {
                    onFilterClick()
                }
                GrowthRoadmapUiEvent.NavigateBack -> {
                    onBackClick()
                }
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
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .clickable {
                                    viewModel.onBackClick()
                                    onBackClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            BackArrowIcon(size = 20.dp, color = HeadingPurple)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Skill Roadmap",
                                color = HeadingPurple,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "PERSONALIZED AI PROGRESSION",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Level Badge
                    val currentLevel = uiState.activeSubcategory?.proficiencyLevel ?: "Beginner"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(TaglineBackground)
                            .border(1.dp, CardBorder, RoundedCornerShape(100.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentLevel.uppercase(),
                            color = HeadingPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Available Subcategories Chip Selector
                if (uiState.availableSubcategories.isNotEmpty()) {
                    val chipScroll = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(chipScroll),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.availableSubcategories.forEach { sub ->
                            val isSelected = sub.subcategoryId == uiState.activeSubcategory?.subcategoryId
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(if (isSelected) HeadingPurple else Color.White)
                                    .border(
                                        1.dp,
                                        if (isSelected) HeadingPurple else CardBorder,
                                        RoundedCornerShape(100.dp)
                                    )
                                    .clickable {
                                        viewModel.switchSubcategory(sub.subcategoryId)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = sub.subcategoryName,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Hero Plan Progress Card
                val subName = uiState.activeSubcategory?.subcategoryName ?: "Skill"
                val goal = uiState.activeSubcategory?.learningGoal ?: "Master Fundamentals"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(22.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(TaglineBackground)
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${uiState.completedStagesCount} OF ${uiState.totalStagesCount} STAGES CLEARED",
                                    color = TaglineText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${uiState.roadmapProgressPercentage}%",
                                    color = HeadingPurple,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                SparkleIcon(size = 18.dp, color = HeadingPurple)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "$subName Mastery Roadmap",
                            color = HeadingPurple,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Goal: $goal · ${uiState.totalPracticeMinutes} min practiced",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Real Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFEDE5FA))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(uiState.roadmapProgressFraction.coerceIn(0.02f, 1.0f))
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(HeadingPurple, GreenNode)
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.roadmapProgressText,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Next: Stage ${uiState.activeStage?.stageOrder ?: 1}",
                                color = HeadingPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AI Feedback & Guidance Section
                val aiFb = uiState.aiFeedback
                if (aiFb != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFD6C8F2))
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White)
                            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    SparkleIcon(size = 18.dp, color = HeadingPurple)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AI Personalized Insights",
                                        color = HeadingPurple,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(TaglineBackground)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        color = HeadingPurple,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = aiFb.coachAdvice,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            // Remedial Alert if struggled
                            if (aiFb.hasRemedialAlert && aiFb.remedialFeedback.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFFF4E5))
                                        .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text(text = "⚠️", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Remedial Coaching Recommended",
                                                color = Color(0xFFB76E00),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = aiFb.remedialFeedback,
                                                color = Color(0xFF6D4500),
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Strengths & Weak Areas Chips
                            if (aiFb.strengths.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "STRENGTH: ",
                                        color = GreenNode,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = aiFb.strengths.firstOrNull() ?: "",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Vertical Learning Timeline Section
                Text(
                    text = "Structured Learning Stages",
                    color = HeadingPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Complete stage practice and pass the assessment to unlock next levels",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Timeline Stages List
                if (uiState.stages.isEmpty() && uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = HeadingPurple)
                    }
                } else {
                    uiState.stages.forEachIndexed { index, stage ->
                        TimelineStageItemView(
                            stage = stage,
                            isLast = index == uiState.stages.size - 1,
                            onToggleTask = { taskId, isDone ->
                                viewModel.toggleTask(stage.id, taskId, isDone)
                            },
                            onStartPractice = {
                                viewModel.startStagePractice(stage)
                            },
                            onTakeAssessment = {
                                viewModel.openStageQuiz(stage)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Next Goal Milestone Card
                if (uiState.aiFeedback?.nextGoal?.isNotBlank() == true) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF3EDFD))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(HeadingPurple),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🎯", fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Next Target Milestone",
                                    color = HeadingPurple,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.aiFeedback?.nextGoal ?: "",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // Primary Start/Continue Learning Button
                val activeStage = uiState.activeStage
                val isInitialStart = uiState.completedStagesCount == 0
                val buttonLabel = if (isInitialStart) {
                    "Start Learning (${activeStage?.topicName ?: "Stage 1"})"
                } else if (activeStage != null && !activeStage.isCompleted) {
                    "Continue Learning (${activeStage.topicName})"
                } else {
                    "Continue Learning"
                }

                Button(
                    onClick = {
                        viewModel.onContinueActiveWeekClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(10.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGradientStart),
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
                            Canvas(modifier = Modifier.size(16.dp)) {
                                val strokeWidth = 2.dp.toPx()
                                drawPath(
                                    path = Path().apply {
                                        moveTo(2.dp.toPx(), 8.dp.toPx())
                                        lineTo(14.dp.toPx(), 8.dp.toPx())
                                        moveTo(9.dp.toPx(), 3.dp.toPx())
                                        lineTo(14.dp.toPx(), 8.dp.toPx())
                                        lineTo(9.dp.toPx(), 13.dp.toPx())
                                    },
                                    color = Color.White,
                                    style = Stroke(width = strokeWidth)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = buttonLabel,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.LEARN,
                onTabSelected = onTabSelected
            )
        }

        // Stage Assessment Quiz Dialog
        if (uiState.isQuizDialogOpen && uiState.quizStage != null) {
            StageQuizDialog(
                stage = uiState.quizStage!!,
                currentQuestionIndex = uiState.currentQuestionIndex,
                isReviewMode = uiState.isReviewMode,
                selectedAnswers = uiState.quizAnswers,
                result = uiState.quizResult,
                isSubmitting = uiState.isSubmittingQuiz,
                errorMessage = uiState.quizError,
                onSelectOption = { qIdx, optIdx ->
                    viewModel.selectQuizAnswer(qIdx, optIdx)
                },
                onNextQuestion = {
                    viewModel.nextQuestion()
                },
                onPreviousQuestion = {
                    viewModel.previousQuestion()
                },
                onJumpToQuestion = { idx ->
                    viewModel.jumpToQuestion(idx)
                },
                onSubmit = {
                    viewModel.submitStageQuiz()
                },
                onOpenReview = {
                    viewModel.openReviewMode()
                },
                onCloseReview = {
                    viewModel.closeReviewMode()
                },
                onRetake = {
                    viewModel.retakeQuiz()
                },
                onDismiss = {
                    viewModel.closeQuizDialog()
                }
            )
        }
    }
}

@Composable
fun TimelineStageItemView(
    stage: RoadmapStageData,
    isLast: Boolean,
    onToggleTask: (String, Boolean) -> Unit,
    onStartPractice: () -> Unit,
    onTakeAssessment: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(stage.isUnlocked && !stage.isCompleted) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Left Column: Node Circle & Vertical Connecting Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            // Stage Indicator Node Circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            stage.isCompleted -> GreenNode
                            stage.isUnlocked -> HeadingPurple
                            else -> Color(0xFFD1C4E9)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    stage.isCompleted -> {
                        // Checkmark
                        Canvas(modifier = Modifier.size(14.dp)) {
                            val path = Path().apply {
                                moveTo(size.width * 0.2f, size.height * 0.5f)
                                lineTo(size.width * 0.45f, size.height * 0.75f)
                                lineTo(size.width * 0.85f, size.height * 0.25f)
                            }
                            drawPath(
                                path = path,
                                color = Color.White,
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                    stage.isUnlocked -> {
                        // Number
                        Text(
                            text = "${stage.stageOrder}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    else -> {
                        // Lock Icon
                        Canvas(modifier = Modifier.size(12.dp)) {
                            val w = size.width
                            val h = size.height
                            // Body
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(w * 0.15f, h * 0.40f),
                                size = androidx.compose.ui.geometry.Size(w * 0.70f, h * 0.60f)
                            )
                            // Shackle
                            drawPath(
                                path = Path().apply {
                                    moveTo(w * 0.30f, h * 0.40f)
                                    lineTo(w * 0.30f, h * 0.20f)
                                    quadraticTo(w * 0.50f, 0f, w * 0.70f, h * 0.20f)
                                    lineTo(w * 0.70f, h * 0.40f)
                                },
                                color = Color.White,
                                style = Stroke(width = 1.6.dp.toPx())
                            )
                        }
                    }
                }
            }

            // Vertical Connecting Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.5.dp)
                        .height(if (isExpanded) 190.dp else 75.dp)
                        .background(
                            if (stage.isCompleted) GreenNode else Color(0xFFE0D7F5)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Right Column: Stage Card
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 12.dp)
                .shadow(
                    elevation = if (stage.isUnlocked) 3.dp else 1.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = Color(0xFFD6C8F2)
                )
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .border(
                    width = if (stage.isUnlocked && !stage.isCompleted) 1.5.dp else 1.dp,
                    color = if (stage.isUnlocked && !stage.isCompleted) HeadingPurple else CardBorder,
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable { isExpanded = !isExpanded }
                .padding(14.dp)
        ) {
            Column {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(
                                when {
                                    stage.isCompleted -> Color(0xFFE8F5E9)
                                    stage.isUnlocked -> TaglineBackground
                                    else -> Color(0xFFF1F1F1)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${stage.stageLevel.uppercase()} · ${stage.estimatedDuration}",
                            color = when {
                                stage.isCompleted -> Color(0xFF2E7D32)
                                stage.isUnlocked -> HeadingPurple
                                else -> TextSecondary
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Status Pill
                    Text(
                        text = when {
                            stage.isCompleted -> "CLEARED (${stage.scoreAchieved ?: 100}%)"
                            stage.isUnlocked -> "CURRENT"
                            else -> "LOCKED"
                        },
                        color = when {
                            stage.isCompleted -> GreenNode
                            stage.isUnlocked -> HeadingPurple
                            else -> TextSecondary
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stage.topicName,
                    color = if (stage.isUnlocked) HeadingPurple else TextSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stage.topicDescription,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                // Expanded Section: Objectives, Instructions, Actions
                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        HorizontalDivider(color = Color(0xFFEDE5FA), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Learning Objectives
                        if (stage.learningObjectives.isNotEmpty()) {
                            Text(
                                text = "Learning Objectives:",
                                color = HeadingPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            stage.learningObjectives.forEach { obj ->
                                Row(
                                    modifier = Modifier.padding(vertical = 1.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = "• ", color = HeadingPurple, fontSize = 12.sp)
                                    Text(text = obj, color = TextPrimary, fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Practice Instructions
                        if (stage.practiceInstructions.isNotBlank()) {
                            Text(
                                text = "Practice Instructions:",
                                color = HeadingPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stage.practiceInstructions,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Practice Tasks
                        if (stage.practiceTasks.isNotEmpty()) {
                            val doneCount = stage.practiceTasks.count { it.isDone }
                            Text(
                                text = "Practice Tasks ($doneCount/${stage.practiceTasks.size} Completed):",
                                color = HeadingPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            stage.practiceTasks.forEach { task ->
                                val isRemedial = task.id.startsWith("remedial_")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when {
                                                isRemedial -> Color(0xFFFFF8E1)
                                                task.isDone -> Color(0xFFE8F5E9)
                                                else -> Color(0xFFF9F7FD)
                                            }
                                        )
                                        .border(
                                            0.8.dp,
                                            when {
                                                isRemedial -> Color(0xFFFFB74D)
                                                task.isDone -> GreenNode
                                                else -> CardBorder
                                            },
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable(enabled = stage.isUnlocked) {
                                            onToggleTask(task.id, !task.isDone)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(if (task.isDone) GreenNode else Color.White)
                                            .border(1.dp, if (task.isDone) GreenNode else CardBorder, RoundedCornerShape(5.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (task.isDone) {
                                            Text(
                                                text = "✓",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            color = if (task.isDone) Color(0xFF2E7D32) else TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = if (isRemedial) FontWeight.Bold else FontWeight.Medium
                                        )
                                        if (isRemedial) {
                                            Text(
                                                text = "⚠️ Additional AI Remedial Practice",
                                                color = Color(0xFFB76E00),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Remedial Notice if flagged
                        if (stage.needsRemedial && stage.remedialFeedback != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = stage.remedialFeedback,
                                    color = Color(0xFF8D4F00),
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Action Buttons (Only if Unlocked)
                        if (stage.isUnlocked) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onStartPractice,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground)
                                ) {
                                    Text(
                                        text = "Practice Drills",
                                        color = HeadingPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Button(
                                    onClick = onTakeAssessment,
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HeadingPurple)
                                ) {
                                    Text(
                                        text = if (stage.isCompleted) "Retake Quiz" else "Take Quiz",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "🔒 Complete preceding stages with 70%+ score to unlock.",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StageQuizDialog(
    stage: RoadmapStageData,
    currentQuestionIndex: Int,
    isReviewMode: Boolean,
    selectedAnswers: Map<Int, Int>,
    result: com.simats.aiskillmirror.data.network.CompleteTopicResultData?,
    isSubmitting: Boolean,
    errorMessage: String?,
    onSelectOption: (Int, Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPreviousQuestion: () -> Unit,
    onJumpToQuestion: (Int) -> Unit,
    onSubmit: () -> Unit,
    onOpenReview: () -> Unit,
    onCloseReview: () -> Unit,
    onRetake: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isReviewMode) "Assessment Review" else "Stage Assessment Quiz",
                            color = HeadingPurple,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stage.topicName,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE5FA))
                            .clickable {
                                if (isReviewMode) {
                                    onCloseReview()
                                } else {
                                    onDismiss()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", color = HeadingPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // =================================================================
                // 1. REVIEW MODE: Display all questions with explanations & answers
                // =================================================================
                if (isReviewMode) {
                    val reviewList = result?.reviewQuestions ?: emptyList()
                    val total = if (reviewList.isNotEmpty()) reviewList.size else stage.assessmentQuestions.size
                    val correct = result?.correctAnswers ?: 0
                    val score = result?.score ?: 0

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3EDFD))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Score: $score% ($correct of $total Correct)",
                                    color = HeadingPurple,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (score >= 70) "Passing Score Achieved (70% required)" else "Below 70% threshold · Review explanations below",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            TextButton(onClick = onCloseReview) {
                                Text("← Summary", color = HeadingPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (reviewList.isNotEmpty()) {
                        reviewList.forEachIndexed { qIdx, item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFFCFBFE))
                                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Question ${qIdx + 1} of $total",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (item.isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (item.isCorrect) "✓ Correct" else "✗ Incorrect",
                                            color = if (item.isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.question,
                                    color = HeadingPurple,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                item.options.forEachIndexed { optIdx, optText ->
                                    val isCorrectChoice = optIdx == item.correctChoice
                                    val isUserChoice = optIdx == item.userChoice

                                    val bgColor = when {
                                        isCorrectChoice -> Color(0xFFE8F8F0)
                                        isUserChoice && !item.isCorrect -> Color(0xFFFFEBEE)
                                        else -> Color(0xFFF9F7FD)
                                    }

                                    val borderColor = when {
                                        isCorrectChoice -> Color(0xFF2E7D32)
                                        isUserChoice && !item.isCorrect -> Color(0xFFEF5350)
                                        else -> CardBorder
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(bgColor)
                                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${('A'.code + optIdx).toChar()}. $optText",
                                            color = when {
                                                isCorrectChoice -> Color(0xFF1B5E20)
                                                isUserChoice && !item.isCorrect -> Color(0xFFB71C1C)
                                                else -> TextPrimary
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = if (isCorrectChoice || isUserChoice) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (isCorrectChoice) {
                                            Text("✓ Correct", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        } else if (isUserChoice) {
                                            Text("Your Choice", color = Color(0xFFC62828), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (item.explanation.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFEDE5FA))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "💡 Explanation: ${item.explanation}",
                                            color = HeadingPurple,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        stage.assessmentQuestions.forEachIndexed { qIdx, question ->
                            val userChoice = selectedAnswers[qIdx]
                            val isCorrect = userChoice != null && userChoice == question.correct

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFBF9FE))
                                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Q${qIdx + 1}. ${question.question}",
                                    color = HeadingPurple,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                question.options.forEachIndexed { optIdx, optText ->
                                    val isCorrectChoice = optIdx == question.correct
                                    val isUserChoice = optIdx == userChoice

                                    val bg = when {
                                        isCorrectChoice -> Color(0xFFE8F8F0)
                                        isUserChoice && !isCorrect -> Color(0xFFFFEBEE)
                                        else -> Color(0xFFF9F7FD)
                                    }
                                    Text(
                                        text = "${('A'.code + optIdx).toChar()}. $optText" +
                                                (if (isCorrectChoice) "  [✓ Correct Answer]" else "") +
                                                (if (isUserChoice && !isCorrectChoice) "  [✗ Your Choice]" else ""),
                                        color = if (isCorrectChoice) Color(0xFF2E7D32) else if (isUserChoice) Color(0xFFC62828) else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isCorrectChoice || isUserChoice) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                                if (question.explanation.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "💡 ${question.explanation}",
                                        color = HeadingPurple,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onCloseReview,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HeadingPurple)
                    ) {
                        Text(text = "← Back to Result Summary", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                // =================================================================
                // 2. RESULT SUMMARY: Displays score, pass/fail, and action buttons
                // =================================================================
                } else if (result != null) {
                    val passed = result.passed
                    val totalQ = if (result.totalQuestions > 0) result.totalQuestions else stage.assessmentQuestions.size
                    val correctCount = result.correctAnswers

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (passed) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                            .border(1.dp, if (passed) GreenNode else Color(0xFFFFB74D), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (passed) "🎉 Stage Cleared!" else "⚠️ Practice Recommended",
                                color = if (passed) Color(0xFF2E7D32) else Color(0xFFE65100),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your Score: ${result.score}%  (Threshold: 70%)",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$correctCount of $totalQ Questions Correct",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            if (passed) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "+${result.xpAwarded} XP Earned! Next Stage Unlocked.",
                                    color = GreenNode,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (result.promotedLevel != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🌟 Level Promoted to ${result.promotedLevel}!",
                                        color = HeadingPurple,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else if (result.remedialFeedback != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = result.remedialFeedback,
                                    color = Color(0xFF5D4037),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Review Answers & Explanations Button
                    OutlinedButton(
                        onClick = onOpenReview,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, HeadingPurple)
                    ) {
                        Text(
                            text = "📖 Review Answers & Explanations",
                            color = HeadingPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Retake Quiz Button
                    OutlinedButton(
                        onClick = onRetake,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(
                            text = "🔄 Retake Assessment Quiz",
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HeadingPurple)
                    ) {
                        Text(
                            text = if (passed) "Continue Roadmap" else "Close & Practice Drills",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                // =================================================================
                // 3. ACTIVE QUIZ: Single Question at a time with Navigation & Dots
                // =================================================================
                } else {
                    val questions = stage.assessmentQuestions
                    val totalQuestions = questions.size

                    if (questions.isEmpty()) {
                        Text("No assessment questions available for this stage.", color = TextSecondary, fontSize = 13.sp)
                    } else {
                        val safeIndex = currentQuestionIndex.coerceIn(0, maxOf(0, totalQuestions - 1))
                        val currentQuestion = questions[safeIndex]

                        // Progress Pill & Linear Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEDE5FA))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Question ${safeIndex + 1} of $totalQuestions",
                                    color = HeadingPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "${selectedAnswers.size}/$totalQuestions answered",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Indicator Bar
                        LinearProgressIndicator(
                            progress = ((safeIndex + 1).toFloat() / totalQuestions).coerceIn(0f, 1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = HeadingPurple,
                            trackColor = Color(0xFFEDE5FA)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Question Dot Navigator (Allows jumping directly between questions)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 0 until totalQuestions) {
                                val isAnswered = selectedAnswers.containsKey(i)
                                val isCurrent = i == safeIndex

                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCurrent -> HeadingPurple
                                                isAnswered -> Color(0xFFD6C7F7)
                                                else -> Color(0xFFF0EDF6)
                                            }
                                        )
                                        .border(
                                            width = if (isCurrent) 2.dp else 1.dp,
                                            color = if (isCurrent) HeadingPurple else CardBorder,
                                            shape = CircleShape
                                        )
                                        .clickable { onJumpToQuestion(i) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${i + 1}",
                                        color = if (isCurrent) Color.White else if (isAnswered) HeadingPurple else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Question Text Card
                        Text(
                            text = currentQuestion.question,
                            color = HeadingPurple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4 Simple Options
                        currentQuestion.options.forEachIndexed { optIdx, optText ->
                            val isSelected = selectedAnswers[safeIndex] == optIdx
                            val optionLetter = ('A'.code + optIdx).toChar()

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFFF3EDFD) else Color(0xFFF9F7FD))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) HeadingPurple else CardBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectOption(safeIndex, optIdx) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) HeadingPurple else Color(0xFFEDE5FA)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = optionLetter.toString(),
                                        color = if (isSelected) Color.White else HeadingPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = optText,
                                    color = if (isSelected) HeadingPurple else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSelectOption(safeIndex, optIdx) },
                                    colors = RadioButtonDefaults.colors(selectedColor = HeadingPurple)
                                )
                            }
                        }

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Question Navigation Buttons (Previous, Next, Submit)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Previous Question Button
                            OutlinedButton(
                                onClick = onPreviousQuestion,
                                enabled = safeIndex > 0,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (safeIndex > 0) HeadingPurple else CardBorder),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (safeIndex > 0) HeadingPurple else TextSecondary
                                )
                            ) {
                                Text(text = "← Prev", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Next Question or Submit Button
                            if (safeIndex < totalQuestions - 1) {
                                Button(
                                    onClick = onNextQuestion,
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HeadingPurple)
                                ) {
                                    Text(text = "Next →", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = onSubmit,
                                    enabled = !isSubmitting,
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HeadingPurple)
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text(text = "Submit Quiz", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSlidersIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // 3 horizontal slider lines
        drawLine(color = color, start = Offset(0f, h * 0.25f), end = Offset(w, h * 0.25f), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(0f, h * 0.50f), end = Offset(w, h * 0.50f), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(0f, h * 0.75f), end = Offset(w, h * 0.75f), strokeWidth = strokeWidth)

        // Slider knobs
        drawCircle(color = color, radius = strokeWidth * 1.5f, center = Offset(w * 0.35f, h * 0.25f))
        drawCircle(color = color, radius = strokeWidth * 1.5f, center = Offset(w * 0.70f, h * 0.50f))
        drawCircle(color = color, radius = strokeWidth * 0.40f, center = Offset(w * 0.40f, h * 0.75f))
    }
}

@Composable
fun PuzzleIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.20f, h * 0.20f)
                lineTo(w * 0.45f, h * 0.20f)
                quadraticTo(w * 0.50f, h * 0.35f, w * 0.55f, h * 0.20f)
                lineTo(w * 0.80f, h * 0.20f)
                lineTo(w * 0.80f, h * 0.80f)
                lineTo(w * 0.20f, h * 0.80f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GrowthRoadmapScreenPreview() {
    AISkillsMirrorTheme {
        GrowthRoadmapScreen(
            viewModel = GrowthRoadmapViewModel()
        )
    }
}
