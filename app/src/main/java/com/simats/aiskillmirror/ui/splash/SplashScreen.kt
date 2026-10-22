package com.simats.aiskillmirror.ui.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
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
            TopBarHeader(appName = uiState.appTitle)

            Spacer(modifier = Modifier.height(16.dp))

            // Central Graphic (Orbital system)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                OrbitalGraphic()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Content Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Tagline Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = uiState.tagline,
                        color = TaglineText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Headline
                Text(
                    text = uiState.headlineLine1,
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 38.sp
                )
                Text(
                    text = uiState.headlineLine2,
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle Description
                Text(
                    text = uiState.description,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Get Started Primary Gradient Button
                Button(
                    onClick = {
                        viewModel.onGetStartedClick()
                        onNavigateToHome()
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
                                text = uiState.getStartedButtonText,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Footer Text: Already have an account? Log in
                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = TextSecondary, fontSize = 14.sp)) {
                        append(uiState.loginPromptText)
                    }
                    withStyle(
                        style = SpanStyle(
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(uiState.loginLinkText)
                    }
                }

                Text(
                    text = annotatedString,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onLoginClick()
                            onNavigateToLogin()
                        }
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TopBarHeader(appName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(top = 8.dp)
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

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = appName,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OrbitalGraphic() {
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Canvas for Orbit Rings & Soft Glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)

            // Radial background glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CenterCardGradientStart.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width / 2
                ),
                radius = size.width / 2,
                center = center
            )

            // Outer Orbit Ring
            drawCircle(
                color = OrbitRing,
                radius = 125.dp.toPx(),
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // Inner Orbit Ring
            drawCircle(
                color = OrbitRing,
                radius = 85.dp.toPx(),
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )
        }

        // Central Card Icon
        Box(
            modifier = Modifier
                .size(90.dp)
                .shadow(24.dp, RoundedCornerShape(26.dp), spotColor = CenterCardGradientStart)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(CenterCardGradientStart, CenterCardGradientEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            MirrorLogoIcon(size = 42.dp, color = Color.White)
        }

        // Orbit Node 1: Green Dot (Top Left on Inner Ring)
        Box(
            modifier = Modifier
                .offset(x = (-62).dp, y = (-58).dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(GreenNode)
        )

        // Orbit Node 2: Sparkle Badge (Top Right on Outer Ring)
        Box(
            modifier = Modifier
                .offset(x = 68.dp, y = (-72).dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(BadgeBackground)
                .border(1.dp, BadgeBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            SparkleIcon(size = 18.dp, color = TaglineText)
        }

        // Orbit Node 3: Chart Badge (Bottom Left on Outer Ring)
        Box(
            modifier = Modifier
                .offset(x = (-76).dp, y = 58.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(BadgeBackground)
                .border(1.dp, BadgeBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            ChartIcon(size = 18.dp, color = GreenNode)
        }

        // Orbit Node 4: Cyan Dot (Bottom Right on Outer Ring)
        Box(
            modifier = Modifier
                .offset(x = 88.dp, y = 48.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(CyanNode)
        )
    }
}

@Composable
fun MirrorLogoIcon(size: androidx.compose.ui.unit.Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = w * 0.12f

        // Left triangle / chevron pointing left
        drawPath(
            path = Path().apply {
                moveTo(w * 0.45f, h * 0.20f)
                lineTo(w * 0.18f, h * 0.50f)
                lineTo(w * 0.45f, h * 0.80f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )

        // Right triangle / chevron pointing right
        drawPath(
            path = Path().apply {
                moveTo(w * 0.55f, h * 0.20f)
                lineTo(w * 0.82f, h * 0.50f)
                lineTo(w * 0.55f, h * 0.80f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun SparkleIcon(size: androidx.compose.ui.unit.Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val cx = this.size.width / 2
        val cy = this.size.height / 2
        val r = this.size.width / 2

        // 4-point star/sparkle
        val path = Path().apply {
            moveTo(cx, cy - r)
            quadraticTo(cx, cy, cx + r, cy)
            quadraticTo(cx, cy, cx, cy + r)
            quadraticTo(cx, cy, cx - r, cy)
            quadraticTo(cx, cy, cx, cy - r)
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun ChartIcon(size: androidx.compose.ui.unit.Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Trending line graph icon
        drawPath(
            path = Path().apply {
                moveTo(w * 0.15f, h * 0.75f)
                lineTo(w * 0.40f, h * 0.50f)
                lineTo(w * 0.60f, h * 0.62f)
                lineTo(w * 0.85f, h * 0.30f)
                // Arrow head
                moveTo(w * 0.65f, h * 0.30f)
                lineTo(w * 0.85f, h * 0.30f)
                lineTo(w * 0.85f, h * 0.50f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    AISkillsMirrorTheme {
        SplashScreen(
            viewModel = SplashViewModel()
        )
    }
}
