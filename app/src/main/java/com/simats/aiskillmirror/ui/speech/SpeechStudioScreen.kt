package com.simats.aiskillmirror.ui.speech

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.login.ShieldCheckIcon
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun SpeechStudioScreen(
    viewModel: SpeechStudioViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onStopClick: () -> Unit = {},
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
                                text = uiState.title,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.subtitle,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Question Mark Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, Color(0xFF22293F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        QuestionMarkCircleIcon(size = 18.dp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Category & Session Row
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
                            text = uiState.categoryBadgeText,
                            color = TaglineText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Text(
                        text = uiState.sessionText,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Speaking Prompt Banner Card
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
                            text = uiState.promptHeader,
                            color = TaglineText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.promptQuote,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = uiState.promptInstruction,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Status Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GreenNode)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.statusText,
                        color = GreenNode,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timer Display Counter
                Text(
                    text = uiState.timerText,
                    color = TextPrimary,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Audio Waveform Spectrum Bar Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AudioWaveformVisualizer()
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Recording Controls Row (Restart, Stop, Pause)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Restart Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B1E2E))
                            .border(1.dp, Color(0xFF282E48), CircleShape)
                            .clickable { viewModel.onRestartClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        RestartIcon(size = 20.dp, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.width(28.dp))

                    // Center Primary Stop Button
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(16.dp, CircleShape, spotColor = CenterCardGradientStart)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(CenterCardGradientStart, CenterCardGradientEnd)
                                )
                            )
                            .clickable {
                                viewModel.onStopClick()
                                onStopClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Stop square icon inside
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.width(28.dp))

                    // Pause Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B1E2E))
                            .border(1.dp, Color(0xFF282E48), CircleShape)
                            .clickable { viewModel.onPauseClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        PauseIcon(size = 20.dp, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = uiState.feedbackHelpText,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Privacy Card Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF221A3E)),
                            contentAlignment = Alignment.Center
                        ) {
                            ShieldCheckIcon(size = 18.dp, color = TaglineText)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = uiState.privacyNoticeText,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
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
fun AudioWaveformVisualizer() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val barWidth = 4.5.dp.toPx()
        val spacing = 5.dp.toPx()

        // Fixed spectrum heights pattern
        val barHeights = listOf(
            0.20f, 0.35f, 0.50f, 0.30f, 0.65f, 0.85f, 0.45f, 0.70f, 0.95f, 0.60f,
            0.75f, 1.00f, 0.80f, 0.65f, 0.90f, 0.70f, 0.85f, 0.60f, 0.95f, 0.75f,
            0.50f, 0.80f, 0.60f, 0.40f, 0.55f, 0.30f, 0.45f, 0.25f
        )

        val totalBarsWidth = (barHeights.size * barWidth) + ((barHeights.size - 1) * spacing)
        val startX = (w - totalBarsWidth) / 2

        barHeights.forEachIndexed { index, relativeHeight ->
            val x = startX + index * (barWidth + spacing)
            val barH = h * relativeHeight
            val y = (h - barH) / 2

            // Colors transition from purple to bright green across spectrum
            val barColor = if (index < 16) AccentPurple else GreenNode

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barH),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}

@Composable
fun QuestionMarkCircleIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Circle
        drawCircle(
            color = color,
            radius = w * 0.45f,
            center = Offset(w / 2, h / 2),
            style = Stroke(width = strokeWidth)
        )

        // Question mark path
        drawPath(
            path = Path().apply {
                moveTo(w * 0.38f, h * 0.38f)
                quadraticTo(w * 0.50f, h * 0.22f, w * 0.62f, h * 0.38f)
                quadraticTo(w * 0.62f, h * 0.52f, w * 0.50f, h * 0.58f)
                lineTo(w * 0.50f, h * 0.65f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )

        // Dot
        drawCircle(
            color = color,
            radius = strokeWidth * 0.75f,
            center = Offset(w / 2, h * 0.75f)
        )
    }
}

@Composable
fun RestartIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Circular arrow
        drawPath(
            path = Path().apply {
                moveTo(w * 0.75f, h * 0.35f)
                quadraticTo(w * 0.50f, h * 0.15f, w * 0.25f, h * 0.40f)
                quadraticTo(w * 0.10f, h * 0.65f, w * 0.35f, h * 0.82f)
                quadraticTo(w * 0.70f, h * 0.90f, w * 0.82f, h * 0.60f)

                // Arrow head
                moveTo(w * 0.60f, h * 0.35f)
                lineTo(w * 0.78f, h * 0.35f)
                lineTo(w * 0.78f, h * 0.18f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun PauseIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 2.5.dp.toPx()

        // Two vertical bars
        drawLine(
            color = color,
            start = Offset(w * 0.38f, h * 0.28f),
            end = Offset(w * 0.38f, h * 0.72f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = Offset(w * 0.62f, h * 0.28f),
            end = Offset(w * 0.62f, h * 0.72f),
            strokeWidth = strokeWidth
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SpeechStudioScreenPreview() {
    AISkillsMirrorTheme {
        SpeechStudioScreen(
            viewModel = SpeechStudioViewModel()
        )
    }
}
