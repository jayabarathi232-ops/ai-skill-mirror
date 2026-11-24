package com.simats.aiskillmirror.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.data.network.PracticeHistoryItemDto
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun PerformanceHistoryScreen(
    viewModel: PerformanceHistoryViewModel,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
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
            // ==========================================
            // TOP APP BAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BackArrowIcon(size = 18.dp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Recent Performance History",
                        color = HeadingPurple,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "All AI-evaluated practical sessions & feedback",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = PrimaryGradientStart,
                        strokeWidth = 2.dp
                    )
                }
            }

            // ==========================================
            // STATS OVERVIEW STRIP
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HistoryMetricCol(label = "Total Sessions", value = "${uiState.totalSessions}")
                Box(modifier = Modifier.width(1.dp).height(28.dp).background(CardBorder))
                HistoryMetricCol(label = "Practice Time", value = "${uiState.totalMinutes}m")
                Box(modifier = Modifier.width(1.dp).height(28.dp).background(CardBorder))
                HistoryMetricCol(label = "Average Score", value = "${uiState.averageScore}%", isScore = true)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // FILTER CHIPS ROW
            // ==========================================
            val codingCount = uiState.countsBySkill["coding"] ?: 0
            val photoCount = uiState.countsBySkill["photography"] ?: 0
            val musicCount = uiState.countsBySkill["music"] ?: 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HistoryFilterChip(
                    text = "All (${uiState.totalSessions})",
                    isSelected = uiState.selectedSkillFilter == "all",
                    onClick = { viewModel.setFilter("all") }
                )
                HistoryFilterChip(
                    text = "💻 Coding ($codingCount)",
                    isSelected = uiState.selectedSkillFilter == "coding",
                    onClick = { viewModel.setFilter("coding") }
                )
                HistoryFilterChip(
                    text = "📷 Photo ($photoCount)",
                    isSelected = uiState.selectedSkillFilter in listOf("photography", "photo"),
                    onClick = { viewModel.setFilter("photography") }
                )
                HistoryFilterChip(
                    text = "🎵 Music ($musicCount)",
                    isSelected = uiState.selectedSkillFilter == "music",
                    onClick = { viewModel.setFilter("music") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // SESSIONS LIST
            // ==========================================
            if (uiState.filteredItems.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎯", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No practice sessions found",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Complete your first practice session in this domain to see evaluations.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.filteredItems, key = { it.id }) { item ->
                        FullHistoryCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun FullHistoryCard(item: PracticeHistoryItemDto) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), ambientColor = Color(0xFFD6C8F2), spotColor = Color(0xFFD6C8F2))
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Column {
            // Header Row: Icon + Skill Name + Subcategory + Score Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(TaglineBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.iconEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.skillName,
                            color = HeadingPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (item.subcategory.isNotBlank()) {
                            Text(
                                text = " · ${item.subcategory}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Text(
                        text = item.dateText + if (item.fullDate.isNotBlank()) " (${item.fullDate})" else "",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Score Badge
                val scoreColor = when {
                    item.score >= 80 -> GreenNode
                    item.score >= 60 -> Color(0xFFD97706)
                    else -> Color(0xFFEF4444)
                }
                val scoreBg = when {
                    item.score >= 80 -> Color(0xFFDCFCE7)
                    item.score >= 60 -> Color(0xFFFEF3C7)
                    else -> Color(0xFFFEE2E2)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(scoreBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${item.score}%",
                        color = scoreColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Task Title
            Text(
                text = item.activity,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Feedback Text
            Text(
                text = item.feedback,
                color = if (item.score >= 80) GreenNode else TextPrimary,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                maxLines = if (expanded) 10 else 2,
                overflow = TextOverflow.Ellipsis
            )

            // Strengths & Weaknesses if expanded
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    if (item.strengths.isNotEmpty()) {
                        Text(
                            text = "STRENGTHS",
                            color = GreenNode,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        item.strengths.forEach { s ->
                            Text(
                                text = "✓ $s",
                                color = TextPrimary,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    if (item.weaknesses.isNotEmpty()) {
                        Text(
                            text = "ACTION ITEMS",
                            color = Color(0xFFD97706),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        item.weaknesses.forEach { w ->
                            Text(
                                text = "• $w",
                                color = TextPrimary,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.durationMinutes} min practice · +${item.xpAwarded} XP",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = if (expanded) "Show Less ▲" else "View Details ▼",
                    color = PrimaryGradientStart,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HistoryMetricCol(label: String, value: String, isScore: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = if (isScore) PrimaryGradientStart else TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp
        )
    }
}

@Composable
fun HistoryFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PrimaryGradientStart else CardBackground)
            .border(1.dp, if (isSelected) PrimaryGradientStart else CardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else TextPrimary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
