package com.simats.aiskillmirror.ui.training

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.dashboard.MicIcon
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.register.CheckCircleIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun DailyTrainingScreen(
    viewModel: DailyTrainingViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onStartPracticeClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
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
            // Scrollable Body Content
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
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.onBackClick()
                                    onBackClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            BackArrowIcon(size = 20.dp, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = uiState.headerTitle,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.headerDateText,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Calendar Icon Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
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

                // Streak & Weekday Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFF132A1B))
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
                        text = uiState.weekDayText,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daily Goal Card Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF181538), Color(0xFF221B4A))
                            )
                        )
                        .border(1.dp, Color(0xFF332B66), RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = uiState.goalLabel,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${uiState.currentMinutes} / ${uiState.targetMinutes} min",
                                color = TextPrimary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )

                            FlameIcon(size = 24.dp, color = GreenNode)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Linear Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFF20263F))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(uiState.currentMinutes.toFloat() / uiState.targetMinutes)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(GreenNode)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.goalSubtitle,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section Header: Your Daily Practice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your daily practice",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.practiceProgressText,
                        color = TaglineText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Completed Practice Items
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.completedPracticeItems.forEach { item ->
                        CompletedPracticeCard(item = item)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Up Next Practice Card (Featured)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF181538), Color(0xFF201B48))
                            )
                        )
                        .border(1.dp, Color(0xFF332B66), RoundedCornerShape(20.dp))
                        .padding(16.dp)
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
                                    .background(Color(0xFF2A2252))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "UP NEXT",
                                    color = TaglineText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }

                            Text(
                                text = uiState.upNextMetaText,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF2A2252)),
                                contentAlignment = Alignment.Center
                            ) {
                                MicIcon(size = 20.dp, color = TaglineText)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = uiState.upNextTitle,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = uiState.upNextCategory,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = uiState.upNextDescription,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Action Button: Start 6-minute practice
                Button(
                    onClick = {
                        viewModel.onStartPracticeClick()
                        onStartPracticeClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGradientStart),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
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
                                text = uiState.startButtonText,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
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
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .border(1.dp, Color(0xFF22293F), RoundedCornerShape(18.dp))
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
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF132A1B)),
                    contentAlignment = Alignment.Center
                ) {
                    CheckCircleIcon(size = 20.dp, color = GreenNode)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.title,
                        color = TextPrimary,
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DailyTrainingScreenPreview() {
    AISkillsMirrorTheme {
        DailyTrainingScreen(
            viewModel = DailyTrainingViewModel()
        )
    }
}
