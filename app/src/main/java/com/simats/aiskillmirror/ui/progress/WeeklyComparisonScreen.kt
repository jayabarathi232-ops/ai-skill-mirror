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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.data.network.SkillComparisonData
import com.simats.aiskillmirror.data.network.WeeklyComparisonData
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.localization.LanguageManager
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun WeeklyComparisonScreen(
    viewModel: WeeklyComparisonViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val compData = uiState.comparisonData

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

                Spacer(modifier = Modifier.height(16.dp))

                // Week Switcher Controls & Progress Label
                WeekSwitcherHeader(
                    currentWeekOffset = uiState.currentWeekOffset,
                    compData = compData,
                    onPrevClick = { viewModel.changeWeekOffset(-1) },
                    onNextClick = { viewModel.changeWeekOffset(1) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // 2. DUAL COMPARISON CARDS (PREVIOUS VS CURRENT)
                // ==========================================
                DualWeekCardsSection(compData = compData)

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 3. PRACTICE TIME COMPARISON & CHART
                // ==========================================
                PracticeTimeComparisonCard(compData = compData)

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 4. SKILL-WISE PERFORMANCE COMPARISON (Music, Photo, Coding)
                // ==========================================
                SkillWiseComparisonCard(compData = compData)

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 5. COMPLETED TASKS COMPARISON
                // ==========================================
                CompletedTasksComparisonCard(compData = compData)

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 7. AI WEEKLY IMPROVEMENT SUMMARY
                // ==========================================
                AiWeeklySummaryCard(
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

                // Next Week Button (disabled if offset >= 0)
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

            // Comparative Bar Chart
            ComparativeBarChart(
                label1 = "Prev Week",
                value1 = prevMin,
                label2 = "Curr Week",
                value2 = curMin,
                unit = "m"
            )
        }
    }
}

@Composable
fun ComparativeBarChart(
    label1: String,
    value1: Int,
    label2: String,
    value2: Int,
    unit: String
) {
    val maxVal = maxOf(value1, value2, 1)
    val fraction1 = (value1.toFloat() / maxVal.toFloat()).coerceIn(0.06f, 1f)
    val fraction2 = (value2.toFloat() / maxVal.toFloat()).coerceIn(0.06f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TaglineBackground.copy(alpha = 0.5f))
            .padding(14.dp)
    ) {
        // Bar 1: Previous Week
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label1,
                color = TextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.width(70.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction1)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF94A3B8))
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "$value1$unit",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(45.dp),
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bar 2: Current Week
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label2,
                color = HeadingPurple,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(70.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction2)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(PrimaryGradientStart, PrimaryGradientEnd)
                            )
                        )
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "$value2$unit",
                color = PrimaryGradientStart,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(45.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

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
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
                    Spacer(modifier = Modifier.height(10.dp))
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
            Text(
                text = skill.skillName,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

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

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Prev: " + (if (skill.previousScore != null) "${skill.previousScore}/100" else "No Score"),
                color = TextSecondary,
                fontSize = 11.sp
            )

            Text(
                text = "Curr: " + (if (skill.currentScore != null) "${skill.currentScore}/100" else "No Score"),
                color = if (skill.currentScore != null) HeadingPurple else TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (skill.improvementPercentage != null) {
                    "Formula: ((Cur - Prev)/Prev) × 100"
                } else {
                    "Not Enough Data"
                },
                color = TextSecondary.copy(alpha = 0.6f),
                fontSize = 9.sp
            )
        }
    }
}

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

            ComparativeBarChart(
                label1 = "Prev Week",
                value1 = prevTasks,
                label2 = "Curr Week",
                value2 = curTasks,
                unit = " tasks"
            )
        }
    }
}

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
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Summary Text
            if (ai.summaryText.isNotBlank()) {
                Text(
                    text = ai.summaryText,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Recommendations List
            if (ai.recommendations.isNotEmpty()) {
                Text(
                    text = "Recommendations for Next Week:",
                    color = HeadingPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                ai.recommendations.forEach { rec ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "• ", color = PrimaryGradientStart, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = rec, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }

            if (ai.message != null && ai.status != "success") {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF2F2))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Notice: ${ai.message}",
                        color = Color(0xFFB91C1C),
                        fontSize = 10.sp
                    )
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
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title.uppercase(),
                color = color,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatusBadge(status: String, text: String) {
    val (bg, fg) = when (status.lowercase()) {
        "increased" -> Pair(Color(0xFFDCFCE7), GreenNode)
        "decreased" -> Pair(Color(0xFFFEE2E2), Color(0xFFDC2626))
        "no_change" -> Pair(Color(0xFFF1F5F9), Color(0xFF64748B))
        else -> Pair(Color(0xFFF3E8FF), PrimaryGradientStart)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = fg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
