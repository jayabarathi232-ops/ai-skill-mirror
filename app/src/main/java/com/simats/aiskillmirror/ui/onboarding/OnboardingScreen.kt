package com.simats.aiskillmirror.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.simats.aiskillmirror.ui.splash.ChartIcon
import com.simats.aiskillmirror.ui.splash.MirrorLogoIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onCreateAccountClick: () -> Unit = {},
    onSkipClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo Container
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(CenterCardGradientStart, CenterCardGradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    MirrorLogoIcon(size = 20.dp, color = Color.White)
                }

                // Skip Link
                Text(
                    text = "Skip",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            viewModel.onSkipClick()
                            onSkipClick()
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Central Orbital Graphic (Compact version for onboarding)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f),
                contentAlignment = Alignment.Center
            ) {
                CompactOrbitalGraphic()
            }

            // Headline + Description
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = uiState.headlineLine1,
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                )
                Text(
                    text = uiState.headlineLine2,
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = uiState.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Features Card List Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        uiState.features.forEach { feature ->
                            FeatureRowItem(feature = feature)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Page Indicator Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(PrimaryGradientEnd)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(OrbitRing)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(OrbitRing)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Create Account Button
                Button(
                    onClick = {
                        viewModel.onCreateAccountClick()
                        onCreateAccountClick()
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
                            // Arrow icon
                            Canvas(modifier = Modifier.size(16.dp)) {
                                val strokeWidth = 2.dp.toPx()
                                drawPath(
                                    path = Path().apply {
                                        moveTo(2.dp.toPx(), 8.dp.toPx())
                                        lineTo(14.dp.toPx(), 8.dp.toPx())
                                        moveTo(9.dp.toPx(), 3.dp.toPx())
                                        lineTo(14.dp.toPx(), 8.dp.toPx())
                                        lineTo(9.dp.toPx(), 13.dp.toPx())
                                    },
                                    color = Color.White,
                                    style = Stroke(width = strokeWidth)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.buttonText,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FeatureRowItem(feature: OnboardingFeature) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icon Badge
        val badgeBackground = when (feature.iconType) {
            OnboardingIconType.PROGRESS -> Color(0xFF14291D)
            else -> Color(0xFF221A3E)
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(badgeBackground),
            contentAlignment = Alignment.Center
        ) {
            when (feature.iconType) {
                OnboardingIconType.DISCOVER -> TargetScanIcon(size = 20.dp, color = TaglineText)
                OnboardingIconType.LEARN -> SparkleIcon(size = 20.dp, color = TaglineText)
                OnboardingIconType.PROGRESS -> ChartIcon(size = 20.dp, color = GreenNode)
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = feature.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = feature.subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun TargetScanIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Bracket corners
        val path = Path().apply {
            // Top Left Corner
            moveTo(w * 0.15f, h * 0.35f)
            lineTo(w * 0.15f, h * 0.15f)
            lineTo(w * 0.35f, h * 0.15f)

            // Top Right Corner
            moveTo(w * 0.65f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.35f)

            // Bottom Right Corner
            moveTo(w * 0.85f, h * 0.65f)
            lineTo(w * 0.85f, h * 0.85f)
            lineTo(w * 0.65f, h * 0.85f)

            // Bottom Left Corner
            moveTo(w * 0.35f, h * 0.85f)
            lineTo(w * 0.15f, h * 0.85f)
            lineTo(w * 0.15f, h * 0.65f)
        }
        drawPath(path = path, color = color, style = Stroke(width = strokeWidth))

        // Center dot/circle
        drawCircle(color = color, radius = w * 0.1f, center = Offset(w / 2, h / 2))
    }
}

@Composable
fun CompactOrbitalGraphic() {
    Box(
        modifier = Modifier.size(230.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)

            // Soft radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CenterCardGradientStart.copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width / 2
                ),
                radius = size.width / 2,
                center = center
            )

            // Outer Ring
            drawCircle(
                color = OrbitRing,
                radius = 100.dp.toPx(),
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // Inner Ring
            drawCircle(
                color = OrbitRing,
                radius = 68.dp.toPx(),
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )
        }

        // Center Card Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .shadow(20.dp, RoundedCornerShape(20.dp), spotColor = CenterCardGradientStart)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(CenterCardGradientStart, CenterCardGradientEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            MirrorLogoIcon(size = 34.dp, color = Color.White)
        }

        // Green Dot Node
        Box(
            modifier = Modifier
                .offset(x = (-48).dp, y = (-46).dp)
                .size(9.dp)
                .clip(CircleShape)
                .background(GreenNode)
        )

        // Sparkle Badge
        Box(
            modifier = Modifier
                .offset(x = 54.dp, y = (-58).dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(BadgeBackground)
                .border(1.dp, BadgeBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            SparkleIcon(size = 14.dp, color = TaglineText)
        }

        // Chart Badge
        Box(
            modifier = Modifier
                .offset(x = (-60).dp, y = 46.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(BadgeBackground)
                .border(1.dp, BadgeBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            ChartIcon(size = 14.dp, color = GreenNode)
        }

        // Cyan Dot Node
        Box(
            modifier = Modifier
                .offset(x = 70.dp, y = 38.dp)
                .size(7.dp)
                .clip(CircleShape)
                .background(CyanNode)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    AISkillsMirrorTheme {
        OnboardingScreen(
            viewModel = OnboardingViewModel()
        )
    }
}
