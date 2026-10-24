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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.dashboard.DottedSkillScoreArc
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.results.ShareIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun SpeechAnalysisScreen(
    viewModel: SpeechAnalysisViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onPracticeAgainClick: () -> Unit = {},
    onPlayRecordingClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
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

                    // Share Icon Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.onShareClick()
                                onShareClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        ShareIcon(size = 20.dp, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Hero Speech Score Card
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
                        // Left: Speech Score Arc
                        Box(
                            modifier = Modifier.size(105.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DottedSkillScoreArc(score = uiState.speechScore)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${uiState.speechScore}",
                                    color = TextPrimary,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "SPEECH SCORE",
                                    color = TextSecondary,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Right: Title & Subtitle
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(0xFF132A1B))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = uiState.baselineGainText,
                                    color = GreenNode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = uiState.headlineLine1,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            )
                            Text(
                                text = uiState.headlineLine2,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = uiState.sessionDurationText,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Speech Metrics Breakdown Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.metrics.forEach { metric ->
                            SpeechMetricBarItem(metric = metric)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section: A Closer Listen
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "A closer listen",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Play recording",
                        color = TaglineText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            viewModel.onPlayRecordingClick()
                            onPlayRecordingClick()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Transcript Card Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        // Highlighted Transcript
                        val annotatedString = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = TextPrimary, fontSize = 14.sp)) {
                                append(uiState.transcriptPart1)
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = TaglineText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append(uiState.transcriptHighlightPronunciation)
                            }
                            withStyle(style = SpanStyle(color = TextPrimary, fontSize = 14.sp)) {
                                append(uiState.transcriptPart2)
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFEAB308),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(uiState.transcriptHighlightFiller)
                            }
                            withStyle(style = SpanStyle(color = TextPrimary, fontSize = 14.sp)) {
                                append(uiState.transcriptPart3)
                            }
                        }

                        Text(
                            text = annotatedString,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Legend Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(TaglineText)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Practice pronunciation",
                                color = TaglineText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEAB308))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Filler word",
                                color = Color(0xFFEAB308),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AI Feedback Tip Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        SparkleIcon(size = 18.dp, color = TaglineText)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = uiState.aiTipText,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Button: Practice again
                Button(
                    onClick = {
                        viewModel.onPracticeAgainClick()
                        onPracticeAgainClick()
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
                            // Mic icon
                            MicSimpleIcon(size = 18.dp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Practice again",
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
fun SpeechMetricBarItem(metric: SpeechMetric) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = metric.name,
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${metric.score}/${metric.maxScore}",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color(0xFF20263F))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(metric.score / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100.dp))
                    .background(AccentPurple)
            )
        }
    }
}

@Composable
fun MicSimpleIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.35f, h * 0.30f)
                lineTo(w * 0.35f, h * 0.50f)
                quadraticTo(w * 0.35f, h * 0.65f, w * 0.50f, h * 0.65f)
                quadraticTo(w * 0.65f, h * 0.65f, w * 0.65f, h * 0.50f)
                lineTo(w * 0.65f, h * 0.30f)
                quadraticTo(w * 0.65f, h * 0.15f, w * 0.50f, h * 0.15f)
                quadraticTo(w * 0.35f, h * 0.15f, w * 0.35f, h * 0.30f)

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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SpeechAnalysisScreenPreview() {
    AISkillsMirrorTheme {
        SpeechAnalysisScreen(
            viewModel = SpeechAnalysisViewModel()
        )
    }
}
