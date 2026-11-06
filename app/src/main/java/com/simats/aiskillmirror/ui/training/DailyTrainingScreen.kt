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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.assess.SparkleMiniIcon
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.dashboard.MicIcon
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
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

                        Spacer(modifier = Modifier.height(10.dp))

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
                                text = "TODAY'S PRACTICE CHALLENGES",
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
                        uiState.weeklyLearningPlan.forEachIndexed { i, dayPlan ->
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
                if (uiState.completedPracticeItems.isNotEmpty()) {
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

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.completedPracticeItems.forEach { item ->
                            CompletedPracticeCard(item = item)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Primary Action Button: Complete Practice Session
                Button(
                    onClick = {
                        viewModel.onStartPracticeClick()
                        onStartPracticeClick()
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                PlayIcon(size = 16.dp, color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Complete Practice (+35 XP)",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Fixed Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.LEARN,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
fun CompletedPracticeCard(item: PracticeItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
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
                        text = item.category,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Text(
                text = item.xpText,
                color = GreenNode,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
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
