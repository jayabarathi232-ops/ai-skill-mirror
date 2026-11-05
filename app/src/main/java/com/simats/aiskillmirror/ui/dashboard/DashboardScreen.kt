package com.simats.aiskillmirror.ui.dashboard

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.onboarding.TargetScanIcon
import com.simats.aiskillmirror.ui.splash.ChartIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * AI Skills Mirror — Brand New Home Dashboard (Soft Lavender & Pure White Design)
 * 
 * Theme:
 * - Background: Soft lavender (#F5F0FF)
 * - Cards: Pure white (#FFFFFF) with subtle shadows
 * - Primary: Deep purple (#7545C8)
 * - Secondary: Light purple (#E9DDFF)
 * - Text: Dark charcoal (#25213A)
 * - Buttons: Purple gradient (#7545C8 -> #9353E2)
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNotificationsClick: () -> Unit = {},
    onTabSelected: (DashboardTab) -> Unit = {},
    onViewSkillMirrorClick: () -> Unit = {},
    onViewPlanClick: () -> Unit = {},
    onContinueTrainingClick: () -> Unit = {},
    onOpenAiCoachClick: () -> Unit = {},
    onUploadPerformanceClick: (String) -> Unit = {},
    onOpenPublicSpeakingClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DashboardUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    // Animated Score
    val animatedScore by animateIntAsState(
        targetValue = uiState.skillScore,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "overall_score_animation"
    )

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
            // Main Scrollable Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // ==========================================
                // 1. WELCOME BACK MESSAGE AND USER PROFILE
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Info & Avatar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // User Avatar Initial with Violet Gradient Ring
                        Box(
                            modifier = Modifier.size(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color(0xFF7545C8), Color(0xFF9353E2))
                                        )
                                    )
                                    .border(2.dp, Color(0xFFFFFFFF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val initial = uiState.userName.firstOrNull()?.uppercase() ?: "M"
                                Text(
                                    text = initial,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            // Active status green indicator
                            Box(
                                modifier = Modifier
                                    .size(13.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(GreenNode)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.greeting,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "✨", fontSize = 12.sp)
                            }
                            Text(
                                text = uiState.userName,
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.currentDateText,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(TaglineBackground)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "PRO",
                                        color = TaglineText,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Notification Bell Icon Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(2.dp, RoundedCornerShape(14.dp), ambientColor = Color(0xFFD6C8F2), spotColor = Color(0xFFD6C8F2))
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                viewModel.onNotificationsClick()
                                onNotificationsClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        BellIcon(size = 20.dp, color = TextPrimary)
                        // Red Unread Badge
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // 2. OVERALL SKILL PERFORMANCE SCORE (WHITE CARD)
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color(0xFFD6C8F2), spotColor = Color(0xFFD6C8F2))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        // Header: Tagline & Monthly Pill
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(TaglineBackground)
                                    .border(1.dp, Color(0xFFD6C3F7), RoundedCornerShape(100.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    SparkleMiniIcon(size = 12.dp, color = TaglineText)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "MULTI-SKILL MIRROR",
                                        color = TaglineText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            // Monthly Improvement Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(100.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "▲ +${uiState.monthlyPtsGain}% this month",
                                    color = GreenNode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gauge + Score Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular Gauge
                            Box(
                                modifier = Modifier.size(116.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                DottedSkillScoreArc(score = animatedScore)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$animatedScore",
                                        color = TextPrimary,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "OVERALL",
                                        color = TextSecondary,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Score Insights
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.headline,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(TaglineBackground)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "⭐ ${uiState.learnerStatus}",
                                        color = TaglineText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        viewModel.onViewSkillMirrorClick()
                                        onViewSkillMirrorClick()
                                    }
                                ) {
                                    Text(
                                        text = "View Skill Analytics",
                                        color = AccentPurple,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    RightArrowHeadIcon(size = 14.dp, color = AccentPurple)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // ==========================================
                // 3. MY SKILLS CARDS (WHITE CARDS)
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Skills",
                            color = AccentPurple,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Real-time AI assessment across 4 core domains",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(TaglineBackground)
                            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                            .clickable { onTabSelected(DashboardTab.ASSESS) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Assess All",
                            color = TaglineText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal Scrollable Skill Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.mySkills.forEach { skill ->
                        MySkillCard(
                            skill = skill,
                            onAssessClick = {
                                if (skill.id == "speaking") {
                                    onOpenPublicSpeakingClick()
                                } else {
                                    Toast.makeText(context, "Opening ${skill.name} assessment...", Toast.LENGTH_SHORT).show()
                                    onTabSelected(DashboardTab.ASSESS)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // 4. AI SKILL ANALYSIS WITH UPLOAD (WHITE TILES)
                // ==========================================
                Text(
                    text = "AI Multi-Modal Skill Analysis",
                    color = AccentPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Upload performance media for computer vision & acoustic evaluation",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Upload Video Tile
                    MediaUploadTile(
                        modifier = Modifier.weight(1f),
                        emoji = "📹",
                        title = "Upload Video",
                        subtitle = "Motion & Form",
                        badge = "Vision AI",
                        accentColor = AccentPurple,
                        onClick = {
                            onUploadPerformanceClick("video")
                        }
                    )

                    // Upload Image Tile
                    MediaUploadTile(
                        modifier = Modifier.weight(1f),
                        emoji = "🖼️",
                        title = "Upload Image",
                        subtitle = "Pose & Framing",
                        badge = "Pose AI",
                        accentColor = CyanNode,
                        onClick = {
                            onUploadPerformanceClick("photo")
                        }
                    )

                    // Upload Audio Tile
                    MediaUploadTile(
                        modifier = Modifier.weight(1f),
                        emoji = "🎙️",
                        title = "Upload Audio",
                        subtitle = "Pitch & Cadence",
                        badge = "Acoustic AI",
                        accentColor = GreenNode,
                        onClick = {
                            onUploadPerformanceClick("audio")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // 5. AI PERSONAL COACH SECTION (WHITE CARD)
                // ==========================================
                Text(
                    text = "AI Personal Coach",
                    color = AccentPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(22.dp), ambientColor = Color(0xFFD6C8F2), spotColor = Color(0xFFD6C8F2))
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White)
                        .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
                        .clickable {
                            viewModel.onOpenAiCoachClick()
                            onOpenAiCoachClick()
                        }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(TaglineBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                SparkleIcon(size = 24.dp, color = TaglineText)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Coach Aria",
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFDCFCE7))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "ONLINE",
                                            color = GreenNode,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "Adaptive Multi-Skill Mentor",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = uiState.aiCoachSubtitle,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // CTA Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                    )
                                )
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Start AI Coaching Session →",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // 6. RECENT PERFORMANCE HISTORY (WHITE CARDS)
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Performance History",
                        color = AccentPurple,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Last 7 days",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.recentHistory.forEach { item ->
                        PerformanceHistoryCard(item = item)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // 7. WEEKLY PROGRESS CHART (WHITE CARD)
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Weekly Progress",
                            color = AccentPurple,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Practice intensity & hours spent across skills",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "14.5 hrs total",
                        color = TaglineText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Weekly Chart Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp), ambientColor = Color(0xFFD6C8F2), spotColor = Color(0xFFD6C8F2))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGradientEnd)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Daily Practice Hours",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "▲ +18% vs last week",
                                color = GreenNode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        WeeklyBarChart(progressList = uiState.weeklyProgress)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // 8. TODAY'S RECOMMENDED PRACTICE (WHITE CARDS)
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Today's Recommended Practice",
                            color = AccentPurple,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Curated drills tailored to your growth goals",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.recommendedPractice.forEach { practice ->
                        RecommendedPracticeCard(
                            practice = practice,
                            onStartClick = {
                                Toast.makeText(context, "Starting ${practice.title}...", Toast.LENGTH_SHORT).show()
                                onContinueTrainingClick()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ==========================================
            // WHITE BOTTOM NAVIGATION BAR WITH PURPLE ACTIVE ICONS
            // ==========================================
            BottomNavigationBar(
                selectedTab = DashboardTab.HOME,
                onTabSelected = { tab ->
                    viewModel.onSelectTab(tab)
                    onTabSelected(tab)
                }
            )
        }
    }
}

// ========================================================
// REUSABLE DASHBOARD CARDS & WIDGETS (WHITE CARDS + SOFT SHADOWS)
// ========================================================

@Composable
fun MySkillCard(
    skill: SkillItem,
    onAssessClick: () -> Unit
) {
    val accentColor = Color(skill.accentColor)

    Box(
        modifier = Modifier
            .width(165.dp)
            .shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0xFFD6C8F2), spotColor = Color(0xFFD6C8F2))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .clickable { onAssessClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = skill.iconEmoji, fontSize = 18.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${skill.score}%",
                        color = GreenNode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = skill.name,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = skill.category,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Linear Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xFFF1EBFB))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(skill.progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(100.dp))
                        .background(accentColor)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAssessClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = skill.level,
                    color = TaglineText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Assess →",
                    color = AccentPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MediaUploadTile(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    subtitle: String,
    badge: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(18.dp), ambientColor = Color(0xFFD6C8F2), spotColor = Color(0xFFD6C8F2))
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TaglineBackground)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = TaglineText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PerformanceHistoryCard(item: PerformanceHistoryItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = Color(0xFFD6C8F2), spotColor = Color(0xFFD6C8F2))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TaglineBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.skillName,
                        color = TaglineText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.dateText,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = item.activity,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.feedback,
                    color = GreenNode,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Score Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFDCFCE7))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${item.score}%",
                    color = GreenNode,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WeeklyBarChart(progressList: List<WeeklyDayProgress>) {
    val maxHours = 4.0f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        progressList.forEach { dayProg ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                // Hours label
                Text(
                    text = "${dayProg.hours}h",
                    color = if (dayProg.isToday) TaglineText else TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = if (dayProg.isToday) FontWeight.Bold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Bar
                val barHeightFraction = (dayProg.hours / maxHours).coerceIn(0.15f, 1f)
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .fillMaxHeight(barHeightFraction)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(
                            if (dayProg.isToday) {
                                Brush.verticalGradient(
                                    colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                )
                            } else {
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFD4C1F6), Color(0xFFE9DCFD))
                                )
                            }
                        )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Day Label
                Text(
                    text = dayProg.day,
                    color = if (dayProg.isToday) AccentPurple else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = if (dayProg.isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun RecommendedPracticeCard(
    practice: RecommendedPracticeItem,
    onStartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = Color(0xFFD6C8F2), spotColor = Color(0xFFD6C8F2))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TaglineBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(text = practice.iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TaglineBackground)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = practice.skill,
                            color = TaglineText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⏱️ ${practice.durationText}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = practice.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                        )
                    )
                    .clickable { onStartClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Start",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ========================================================
// SHARED EXPORTED COMPONENTS (WHITE BOTTOM NAVIGATION & ICONS)
// ========================================================

@Composable
fun DottedSkillScoreArc(score: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2
        val cy = size.height / 2
        val radius = size.width / 2 - 8.dp.toPx()
        val totalDots = 28
        val activeDots = (totalDots * (score / 100f)).toInt()

        for (i in 0 until totalDots) {
            val angleDeg = 135f + (i * (270f / (totalDots - 1)))
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val dotX = cx + radius * cos(angleRad).toFloat()
            val dotY = cy + radius * sin(angleRad).toFloat()

            val dotColor = if (i < activeDots) AccentPurple else Color(0xFFDCCDF7)
            val dotRadius = if (i < activeDots) 3.5.dp.toPx() else 2.5.dp.toPx()

            drawCircle(
                color = dotColor,
                radius = dotRadius,
                center = Offset(dotX, dotY)
            )
        }
    }
}

/**
 * Clean White Bottom Navigation Bar with Deep Purple Highlight for Active Tab
 */
@Composable
fun BottomNavigationBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), ambientColor = Color(0xFFD6C8F2), spotColor = Color(0xFFD6C8F2))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "Home",
            isSelected = selectedTab == DashboardTab.HOME,
            icon = { color -> HomeNavIcon(size = 20.dp, color = color) },
            onClick = { onTabSelected(DashboardTab.HOME) }
        )
        BottomNavItem(
            label = "Assess",
            isSelected = selectedTab == DashboardTab.ASSESS,
            icon = { color -> TargetScanIcon(size = 20.dp, color = color) },
            onClick = { onTabSelected(DashboardTab.ASSESS) }
        )
        BottomNavItem(
            label = "Learn",
            isSelected = selectedTab == DashboardTab.LEARN,
            icon = { color -> BookNavIcon(size = 20.dp, color = color) },
            onClick = { onTabSelected(DashboardTab.LEARN) }
        )
        BottomNavItem(
            label = "Progress",
            isSelected = selectedTab == DashboardTab.PROGRESS,
            icon = { color -> ChartIcon(size = 20.dp, color = color) },
            onClick = { onTabSelected(DashboardTab.PROGRESS) }
        )
        BottomNavItem(
            label = "Profile",
            isSelected = selectedTab == DashboardTab.PROFILE,
            icon = { color -> ProfileNavIcon(size = 20.dp, color = color) },
            onClick = { onTabSelected(DashboardTab.PROFILE) }
        )
    }
}

@Composable
fun BottomNavItem(
    label: String,
    isSelected: Boolean,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) AccentPurple else Color(0xFF8E88A8)
    val backgroundModifier = if (isSelected) {
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE9DDFF))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    } else {
        Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .then(backgroundModifier)
    ) {
        icon(contentColor)
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun BellIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.30f, h * 0.70f)
                lineTo(w * 0.70f, h * 0.70f)
                lineTo(w * 0.78f, h * 0.55f)
                lineTo(w * 0.70f, h * 0.35f)
                quadraticTo(w * 0.70f, h * 0.15f, w * 0.50f, h * 0.15f)
                quadraticTo(w * 0.30f, h * 0.15f, w * 0.30f, h * 0.35f)
                lineTo(w * 0.22f, h * 0.55f)
                close()

                moveTo(w * 0.40f, h * 0.75f)
                quadraticTo(w * 0.50f, h * 0.90f, w * 0.60f, h * 0.75f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun MicIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Mic capsule
        drawPath(
            path = Path().apply {
                moveTo(w * 0.35f, h * 0.35f)
                lineTo(w * 0.35f, h * 0.50f)
                quadraticTo(w * 0.35f, h * 0.65f, w * 0.50f, h * 0.65f)
                quadraticTo(w * 0.65f, h * 0.65f, w * 0.65f, h * 0.50f)
                lineTo(w * 0.65f, h * 0.35f)
                quadraticTo(w * 0.65f, h * 0.20f, w * 0.50f, h * 0.20f)
                quadraticTo(w * 0.35f, h * 0.20f, w * 0.35f, h * 0.35f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )

        // Mic stand arc
        drawPath(
            path = Path().apply {
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

@Composable
fun HomeNavIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.15f, h * 0.45f)
                lineTo(w * 0.50f, h * 0.15f)
                lineTo(w * 0.85f, h * 0.45f)
                lineTo(w * 0.85f, h * 0.85f)
                lineTo(w * 0.15f, h * 0.85f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun BookNavIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.50f, h * 0.25f)
                quadraticTo(w * 0.25f, h * 0.20f, w * 0.15f, h * 0.28f)
                lineTo(w * 0.15f, h * 0.80f)
                quadraticTo(w * 0.25f, h * 0.75f, w * 0.50f, h * 0.80f)
                quadraticTo(w * 0.75f, h * 0.75f, w * 0.85f, h * 0.80f)
                lineTo(w * 0.85f, h * 0.28f)
                quadraticTo(w * 0.75f, h * 0.20f, w * 0.50f, h * 0.25f)

                moveTo(w * 0.50f, h * 0.25f)
                lineTo(w * 0.50f, h * 0.80f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun ProfileNavIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.22f,
            center = Offset(w / 2, h * 0.35f),
            style = Stroke(width = strokeWidth)
        )

        drawPath(
            path = Path().apply {
                moveTo(w * 0.20f, h * 0.85f)
                quadraticTo(w * 0.20f, h * 0.62f, w * 0.50f, h * 0.62f)
                quadraticTo(w * 0.80f, h * 0.62f, w * 0.80f, h * 0.85f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun RightArrowHeadIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.35f, h * 0.20f)
                lineTo(w * 0.65f, h * 0.50f)
                lineTo(w * 0.35f, h * 0.80f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun SparkleMiniIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.50f, 0f)
            quadraticTo(w * 0.50f, h * 0.50f, w, h * 0.50f)
            quadraticTo(w * 0.50f, h * 0.50f, w * 0.50f, h)
            quadraticTo(w * 0.50f, h * 0.50f, 0f, h * 0.50f)
            quadraticTo(w * 0.50f, h * 0.50f, w * 0.50f, 0f)
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    AISkillsMirrorTheme {
        DashboardScreen(
            viewModel = DashboardViewModel()
        )
    }
}
