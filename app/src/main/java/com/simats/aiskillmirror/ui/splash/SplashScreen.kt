package com.simats.aiskillmirror.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated Premium Splash Screen for AI Skill Mirror
 *
 * Features:
 * - Premium purple & lavender gradient background with ambient light orbs
 * - Central AI Skill Mirror logo enclosed in a rich gradient tile
 * - Dual harmonic orbital ripple rings and revolving satellite particles
 * - Logo pulse & starlight twinkle animations
 * - App title "AI SKILL MIRROR" with tracked typography
 * - Tagline: "Discover Your Skills. Improve Your Future."
 * - Sleek modern indeterminate shimmer loading indicator
 * - 2.5 second automatic timer navigating to Home (if authenticated) or Login
 * - Fully responsive across mobile, tablet, and desktop aspect ratios
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Automatic Navigation Timer (2.5 seconds)
    LaunchedEffect(Unit) {
        delay(2500L)
        if (UserSession.isLoggedIn) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    // AI Pulse & Orbit Animations
    val infiniteTransition = rememberInfiniteTransition(label = "splash_infinite")

    // Outer ripple pulse (expands and fades out)
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_scale"
    )

    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_alpha"
    )

    // Secondary inner harmonic ring
    val harmonicScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "harmonic_scale"
    )

    val harmonicAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "harmonic_alpha"
    )

    // Center logo subtle breathing
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    // Satellite orbital particles rotation angle (0 to 360 deg)
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_angle"
    )

    // Sparkle twinkle alpha
    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_alpha"
    )

    // Shimmer sweep for the loader bar (-1f to 1f)
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    // Luxury Dark Purple & Lavender Gradient Palette
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF150729), // Ultra deep violet midnight
            Color(0xFF260D49), // Deep royal purple
            Color(0xFF3F1675), // Brand purple core
            Color(0xFF240C45), // Rich twilight
            Color(0xFF130625)  // Grounded deep purple base
        )
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth
        val isTablet = screenWidth > 600.dp

        // Ambient glowing decorative background orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top-right lilac ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF9353E2).copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(w * 0.85f, h * 0.15f),
                    radius = w * 0.55f
                ),
                radius = w * 0.55f,
                center = Offset(w * 0.85f, h * 0.15f)
            )

            // Center purple energy aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF7545C8).copy(alpha = 0.28f), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.45f),
                    radius = w * 0.50f
                ),
                radius = w * 0.50f,
                center = Offset(w * 0.5f, h * 0.45f)
            )

            // Bottom-left soft lavender glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFB794F6).copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(w * 0.15f, h * 0.85f),
                    radius = w * 0.60f
                ),
                radius = w * 0.60f,
                center = Offset(w * 0.15f, h * 0.85f)
            )
        }

        // Main Centered Content Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top spacing placeholder for optical balance
            Spacer(modifier = Modifier.height(32.dp))

            // Center Branding Cluster: Animated Logo + App Name + Tagline
            Column(
                modifier = Modifier
                    .widthIn(max = if (isTablet) 520.dp else 380.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ==========================================
                // 1. AI-INSPIRED ANIMATED LOGO
                // ==========================================
                Box(
                    modifier = Modifier
                        .size(if (isTablet) 230.dp else 190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Ripple Pulse Ring
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(rippleScale)
                            .alpha(rippleAlpha)
                    ) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFB794F6), Color(0xFF7545C8).copy(alpha = 0.4f)),
                                center = center,
                                radius = size.minDimension / 2
                            ),
                            style = Stroke(width = 2.5.dp.toPx())
                        )
                    }

                    // Secondary Inner Harmonic Ring
                    Canvas(
                        modifier = Modifier
                            .size(if (isTablet) 180.dp else 150.dp)
                            .scale(harmonicScale)
                            .alpha(harmonicAlpha)
                    ) {
                        drawCircle(
                            color = Color(0xFFE9DDFF),
                            style = Stroke(width = 1.8.dp.toPx())
                        )
                    }

                    // Satellite Orbital Particles (Circling around logo)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2
                        val cy = size.height / 2
                        val orbitRadius = (size.minDimension / 2) * 0.78f

                        // Draw subtle dotted orbital track
                        drawCircle(
                            color = Color(0xFFD8C7F8).copy(alpha = 0.25f),
                            radius = orbitRadius,
                            style = Stroke(width = 1.2.dp.toPx())
                        )

                        // Satellite Node 1 (Luminous Cyan)
                        val rad1 = Math.toRadians(orbitAngle.toDouble())
                        val px1 = cx + (orbitRadius * cos(rad1)).toFloat()
                        val py1 = cy + (orbitRadius * sin(rad1)).toFloat()
                        drawCircle(
                            color = Color(0xFF38BDF8),
                            radius = 4.5.dp.toPx(),
                            center = Offset(px1, py1)
                        )
                        drawCircle(
                            color = Color(0xFF38BDF8).copy(alpha = 0.35f),
                            radius = 9.dp.toPx(),
                            center = Offset(px1, py1)
                        )

                        // Satellite Node 2 (Glowing Lavender, 180 deg offset)
                        val rad2 = Math.toRadians((orbitAngle + 180f).toDouble())
                        val px2 = cx + (orbitRadius * cos(rad2)).toFloat()
                        val py2 = cy + (orbitRadius * sin(rad2)).toFloat()
                        drawCircle(
                            color = Color(0xFFE9DDFF),
                            radius = 3.5.dp.toPx(),
                            center = Offset(px2, py2)
                        )
                    }

                    // Subtle Accent Sparkle Icons
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 14.dp, top = 8.dp)
                                .alpha(sparkleAlpha)
                        ) {
                            SparkleIcon(size = 18.dp, color = Color(0xFFE9DDFF))
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 10.dp)
                                .alpha(1.3f - sparkleAlpha)
                        ) {
                            SparkleIcon(size = 15.dp, color = Color(0xFF38BDF8))
                        }
                    }

                    // Central App Logo Card
                    Box(
                        modifier = Modifier
                            .size(if (isTablet) 116.dp else 98.dp)
                            .scale(logoScale)
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(28.dp),
                                spotColor = Color(0xFF7545C8),
                                ambientColor = Color(0xFF9353E2)
                            )
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF8A3FFC), // Bright vibrant violet
                                        Color(0xFF7545C8), // Brand deep purple
                                        Color(0xFF5B2C9D)  // Deep royal accent
                                    )
                                )
                            )
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.6f),
                                        Color(0xFFE9DDFF).copy(alpha = 0.25f)
                                    )
                                ),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        MirrorLogoIcon(
                            size = if (isTablet) 56.dp else 46.dp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // 2. APP NAME: AI SKILL MIRROR
                // ==========================================
                Text(
                    text = "AI SKILL MIRROR",
                    color = Color.White,
                    fontSize = if (isTablet) 30.sp else 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ==========================================
                // 3. TAGLINE
                // ==========================================
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.09f))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE9DDFF).copy(alpha = 0.22f),
                            shape = RoundedCornerShape(100.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Discover Your Skills. Improve Your Future.",
                        color = Color(0xFFE9DDFF),
                        fontSize = if (isTablet) 14.sp else 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.4.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ==========================================
            // 4. SMOOTH MODERN AI LOADING ANIMATION
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sleek Indeterminate Gradient Shimmer Bar
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = size.width
                        val shimmerWidth = barWidth * 0.5f
                        val startX = (barWidth + shimmerWidth) * (shimmerOffset + 1.2f) / 2.4f - shimmerWidth

                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFE9DDFF),
                                    Color(0xFF9353E2),
                                    Color.White,
                                    Color.Transparent
                                ),
                                startX = startX,
                                endX = startX + shimmerWidth
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "INITIALIZING AI MIRROR",
                    color = Color(0xFFD8C7F8).copy(alpha = 0.75f),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.4.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ====================================================================
// REUSABLE BRAND ICONS & HEADERS (PRESERVED FOR CODEBASE COMPATIBILITY)
// ====================================================================

@Composable
fun TopBarHeader(
    appName: String,
    onBackClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFD6C8F2))
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                BackArrowIcon(size = 18.dp, color = TextPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

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
