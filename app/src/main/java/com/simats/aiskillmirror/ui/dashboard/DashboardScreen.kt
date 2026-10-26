package com.simats.aiskillmirror.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.onboarding.TargetScanIcon
import com.simats.aiskillmirror.ui.splash.ChartIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNotificationsClick: () -> Unit = {},
    onTabSelected: (DashboardTab) -> Unit = {},
    onViewSkillMirrorClick: () -> Unit = {},
    onViewPlanClick: () -> Unit = {},
    onContinueTrainingClick: () -> Unit = {},
    onOpenAiCoachClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            // Main Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = uiState.currentDateText,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Good morning, ${uiState.userName}",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Notification Bell Icon Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, Color(0xFF22293F), CircleShape)
                            .clickable {
                                viewModel.onNotificationsClick()
                                onNotificationsClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        BellIcon(size = 18.dp, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Skill Score Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF16132D), Color(0xFF1E1A3D))
                            )
                        )
                        .border(1.dp, Color(0xFF2E2858), RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Skill Score Circular Arc
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(105.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                DottedSkillScoreArc(score = uiState.skillScore)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${uiState.skillScore}",
                                        color = TextPrimary,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "SKILL SCORE",
                                        color = TextSecondary,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${uiState.assessedSkillsCount} skills assessed",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Right: Skill Score Info Details
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            // Points Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(0xFF132A1B))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+${uiState.monthlyPtsGain} pts this month",
                                    color = GreenNode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = uiState.headline,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = uiState.learnerStatus,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "View skill mirror →",
                                color = TaglineText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    viewModel.onViewSkillMirrorClick()
                                    onViewSkillMirrorClick()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Metrics Row (Current streak & Minutes today)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Metric 1: Current Streak
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceDark)
                            .border(1.dp, Color(0xFF22293F), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Current streak",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${uiState.currentStreakDays} days",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.streakSubtitle,
                                color = GreenNode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Metric 2: Minutes Today
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceDark)
                            .border(1.dp, Color(0xFF22293F), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Minutes today",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${uiState.minutesToday} / ${uiState.goalMinutesToday}",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.minutesSubtitle,
                                color = Color(0xFFEAB308),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Continue Your Journey Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Continue your journey",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View plan",
                        color = TaglineText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            viewModel.onViewPlanClick()
                            onViewPlanClick()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Lesson Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF221A3E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    MicIcon(size = 20.dp, color = TaglineText)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = uiState.currentLessonTitle,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = uiState.currentLessonCategory,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }

                            Text(
                                text = uiState.currentLessonDuration,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Linear Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFF222842))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(uiState.currentLessonProgress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(PrimaryGradientEnd)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Continue training →",
                            color = TaglineText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                viewModel.onContinueTrainingClick()
                                onContinueTrainingClick()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AI Coach Card Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF1A163B), Color(0xFF1F1B45))
                            )
                        )
                        .border(1.dp, Color(0xFF332B66), RoundedCornerShape(20.dp))
                        .clickable {
                            viewModel.onOpenAiCoachClick()
                            onOpenAiCoachClick()
                        }
                        .padding(16.dp)
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
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF2A2252)),
                                contentAlignment = Alignment.Center
                            ) {
                                SparkleIcon(size = 20.dp, color = TaglineText)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = uiState.aiCoachTitle,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = uiState.aiCoachSubtitle,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        RightArrowHeadIcon(size = 18.dp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Fixed Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { tab ->
                    viewModel.onSelectTab(tab)
                    onTabSelected(tab)
                }
            )
        }
    }
}

@Composable
fun DottedSkillScoreArc(score: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2
        val cy = size.height / 2
        val radius = size.width / 2 - 8.dp.toPx()
        val totalDots = 28
        val activeDots = (totalDots * (score / 100f)).toInt()

        for (i in 0 until totalDots) {
            val angleDeg = 135f + (i * (270f / (totalDots - 1)))
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val dotX = cx + radius * cos(angleRad).toFloat()
            val dotY = cy + radius * sin(angleRad).toFloat()

            val dotColor = if (i < activeDots) AccentPurple else Color(0xFF322C5C)
            val dotRadius = if (i < activeDots) 3.5.dp.toPx() else 2.5.dp.toPx()

            drawCircle(
                color = dotColor,
                radius = dotRadius,
                center = Offset(dotX, dotY)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0C0F1D))
            .border(1.dp, Color(0xFF1E243A), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "Home",
            isSelected = selectedTab == DashboardTab.HOME,
            icon = { color -> HomeNavIcon(size = 20.dp, color = color) },
            onClick = { onTabSelected(DashboardTab.HOME) }
        )
        BottomNavItem(
            label = "Assess",
            isSelected = selectedTab == DashboardTab.ASSESS,
            icon = { color -> TargetScanIcon(size = 20.dp, color = color) },
            onClick = { onTabSelected(DashboardTab.ASSESS) }
        )
        BottomNavItem(
            label = "Learn",
            isSelected = selectedTab == DashboardTab.LEARN,
            icon = { color -> BookNavIcon(size = 20.dp, color = color) },
            onClick = { onTabSelected(DashboardTab.LEARN) }
        )
        BottomNavItem(
            label = "Progress",
            isSelected = selectedTab == DashboardTab.PROGRESS,
            icon = { color -> ChartIcon(size = 20.dp, color = color) },
            onClick = { onTabSelected(DashboardTab.PROGRESS) }
        )
        BottomNavItem(
            label = "Profile",
            isSelected = selectedTab == DashboardTab.PROFILE,
            icon = { color -> ProfileNavIcon(size = 20.dp, color = color) },
            onClick = { onTabSelected(DashboardTab.PROFILE) }
        )
    }
}

@Composable
fun BottomNavItem(
    label: String,
    isSelected: Boolean,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) AccentPurple else Color(0xFF64748B)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        icon(contentColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun BellIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.30f, h * 0.70f)
                lineTo(w * 0.70f, h * 0.70f)
                lineTo(w * 0.78f, h * 0.55f)
                lineTo(w * 0.70f, h * 0.35f)
                quadraticTo(w * 0.70f, h * 0.15f, w * 0.50f, h * 0.15f)
                quadraticTo(w * 0.30f, h * 0.15f, w * 0.30f, h * 0.35f)
                lineTo(w * 0.22f, h * 0.55f)
                close()

                moveTo(w * 0.40f, h * 0.75f)
                quadraticTo(w * 0.50f, h * 0.90f, w * 0.60f, h * 0.75f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun MicIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Mic capsule
        drawPath(
            path = Path().apply {
                moveTo(w * 0.35f, h * 0.35f)
                lineTo(w * 0.35f, h * 0.50f)
                quadraticTo(w * 0.35f, h * 0.65f, w * 0.50f, h * 0.65f)
                quadraticTo(w * 0.65f, h * 0.65f, w * 0.65f, h * 0.50f)
                lineTo(w * 0.65f, h * 0.35f)
                quadraticTo(w * 0.65f, h * 0.20f, w * 0.50f, h * 0.20f)
                quadraticTo(w * 0.35f, h * 0.20f, w * 0.35f, h * 0.35f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )

        // Mic stand arc
        drawPath(
            path = Path().apply {
                moveTo(w * 0.25f, h * 0.50f)
                quadraticTo(w * 0.25f, h * 0.80f, w * 0.50f, h * 0.80f)
                quadraticTo(w * 0.75f, h * 0.80f, w * 0.75f, h * 0.50f)

                moveTo(w * 0.50f, h * 0.80f)
                lineTo(w * 0.50f, h * 0.92f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun HomeNavIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.15f, h * 0.45f)
                lineTo(w * 0.50f, h * 0.15f)
                lineTo(w * 0.85f, h * 0.45f)
                lineTo(w * 0.85f, h * 0.85f)
                lineTo(w * 0.15f, h * 0.85f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun BookNavIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.50f, h * 0.25f)
                quadraticTo(w * 0.25f, h * 0.20f, w * 0.15f, h * 0.28f)
                lineTo(w * 0.15f, h * 0.80f)
                quadraticTo(w * 0.25f, h * 0.75f, w * 0.50f, h * 0.80f)
                quadraticTo(w * 0.75f, h * 0.75f, w * 0.85f, h * 0.80f)
                lineTo(w * 0.85f, h * 0.28f)
                quadraticTo(w * 0.75f, h * 0.20f, w * 0.50f, h * 0.25f)

                moveTo(w * 0.50f, h * 0.25f)
                lineTo(w * 0.50f, h * 0.80f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun ProfileNavIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.22f,
            center = Offset(w / 2, h * 0.35f),
            style = Stroke(width = strokeWidth)
        )

        drawPath(
            path = Path().apply {
                moveTo(w * 0.20f, h * 0.85f)
                quadraticTo(w * 0.20f, h * 0.62f, w * 0.50f, h * 0.62f)
                quadraticTo(w * 0.80f, h * 0.62f, w * 0.80f, h * 0.85f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun RightArrowHeadIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.35f, h * 0.20f)
                lineTo(w * 0.65f, h * 0.50f)
                lineTo(w * 0.35f, h * 0.80f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    AISkillsMirrorTheme {
        DashboardScreen(
            viewModel = DashboardViewModel()
        )
    }
}
