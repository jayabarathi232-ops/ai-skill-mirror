package com.simats.aiskillmirror.ui.progress

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.results.SkillScoreBarItem
import com.simats.aiskillmirror.ui.results.SkillScoreItem
import com.simats.aiskillmirror.ui.theme.*
import com.simats.aiskillmirror.ui.training.CalendarIcon

@Composable
fun ProgressAnalyticsScreen(
    viewModel: ProgressAnalyticsViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onViewReportClick: () -> Unit = {},
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
                                text = uiState.headerCategory,
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

                Spacer(modifier = Modifier.height(20.dp))

                // Timeframe Segment Tabs
                TimeframeSegmentedTabs(
                    selectedTimeframe = uiState.selectedTimeframe,
                    onSelect = { viewModel.onSelectTimeframe(it) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Overall Skill Score Chart Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "Overall skill score",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${uiState.overallScore}",
                                    color = TextPrimary,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Gain Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(0xFF132A1B))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = uiState.pointsGainText,
                                    color = GreenNode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Smooth Trend Line Chart
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            SmoothTrendChartCanvas()
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // X-Axis Date Labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Sep 29", color = TextSecondary, fontSize = 11.sp)
                            Text(text = "Oct 06", color = TextSecondary, fontSize = 11.sp)
                            Text(text = "Oct 13", color = TextSecondary, fontSize = 11.sp)
                            Text(text = "Oct 20", color = TextSecondary, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = uiState.scoreRangeText,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Metrics Grid Row (Sessions & Time invested)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Metric 1: Sessions
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
                                text = "Sessions",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${uiState.totalSessions}",
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.sessionsWeeklyGainText,
                                color = GreenNode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Metric 2: Time Invested
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
                                text = "Time invested",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.timeInvestedText,
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.timeWeeklyGainText,
                                color = GreenNode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Skill Growth Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Skill growth",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View report",
                        color = TaglineText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            viewModel.onViewReportClick()
                            onViewReportClick()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Skill Growth List Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        uiState.skillItems.forEach { item ->
                            SkillScoreBarItem(
                                item = SkillScoreItem(
                                    name = item.name,
                                    score = item.score,
                                    maxScore = item.maxScore,
                                    isTopTier = item.isTopTier
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Fixed Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.PROGRESS,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
fun TimeframeSegmentedTabs(
    selectedTimeframe: TimeframeTab,
    onSelect: (TimeframeTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, Color(0xFF22293F), RoundedCornerShape(14.dp))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeframeTabItem(
                label = "Week",
                isSelected = selectedTimeframe == TimeframeTab.WEEK,
                onClick = { onSelect(TimeframeTab.WEEK) },
                modifier = Modifier.weight(1f)
            )
            TimeframeTabItem(
                label = "Month",
                isSelected = selectedTimeframe == TimeframeTab.MONTH,
                onClick = { onSelect(TimeframeTab.MONTH) },
                modifier = Modifier.weight(1f)
            )
            TimeframeTabItem(
                label = "All time",
                isSelected = selectedTimeframe == TimeframeTab.ALL_TIME,
                onClick = { onSelect(TimeframeTab.ALL_TIME) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TimeframeTabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabBackground = if (isSelected) Color(0xFF1E1A3D) else Color.Transparent
    val borderModifier = if (isSelected) {
        Modifier.border(1.dp, Color(0xFF332B66), RoundedCornerShape(10.dp))
    } else Modifier

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(tabBackground)
            .then(borderModifier)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) TextPrimary else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun SmoothTrendChartCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.5.dp.toPx()

        val p1 = Offset(w * 0.05f, h * 0.85f)
        val p2 = Offset(w * 0.35f, h * 0.70f)
        val p3 = Offset(w * 0.65f, h * 0.45f)
        val p4 = Offset(w * 0.95f, h * 0.15f)

        // Smooth cubic bezier path
        val path = Path().apply {
            moveTo(p1.x, p1.y)
            cubicTo(
                (p1.x + p2.x) / 2, (p1.y + p2.y) / 2,
                (p1.x + p2.x) / 2, p2.y,
                p2.x, p2.y
            )
            cubicTo(
                (p2.x + p3.x) / 2, p2.y,
                (p2.x + p3.x) / 2, p3.y,
                p3.x, p3.y
            )
            cubicTo(
                (p3.x + p4.x) / 2, p3.y,
                (p3.x + p4.x) / 2, p4.y,
                p4.x, p4.y
            )
        }

        // Fill path under curve
        val fillPath = Path().apply {
            addPath(path)
            lineTo(p4.x, h)
            lineTo(p1.x, h)
            close()
        }

        // Draw gradient area fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    AccentPurple.copy(alpha = 0.35f),
                    Color.Transparent
                )
            )
        )

        // Draw curve stroke line
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(AccentPurple, PrimaryGradientEnd, GreenNode)
            ),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Draw glowing end dot at Oct 20
        drawCircle(color = GreenNode, radius = 5.dp.toPx(), center = p4)
        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = p4)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProgressAnalyticsScreenPreview() {
    AISkillsMirrorTheme {
        ProgressAnalyticsScreen(
            viewModel = ProgressAnalyticsViewModel()
        )
    }
}
