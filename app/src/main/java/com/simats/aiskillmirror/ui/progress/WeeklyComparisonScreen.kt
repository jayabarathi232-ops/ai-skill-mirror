package com.simats.aiskillmirror.ui.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.data.network.DailyComparisonDayData
import com.simats.aiskillmirror.data.network.SkillComparisonData
import com.simats.aiskillmirror.data.network.WeeklyComparisonData
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.localization.LanguageManager
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.theme.*

enum class WeeklyChartMode {
    CURRENT_WEEK,
    PREVIOUS_WEEK,
    COMPARE_BOTH
}

@Composable
fun WeeklyComparisonScreen(
    viewModel: WeeklyComparisonViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val compData = uiState.comparisonData
    var chartMode by remember { mutableStateOf(WeeklyChartMode.CURRENT_WEEK) }

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
                            BackArrowIcon(size = 18.dp, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = LanguageManager.t("weekly_comparison"),
                                color = HeadingPurple,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "PREVIOUS WEEK VS CURRENT WEEK",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = PrimaryGradientStart,
                            strokeWidth = 2.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Week Switcher Controls & Progress Label
                WeekSwitcherHeader(
                    currentWeekOffset = uiState.currentWeekOffset,
                    compData = compData,
                    onPrevClick = { viewModel.changeWeekOffset(-1) },
                    onNextClick = { viewModel.changeWeekOffset(1) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Chart Mode Segmented Selector: Current Week | Previous Week | Compare Both
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEDE9FE))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    WeeklyModeChip(
                        label = "Current Week",
                        isSelected = chartMode == WeeklyChartMode.CURRENT_WEEK,
                        onClick = { chartMode = WeeklyChartMode.CURRENT_WEEK },
                        modifier = Modifier.weight(1f)
                    )
                    WeeklyModeChip(
                        label = "Previous Week",
                        isSelected = chartMode == WeeklyChartMode.PREVIOUS_WEEK,
                        onClick = { chartMode = WeeklyChartMode.PREVIOUS_WEEK },
                        modifier = Modifier.weight(1f)
                    )
                    WeeklyModeChip(
                        label = "Compare Both",
                        isSelected = chartMode == WeeklyChartMode.COMPARE_BOTH,
                        onClick = { chartMode = WeeklyChartMode.COMPARE_BOTH },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // 1. CENTERPIECE: DAILY PRACTICE BAR CHART (%) BREAKDOWN (MATCHING USER PHOTO)
                // ==========================================
                DailyPracticeBarChartCard(
                    compData = compData,
                    mode = chartMode
                )

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // 2. PREVIOUS WEEK VS CURRENT WEEK SUMMARY & AI INSIGHTS
                // ==========================================
                WeeklyComparisonSummaryCard(
                    compData = compData,
                    isRetrying = uiState.isAiRetrying,
                    onRetryClick = { viewModel.retryAiSummary() }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.PROGRESS,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
fun WeekSwitcherHeader(
    currentWeekOffset: Int,
    compData: WeeklyComparisonData,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
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
                // Prev Week Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TaglineBackground)
                        .clickable { onPrevClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "◀ " + LanguageManager.t("prev_week"),
                        color = TaglineText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Center Week Label
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (compData.isCurrentWeek) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Week in Progress",
                                    color = Color(0xFFB45309),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = compData.currentWeekLabel,
                            color = HeadingPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = compData.currentWeekRange.ifBlank { "Monday – Sunday" },
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                // Next Week Button
                val canGoNext = currentWeekOffset < 0
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (canGoNext) TaglineBackground else TaglineBackground.copy(alpha = 0.3f))
                        .clickable(enabled = canGoNext) { onNextClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = LanguageManager.t("next_week") + " ▶",
                        color = if (canGoNext) TaglineText else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (compData.fairComparisonNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGradientStart.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "ℹ️ " + compData.fairComparisonNote,
                        color = PrimaryGradientStart,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun DualWeekCardsSection(compData: WeeklyComparisonData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // PREVIOUS WEEK CARD
        SingleWeekSummaryCard(
            modifier = Modifier.weight(1f),
            title = compData.previousWeekLabel,
            dateRange = compData.previousWeekRange,
            isCurrent = false,
            practiceMinutes = compData.previousWeek.totalPracticeMinutes,
            completedTasks = compData.previousWeek.completedTasks,
            musicScore = compData.previousWeek.musicScore,
            photoScore = compData.previousWeek.photographyScore,
            codingScore = compData.previousWeek.codingScore
        )

        // CURRENT WEEK CARD
        SingleWeekSummaryCard(
            modifier = Modifier.weight(1f),
            title = if (compData.isCurrentWeek) "Current Week" else compData.currentWeekLabel,
            dateRange = compData.currentWeekRange,
            isCurrent = compData.isCurrentWeek,
            practiceMinutes = compData.currentWeek.totalPracticeMinutes,
            completedTasks = compData.currentWeek.completedTasks,
            musicScore = compData.currentWeek.musicScore,
            photoScore = compData.currentWeek.photographyScore,
            codingScore = compData.currentWeek.codingScore
        )
    }
}

@Composable
fun SingleWeekSummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    dateRange: String,
    isCurrent: Boolean,
    practiceMinutes: Int,
    completedTasks: Int,
    musicScore: Int?,
    photoScore: Int?,
    codingScore: Int?
) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground)
            .border(
                1.5.dp,
                if (isCurrent) PrimaryGradientStart else CardBorder,
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = HeadingPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Active",
                            color = Color(0xFFB45309),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = dateRange,
                color = TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Time & Tasks
            MetricRow(label = "Practice Time", value = "${practiceMinutes}m")
            Spacer(modifier = Modifier.height(6.dp))
            MetricRow(label = "Tasks Done", value = "$completedTasks")

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
            Spacer(modifier = Modifier.height(10.dp))

            // Skill Scores
            Text(
                text = "Performance Scores",
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            ScoreRow(label = "Music", score = musicScore)
            Spacer(modifier = Modifier.height(4.dp))
            ScoreRow(label = "Photo", score = photoScore)
            Spacer(modifier = Modifier.height(4.dp))
            ScoreRow(label = "Coding", score = codingScore)
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ScoreRow(label: String, score: Int?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        if (score != null) {
            Text(
                text = "$score/100",
                color = PrimaryGradientStart,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = "Not Enough Data",
                color = TextSecondary.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

// ==========================================
// 2. PRACTICE TIME COMPARISON & VERTICAL BAR CHART WITH PERCENTAGES
// ==========================================
@Composable
fun PracticeTimeComparisonCard(compData: WeeklyComparisonData) {
    val pt = compData.practiceTimeComparison
    val prevMin = pt.previousMinutes
    val curMin = pt.currentMinutes

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
                Column {
                    Text(
                        text = LanguageManager.t("practice_time_comp"),
                        color = HeadingPurple,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total minutes spent in active practice",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Status Badge
                StatusBadge(status = pt.status, text = pt.formattedDiff)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Numbers Comparison Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Previous Week", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${prevMin}m",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "vs",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Current Week", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${curMin}m",
                        color = PrimaryGradientStart,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // COMPARATIVE BAR CHART WITH PERCENTAGES
            // ==========================================
            VerticalComparisonBarChartWithPercentage(
                prevVal = prevMin,
                currVal = curMin,
                unit = "m",
                prevLabel = "Prev Week",
                currLabel = "Curr Week"
            )
        }
    }
}

@Composable
fun VerticalComparisonBarChartWithPercentage(
    prevVal: Int,
    currVal: Int,
    unit: String,
    prevLabel: String,
    currLabel: String
) {
    val totalSum = (prevVal + currVal).coerceAtLeast(1)
    val prevPct = ((prevVal.toFloat() / totalSum.toFloat()) * 100).toInt()
    val currPct = if (prevVal == 0 && currVal > 0) 100 else ((currVal.toFloat() / totalSum.toFloat()) * 100).toInt()

    val maxVal = maxOf(prevVal, currVal, 1)
    val prevFraction = (prevVal.toFloat() / maxVal.toFloat()).coerceIn(0.12f, 1f)
    val currFraction = (currVal.toFloat() / maxVal.toFloat()).coerceIn(0.12f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Time Ratio & Growth",
                    color = HeadingPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(if (currVal >= prevVal) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    val diff = currVal - prevVal
                    val growthText = if (prevVal == 0) "+100% Growth" else if (diff >= 0) "+${((diff.toFloat()/prevVal)*100).toInt()}% Growth" else "${((diff.toFloat()/prevVal)*100).toInt()}%"
                    Text(
                        text = "▲ $growthText",
                        color = if (currVal >= prevVal) GreenNode else Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Two Vertical Bars with Prominent Percentage Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Bar 1: Previous Week
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.width(90.dp)
                ) {
                    // Percentage on top of Bar 1
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE2E8F0))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$prevPct%",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Vertical Bar
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .fillMaxHeight(prevFraction)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF94A3B8), Color(0xFFCBD5E1))
                                )
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$prevVal$unit",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = prevLabel,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                // Divider VS
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "vs",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Bar 2: Current Week (With Primary Gradient & Highlight Percentage)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.width(90.dp)
                ) {
                    // Percentage on top of Bar 2
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$currPct%",
                            color = GreenNode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Vertical Bar
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .fillMaxHeight(currFraction)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                )
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$currVal$unit",
                        color = PrimaryGradientStart,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currLabel,
                        color = HeadingPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. BAR CHART ICON BOX & PHOTO-MATCHED DAILY PRACTICE BAR CHART
// ==========================================
@Composable
fun BarChartIconBox() {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF3E8FF)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.5.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.5.dp)
                    .height(11.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF6D28D9))
            )
            Box(
                modifier = Modifier
                    .width(4.5.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF6D28D9))
            )
            Box(
                modifier = Modifier
                    .width(4.5.dp)
                    .height(15.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF6D28D9))
            )
        }
    }
}

@Composable
fun DailyPracticeBarChartCard(
    compData: WeeklyComparisonData,
    mode: WeeklyChartMode = WeeklyChartMode.CURRENT_WEEK
) {
    val dailyList = if (compData.dailyBreakdown.isNotEmpty()) {
        compData.dailyBreakdown
    } else {
        listOf(
            DailyComparisonDayData("Mon", "Monday", minutes = 0, percentage = 0, isToday = true),
            DailyComparisonDayData("Tue", "Tuesday", minutes = 30, percentage = 39),
            DailyComparisonDayData("Wed", "Wednesday", minutes = 13, percentage = 17),
            DailyComparisonDayData("Thu", "Thursday", minutes = 5, percentage = 6),
            DailyComparisonDayData("Fri", "Friday", minutes = 34, percentage = 44),
            DailyComparisonDayData("Sat", "Saturday", minutes = 0, percentage = 0),
            DailyComparisonDayData("Sun", "Sunday", minutes = 0, percentage = 0)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = Color(0xFFD6C8F2),
                ambientColor = Color(0xFFEDE9FE)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEDE9FE), RoundedCornerShape(26.dp))
            .padding(20.dp)
    ) {
        Column {
            // Header with Bar Chart Icon, Title, Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BarChartIconBox()

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Practice Bar Chart (%)\nBreakdown)",
                        color = Color(0xFF2E1065),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = when (mode) {
                            WeeklyChartMode.PREVIOUS_WEEK -> "Previous week (${compData.previousWeekRange.ifBlank { "Mon – Sun" }}) distribution"
                            WeeklyChartMode.COMPARE_BOTH -> "Previous Week vs Current Week percentage share"
                            else -> "Monday to Sunday distribution with percentage share"
                        },
                        color = Color(0xFF64748B),
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            if (mode == WeeklyChartMode.COMPARE_BOTH) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF6D28D9)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Current Week", color = Color(0xFF2E1065), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFDDD6FE)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Prev Week", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chart Canvas: Left Y-Axis Labels + Right Dashed Lines & Vertical Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
            ) {
                // Y-Axis Labels: 100%, 75%, 50%, 25%, 0%
                val yAxisLabels = listOf("100%", "75%", "50%", "25%", "0%")
                Column(
                    modifier = Modifier
                        .width(42.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    yAxisLabels.forEach { label ->
                        Text(
                            text = label,
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }

                // Chart Grid & Bars Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // 5 Horizontal Dashed Gridlines
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 1.dp.toPx()
                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        val stepY = size.height / 4f

                        for (i in 0..4) {
                            val y = if (i == 4) size.height - 1f else i * stepY
                            drawLine(
                                color = Color(0xFFE2E8F0),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth,
                                pathEffect = dashEffect
                            )
                        }
                    }

                    // 7 Vertical Bars placed above 0% baseline
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dailyList.forEach { dayItem ->
                            val pct = if (mode == WeeklyChartMode.PREVIOUS_WEEK) dayItem.prevPercentage else dayItem.percentage
                            val isToday = dayItem.isToday && mode != WeeklyChartMode.PREVIOUS_WEEK

                            val barFraction = (pct / 100f).coerceIn(0f, 1f)
                            val totalUsableHeight = 130.dp
                            val minStubHeight = 12.dp
                            val computedBarHeight = minStubHeight + (totalUsableHeight - minStubHeight) * barFraction

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (mode == WeeklyChartMode.COMPARE_BOTH) {
                                    val prevPct = dayItem.prevPercentage
                                    val curPct = dayItem.percentage
                                    val prevFraction = (prevPct / 100f).coerceIn(0f, 1f)
                                    val curFraction = (curPct / 100f).coerceIn(0f, 1f)
                                    val prevHeight = minStubHeight + (totalUsableHeight - minStubHeight) * prevFraction
                                    val curHeight = minStubHeight + (totalUsableHeight - minStubHeight) * curFraction

                                    Text(
                                        text = "$curPct%",
                                        color = if (curPct > 0) Color(0xFF6D28D9) else Color(0xFF64748B),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .height(prevHeight)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(Color(0xFFDDD6FE))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .height(curHeight)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(Color(0xFF6D28D9))
                                        )
                                    }
                                } else {
                                    // Single mode (Current Week or Previous Week)
                                    if (isToday) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF6D28D9))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "$pct%",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "$pct%",
                                            color = Color(0xFF334155),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Box(
                                        modifier = Modifier
                                            .width(22.dp)
                                            .height(computedBarHeight)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                            .background(
                                                if (isToday) Color(0xFF6D28D9)
                                                else if (pct > 0) Color(0xFFC4B5FD)
                                                else Color(0xFFDDD6FE)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-Axis Day Labels Row (Offset by 42.dp to align with bars)
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(42.dp))

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    dailyList.forEach { dayItem ->
                        val isToday = dayItem.isToday && mode != WeeklyChartMode.PREVIOUS_WEEK

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isToday) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(Color(0xFF6D28D9))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = dayItem.day,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF6D28D9))
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Today",
                                    color = Color(0xFF6D28D9),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = dayItem.day,
                                    color = Color(0xFF334155),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Trophy Banner inside Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F0FF))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🏆", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Keep Practicing!",
                            color = Color(0xFF2E1065),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Track your progress and improve your skills.",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "✨", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun DailyBreakdownBarChartCard(compData: WeeklyComparisonData) {
    DailyPracticeBarChartCard(compData = compData, mode = WeeklyChartMode.CURRENT_WEEK)
}

@Composable
fun WeeklyModeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFF6D28D9) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color(0xFF4C1D95),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun WeeklyComparisonSummaryCard(
    compData: WeeklyComparisonData,
    isRetrying: Boolean = false,
    onRetryClick: () -> Unit = {}
) {
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
                Column {
                    Text(
                        text = "Weekly Performance Overview",
                        color = HeadingPurple,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Previous Week vs Current Week summary metrics",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                StatusBadge(
                    status = compData.practiceTimeComparison.status,
                    text = compData.practiceTimeComparison.formattedDiff
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two Quick Metric Comparison Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Practice Time Tile
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "Practice Time", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${compData.previousWeek.totalPracticeMinutes}m",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = " → ", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "${compData.currentWeek.totalPracticeMinutes}m",
                                color = PrimaryGradientStart,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Completed Tasks Tile
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "Completed Tasks", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${compData.previousWeek.completedTasks}",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = " → ", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "${compData.currentWeek.completedTasks}",
                                color = PrimaryGradientStart,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Skill Scores Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkBackground)
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🎵 Music", color = TextSecondary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (compData.currentWeek.musicScore != null) "${compData.currentWeek.musicScore}%" else "Pending",
                        color = HeadingPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.width(1.dp).height(20.dp).background(CardBorder))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📷 Photo", color = TextSecondary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (compData.currentWeek.photographyScore != null) "${compData.currentWeek.photographyScore}%" else "Pending",
                        color = HeadingPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.width(1.dp).height(20.dp).background(CardBorder))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "💻 Coding", color = TextSecondary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (compData.currentWeek.codingScore != null) "${compData.currentWeek.codingScore}%" else "Pending",
                        color = HeadingPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // AI Weekly Improvement Insight
            if (compData.aiSummary.summaryText.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF5F0FF))
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Improvement Insight",
                                color = HeadingPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = compData.aiSummary.summaryText,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                        if (compData.aiSummary.recommendations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 " + compData.aiSummary.recommendations.first(),
                                color = PrimaryGradientStart,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. SKILL-WISE PERFORMANCE COMPARISON
// ==========================================
@Composable
fun SkillWiseComparisonCard(compData: WeeklyComparisonData) {
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
            Text(
                text = LanguageManager.t("skill_performance_comp"),
                color = HeadingPurple,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Comparing genuine AI-assessed practical performance scores",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            compData.skillsComparison.forEachIndexed { index, skill ->
                SkillComparisonItem(skill = skill)
                if (index < compData.skillsComparison.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SkillComparisonItem(skill: SkillComparisonData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val emoji = when (skill.skillKey.lowercase()) {
                    "music" -> "🎵"
                    "photography", "photo" -> "📷"
                    else -> "💻"
                }
                Text(text = emoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = skill.skillName,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            StatusBadge(
                status = skill.status,
                text = when (skill.status) {
                    "increased" -> "▲ +${skill.improvementPercentage ?: 0}% (+${skill.scoreDiff ?: 0})"
                    "decreased" -> "▼ ${skill.improvementPercentage ?: 0}% (${skill.scoreDiff ?: 0})"
                    "no_change" -> "• 0% (No Change)"
                    else -> skill.statusText
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Visual Paired Horizontal Comparison Bars with Percentages
        val curScore = skill.currentScore ?: 0
        val prevScore = skill.previousScore ?: 0

        Column(modifier = Modifier.fillMaxWidth()) {
            // Curr Week Score Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Curr", color = HeadingPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((curScore / 100f).coerceIn(0.05f, 1f))
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                )
                            )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (skill.currentScore != null) "${skill.currentScore}%" else "Pending",
                    color = PrimaryGradientStart,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.width(48.dp),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Prev Week Score Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Prev", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.width(32.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((prevScore / 100f).coerceIn(0.05f, 1f))
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF94A3B8))
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (skill.previousScore != null) "${skill.previousScore}%" else "--",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(48.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

// ==========================================
// 5. COMPLETED TASKS COMPARISON
// ==========================================
@Composable
fun CompletedTasksComparisonCard(compData: WeeklyComparisonData) {
    val tk = compData.tasksComparison
    val prevTasks = tk.previousTasks
    val curTasks = tk.currentTasks

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
                Column {
                    Text(
                        text = LanguageManager.t("completed_tasks_comp"),
                        color = HeadingPurple,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Verified practice drills completed & saved in database",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                StatusBadge(status = tk.status, text = tk.formattedDiff)
            }

            Spacer(modifier = Modifier.height(14.dp))

            VerticalComparisonBarChartWithPercentage(
                prevVal = prevTasks,
                currVal = curTasks,
                unit = " tasks",
                prevLabel = "Prev Tasks",
                currLabel = "Curr Tasks"
            )
        }
    }
}

// ==========================================
// 6. AI WEEKLY IMPROVEMENT SUMMARY
// ==========================================
@Composable
fun AiWeeklySummaryCard(
    compData: WeeklyComparisonData,
    isRetrying: Boolean,
    onRetryClick: () -> Unit
) {
    val ai = compData.aiSummary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.5.dp, PrimaryGradientStart.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            // Header with AI Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PrimaryGradientStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✦", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = LanguageManager.t("ai_weekly_summary"),
                            color = HeadingPurple,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Powered by Python AI Backend & Real User Metrics",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                if (ai.canRetry) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TaglineBackground)
                            .clickable(enabled = !isRetrying) { onRetryClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isRetrying) "Analyzing..." else "Retry AI",
                            color = TaglineText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Highlight Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiHighlightPill(
                    modifier = Modifier.weight(1f),
                    title = "Most Improved",
                    value = ai.improvedMost,
                    color = GreenNode
                )
                AiHighlightPill(
                    modifier = Modifier.weight(1f),
                    title = "Needs Practice",
                    value = ai.needsPractice,
                    color = Color(0xFFD97706)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Consistency & Overall Trend
            if (ai.consistency.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TaglineBackground.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "PRACTICE CONSISTENCY",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = ai.consistency,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Summary Text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = ai.summaryText,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            // Recommendations
            if (ai.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "NEXT WEEK RECOMMENDATIONS",
                    color = HeadingPurple,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                ai.recommendations.forEach { rec ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "•", color = PrimaryGradientStart, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = rec, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AiHighlightPill(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = title.uppercase(), color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun StatusBadge(status: String, text: String) {
    val bgColor = when (status) {
        "increased" -> Color(0xFFDCFCE7)
        "decreased" -> Color(0xFFFEE2E2)
        else -> TaglineBackground
    }
    val textColor = when (status) {
        "increased" -> GreenNode
        "decreased" -> Color(0xFFEF4444)
        else -> TaglineText
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
