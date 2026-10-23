package com.simats.aiskillmirror.ui.assess

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.profile.BriefcaseIcon
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun SkillSelectionScreen(
    viewModel: SkillSelectionViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onStartAssessmentClick: () -> Unit = {},
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
            // Scrollable Body Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Top Bar with Back Arrow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                Spacer(modifier = Modifier.height(24.dp))

                // Tagline & Headlines
                Text(
                    text = uiState.tagline,
                    color = TaglineText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = uiState.headline,
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = uiState.description,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Skill Category Cards List
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    uiState.skills.forEach { skill ->
                        SkillCategoryCard(
                            skill = skill,
                            isSelected = skill.id == uiState.selectedSkillId,
                            onSelect = { viewModel.onSelectSkill(skill.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Assessment Meta Info Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ClockIcon(size = 16.dp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.assessmentMetaText,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Start Assessment Primary Button
                Button(
                    onClick = {
                        viewModel.onStartAssessmentClick()
                        onStartAssessmentClick()
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
                                text = "Start assessment",
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
                selectedTab = DashboardTab.ASSESS,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
fun SkillCategoryCard(
    skill: SkillCategory,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val cardBackground = if (isSelected) Color(0xFF1D193E) else SurfaceDark
    val borderBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(PrimaryGradientStart, PrimaryGradientEnd))
    } else {
        Brush.linearGradient(listOf(Color(0xFF22293F), Color(0xFF22293F)))
    }
    val scoreColor = if (isSelected) TaglineText else GreenNode

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackground)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onSelect() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon Badge Container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF142B1F)),
                contentAlignment = Alignment.Center
            ) {
                when (skill.iconType) {
                    SkillIconType.COMMUNICATION -> ChatIcon(size = 20.dp, color = GreenNode)
                    SkillIconType.TECHNICAL -> CodeChevronIcon(size = 20.dp, color = GreenNode)
                    SkillIconType.PROBLEM_SOLVING -> LightningIcon(size = 20.dp, color = GreenNode)
                    SkillIconType.INTERVIEW -> BriefcaseIcon(size = 20.dp, color = GreenNode)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skill.name,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = skill.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Last score: ${skill.lastScore}/${skill.maxScore}",
                        color = scoreColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(TaglineText)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.20f, h * 0.25f)
                lineTo(w * 0.80f, h * 0.25f)
                quadraticTo(w * 0.85f, h * 0.25f, w * 0.85f, h * 0.30f)
                lineTo(w * 0.85f, h * 0.65f)
                quadraticTo(w * 0.85f, h * 0.70f, w * 0.80f, h * 0.70f)
                lineTo(w * 0.45f, h * 0.70f)
                lineTo(w * 0.25f, h * 0.85f)
                lineTo(w * 0.25f, h * 0.70f)
                lineTo(w * 0.20f, h * 0.70f)
                quadraticTo(w * 0.15f, h * 0.70f, w * 0.15f, h * 0.65f)
                lineTo(w * 0.15f, h * 0.30f)
                quadraticTo(w * 0.15f, h * 0.25f, w * 0.20f, h * 0.25f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun CodeChevronIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Left chevron <
        drawPath(
            path = Path().apply {
                moveTo(w * 0.40f, h * 0.30f)
                lineTo(w * 0.20f, h * 0.50f)
                lineTo(w * 0.40f, h * 0.70f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )

        // Right chevron >
        drawPath(
            path = Path().apply {
                moveTo(w * 0.60f, h * 0.30f)
                lineTo(w * 0.80f, h * 0.50f)
                lineTo(w * 0.60f, h * 0.70f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun LightningIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.55f, h * 0.15f)
                lineTo(w * 0.25f, h * 0.52f)
                lineTo(w * 0.50f, h * 0.52f)
                lineTo(w * 0.45f, h * 0.85f)
                lineTo(w * 0.75f, h * 0.48f)
                lineTo(w * 0.50f, h * 0.48f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun ClockIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.42f,
            center = Offset(w / 2, h / 2),
            style = Stroke(width = strokeWidth)
        )

        // Clock hands
        drawPath(
            path = Path().apply {
                moveTo(w / 2, h * 0.28f)
                lineTo(w / 2, h / 2)
                lineTo(w * 0.68f, h / 2)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SkillSelectionScreenPreview() {
    AISkillsMirrorTheme {
        SkillSelectionScreen(
            viewModel = SkillSelectionViewModel()
        )
    }
}
