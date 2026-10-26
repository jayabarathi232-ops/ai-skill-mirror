package com.simats.aiskillmirror.ui.coach

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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.assessment.MoreDotsIcon
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.dashboard.MicIcon
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.splash.MirrorLogoIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun AiCoachScreen(
    viewModel: AiCoachViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onStartPracticeFromCoachClick: () -> Unit = {},
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
            // Scrollable Chat Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                                text = uiState.coachName,
                                color = TextPrimary,
                                fontSize = 20.sp,
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

                    // More Dots Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        MoreDotsIcon(size = 18.dp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sub-header Notice Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF181538))
                        .border(1.dp, Color(0xFF2F285C), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        SparkleIcon(size = 16.dp, color = TaglineText)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = uiState.bannerNoticeText,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Timestamp Header
                Text(
                    text = uiState.timestampHeader,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Chat Messages List
                uiState.messages.forEachIndexed { index, message ->
                    val isLastMessage = index == uiState.messages.size - 1

                    if (message.isUser) {
                        // User Right-Aligned Message Bubble
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                                    .background(Color(0xFF251E45))
                                    .border(1.dp, Color(0xFF392F60), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
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
                                .padding(vertical = 6.dp),
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
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
                                        .background(SurfaceDark)
                                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
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
                                                    .clip(RoundedCornerShape(100.dp))
                                                    .background(Color(0xFF221A3E))
                                                    .border(1.dp, Color(0xFF382D60), RoundedCornerShape(100.dp))
                                                    .clickable {
                                                        if (chipText.contains("practice", ignoreCase = true)) {
                                                            onStartPracticeFromCoachClick()
                                                        }
                                                        viewModel.onSuggestionChipClick(chipText)
                                                    }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = chipText,
                                                    color = TaglineText,
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

            // Bottom Input Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Color(0xFF22293F), RoundedCornerShape(26.dp))
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
                                text = "Ask Mira anything...",
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
                        MicIcon(size = 18.dp, color = TextSecondary)

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
