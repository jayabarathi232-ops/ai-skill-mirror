package com.simats.aiskillmirror.ui.gap

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.data.network.*
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun SkillGapScreen(
    viewModel: SkillGapViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onAskAiCoachClick: (topic: String) -> Unit = {},
    onStartPracticeClick: (subcatId: String) -> Unit = {},
    onOpenRoadmapClick: () -> Unit = {},
    onViewProgressClick: () -> Unit = {},
    onSwitchSkillClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.analysisSuccessToast) {
        uiState.analysisSuccessToast?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = DashboardTab.PROGRESS,
                onTabSelected = onTabSelected
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(TaglineBackground)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        BackArrowIcon(size = 18.dp, color = HeadingPurple)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI SKILL GAP DETECTOR",
                                color = AccentPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE1BEE7))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("DIAGNOSTIC", color = HeadingPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "Performance & Gaps",
                            color = HeadingPurple,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Switch Skill Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightPurple)
                        .clickable { onSwitchSkillClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "All Skills",
                        color = HeadingPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AccentPurple)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Loading Skill Gap Engine...",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                val report = uiState.report

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    // Skill Domain Selector Bar
                    SkillDomainSelectorCard(
                        availableSkills = uiState.availableSkills,
                        selectedSubcatId = report?.subcategoryId ?: "",
                        currentSkillName = report?.subcategoryName ?: "Select Skill",
                        currentLevel = report?.proficiencyLevel ?: "Beginner",
                        onSelectSkill = { subcatId ->
                            viewModel.selectSkill(subcatId)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary "Analyse My Skills" Action Card
                    AnalyseSkillsActionCard(
                        isAnalyzing = uiState.isAnalyzing,
                        skillName = report?.subcategoryName ?: "Skills",
                        onAnalyseClick = {
                            viewModel.analyzeSkills()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content Check: Insufficient Data vs Sufficient Report
                    if (report == null || report.dataSufficiency == "insufficient") {
                        InsufficientDataCard(
                            report = report,
                            onStartPracticeClick = {
                                onStartPracticeClick(report?.subcategoryId ?: "")
                            },
                            onOpenRoadmapClick = onOpenRoadmapClick
                        )
                    } else {
                        // 1. Overall Assessment Summary
                        OverallAssessmentSummaryCard(report = report)

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Skill Performance & Gap Analysis Chart
                        if (report.competencies.isNotEmpty()) {
                            CompetencyGapChartCard(competencies = report.competencies)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // 3. Strong Areas vs Weak Areas Comparison
                        StrengthWeaknessComparisonCard(
                            strongAreas = report.strongAreas,
                            weakAreas = report.weakAreas
                        )

                        // 4. Skills That Need Improvement (if any)
                        if (report.needsImprovement.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            NeedsImprovementCard(needsImprovement = report.needsImprovement)
                        }

                        // 5. Missing Learning Topics (Curriculum Gap)
                        if (report.missingTopics.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            MissingTopicsCard(
                                missingTopics = report.missingTopics,
                                onOpenRoadmapClick = onOpenRoadmapClick
                            )
                        }

                        // 6. Personalized AI Recommendations
                        if (report.recommendations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            RecommendationsCard(
                                recommendations = report.recommendations,
                                onAskAiCoachClick = onAskAiCoachClick,
                                onStartPracticeClick = { onStartPracticeClick(report.subcategoryId) },
                                onOpenRoadmapClick = onOpenRoadmapClick
                            )
                        }

                        // 7. Previous Analysis History
                        if (uiState.history.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            AnalysisHistoryCard(history = uiState.history)
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

// ========================================================
// 1. SKILL DOMAIN SELECTOR CARD
// ========================================================
@Composable
fun SkillDomainSelectorCard(
    availableSkills: List<AvailableSkillItem>,
    selectedSubcatId: String,
    currentSkillName: String,
    currentLevel: String,
    onSelectSkill: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CardBorder, LightPurple)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACTIVE SKILL FOCUS",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentSkillName,
                        color = HeadingPurple,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = currentLevel,
                        color = HeadingPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (availableSkills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableSkills.forEach { skill ->
                        val isSelected = skill.subcategoryId.equals(selectedSubcatId, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) AccentPurple else LightPurple.copy(alpha = 0.5f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AccentPurple else CardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectSkill(skill.subcategoryId) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = skill.subcategoryName,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ========================================================
// 2. PRIMARY "ANALYSE MY SKILLS" ACTION CARD
// ========================================================
@Composable
fun AnalyseSkillsActionCard(
    isAnalyzing: Boolean,
    skillName: String,
    onAnalyseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFD6C8F2))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(PrimaryGradientStart, PrimaryGradientEnd)
                )
            )
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SparkleIcon(size = 20.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Diagnostic Evaluation",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "REAL-TIME",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Runs a deep diagnostic across your recordings, code, quizzes, and completed tasks to detect strong areas, blind spots, and missing skills.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onAnalyseClick,
                enabled = !isAnalyzing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = HeadingPurple,
                    disabledContainerColor = Color.White.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        color = AccentPurple,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Analyzing $skillName...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analyse My Skills",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ========================================================
// 3. INSUFFICIENT DATA CARD (Requirement 9)
// ========================================================
@Composable
fun InsufficientDataCard(
    report: SkillGapReportData?,
    onStartPracticeClick: () -> Unit,
    onOpenRoadmapClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(Color(0xFFFFE082), CardBorder)))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF8E1)),
                contentAlignment = Alignment.Center
            ) {
                Text("📊", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Not Enough Assessment Data",
                color = HeadingPurple,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = report?.aiSummary
                    ?: "To identify genuine strengths and skill gaps without guessing, our AI requires at least one completed practice session, microphone recording, drawing, code, or quiz.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Button(
                onClick = onStartPracticeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🎙️ Complete Daily Practice Session", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onOpenRoadmapClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🗺️ View Skill Learning Roadmap", color = HeadingPurple, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }

            // Missing topics preview
            if (!report?.missingTopics.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Curriculum Topics Ready to Learn:",
                        color = HeadingPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    report!!.missingTopics.take(3).forEach { topic ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• ", color = AccentPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(topic.topic, color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ========================================================
// 4. OVERALL ASSESSMENT SUMMARY CARD
// ========================================================
@Composable
fun OverallAssessmentSummaryCard(report: SkillGapReportData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CardBorder, LightPurple)))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OVERALL ASSESSMENT SUMMARY",
                    color = AccentPurple,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                val severityBg = when (report.gapSeverity) {
                    "Low Gap" -> Color(0xFFE8F5E9)
                    "Moderate Gap" -> Color(0xFFEDE7F6)
                    "Significant Gap" -> Color(0xFFFFF3E0)
                    else -> Color(0xFFFFEBEE)
                }
                val severityColor = when (report.gapSeverity) {
                    "Low Gap" -> GreenNode
                    "Moderate Gap" -> AccentPurple
                    "Significant Gap" -> Color(0xFFE65100)
                    else -> Color(0xFFC62828)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(severityBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(report.gapSeverity, color = severityColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Circular Gauge
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 9.dp.toPx()
                        drawArc(
                            color = CardBorder,
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                        val sweep = (report.overallCompetencyScore / 100f) * 270f
                        drawArc(
                            brush = Brush.sweepGradient(listOf(AccentPurple, PrimaryGradientEnd)),
                            startAngle = 135f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${report.overallCompetencyScore}%",
                            color = HeadingPurple,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Competency", color = TextSecondary, fontSize = 9.sp)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Summary Stats
                Column(modifier = Modifier.weight(1f)) {
                    val gradeText = when {
                        report.overallCompetencyScore >= 90 -> "🏆 Exceptional Mastery"
                        report.overallCompetencyScore >= 80 -> "⭐ High Proficiency"
                        report.overallCompetencyScore >= 70 -> "🎯 Solid Progress"
                        else -> "📈 Growing & Developing"
                    }
                    Text(
                        text = gradeText,
                        color = HeadingPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${report.assessmentCount} assessments analyzed (practical evaluations & quizzes).",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    // Previous comparison banner
                    if (report.previousComparison != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        val p = report.previousComparison
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = p.summary,
                                color = GreenNode,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // AI Narrative Card
            if (report.aiSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LightPurple.copy(alpha = 0.5f))
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "💬 Mira's Diagnostic Insight: ${report.aiSummary}",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ========================================================
// 5. COMPETENCY GAP CHART CARD
// ========================================================
@Composable
fun CompetencyGapChartCard(competencies: List<SkillCompetencyItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CardBorder, LightPurple)))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SKILL PERFORMANCE & GAP DELTAS",
                    color = HeadingPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Target: 100%",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            competencies.forEach { comp ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comp.name,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${comp.score}%",
                                color = HeadingPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (comp.gapDelta > 25) Color(0xFFFFEBEE) else Color(0xFFEDE7F6)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "-${comp.gapDelta}% gap",
                                    color = if (comp.gapDelta > 25) Color(0xFFC62828) else AccentPurple,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CardBorder.copy(alpha = 0.5f))
                    ) {
                        val fraction = comp.score / 100f
                        val barColor = when {
                            comp.score >= 75 -> GreenNode
                            comp.score >= 70 -> AccentPurple
                            else -> Color(0xFFD84315)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(barColor)
                        )
                    }
                }
            }
        }
    }
}

// ========================================================
// 6. STRENGTH VS WEAKNESS COMPARISON CARD
// ========================================================
@Composable
fun StrengthWeaknessComparisonCard(
    strongAreas: List<SkillAreaItem>,
    weakAreas: List<SkillAreaItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CardBorder, LightPurple)))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "STRENGTHS VS WEAK AREAS",
                color = HeadingPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Strong Areas Block
            if (strongAreas.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✓ ", color = GreenNode, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Strong Performance Areas",
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        strongAreas.forEach { sa ->
                            Column(modifier = Modifier.padding(vertical = 3.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = sa.name,
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${sa.score}%",
                                        color = GreenNode,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = sa.detail,
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weak Areas Block
            if (weakAreas.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFEBEE))
                        .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚠️ ", fontSize = 14.sp)
                            Text(
                                text = "Identified Weak Areas & Gaps",
                                color = Color(0xFFC62828),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        weakAreas.forEach { wa ->
                            Column(modifier = Modifier.padding(vertical = 3.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = wa.name,
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${wa.score}% (gap: -${wa.gapDelta}%)",
                                        color = Color(0xFFC62828),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = wa.detail,
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========================================================
// 7. NEEDS IMPROVEMENT CARD
// ========================================================
@Composable
fun NeedsImprovementCard(needsImprovement: List<SkillAreaItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CardBorder, LightPurple)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SKILLS THAT NEED TARGETED REFINEMENT",
                color = HeadingPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            needsImprovement.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(item.detail, color = TextSecondary, fontSize = 10.sp, lineHeight = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${item.score}%", color = AccentPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ========================================================
// 8. MISSING LEARNING TOPICS (Curriculum Gap)
// ========================================================
@Composable
fun MissingTopicsCard(
    missingTopics: List<MissingTopicItem>,
    onOpenRoadmapClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CardBorder, LightPurple)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MISSING CURRICULUM TOPICS",
                    color = HeadingPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${missingTopics.size} to complete",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            missingTopics.forEach { topic ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭕ ", fontSize = 10.sp)
                    Column {
                        Text(topic.topic, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(topic.reason, color = TextSecondary, fontSize = 9.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onOpenRoadmapClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("🗺️ View Full Topic Roadmap", color = HeadingPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ========================================================
// 9. PERSONALIZED AI RECOMMENDATIONS CARD
// ========================================================
@Composable
fun RecommendationsCard(
    recommendations: List<SkillRecommendationItem>,
    onAskAiCoachClick: (String) -> Unit,
    onStartPracticeClick: () -> Unit,
    onOpenRoadmapClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CardBorder, LightPurple)))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SparkleIcon(size = 18.dp, color = AccentPurple)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI RECOMMENDED NEXT STEPS",
                    color = HeadingPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            recommendations.forEach { rec ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LightPurple.copy(alpha = 0.4f))
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = rec.title,
                            color = HeadingPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = rec.description,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                when (rec.actionType) {
                                    "ai_coach" -> onAskAiCoachClick(rec.title)
                                    "daily_training" -> onStartPracticeClick()
                                    "roadmap" -> onOpenRoadmapClick()
                                    else -> onStartPracticeClick()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(rec.actionLabel, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ========================================================
// 10. PREVIOUS ANALYSIS HISTORY CARD
// ========================================================
@Composable
fun AnalysisHistoryCard(history: List<SkillGapHistoryItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CardBorder, LightPurple)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "PREVIOUS DIAGNOSTIC HISTORY",
                color = HeadingPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            history.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(item.dateFormatted, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${item.gapSeverity} · ${item.assessmentCount} tests", color = TextSecondary, fontSize = 10.sp)
                    }
                    Text("${item.score}%", color = HeadingPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
