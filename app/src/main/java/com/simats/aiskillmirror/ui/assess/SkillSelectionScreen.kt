package com.simats.aiskillmirror.ui.assess

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.profile.BriefcaseIcon
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun SkillSelectionScreen(
    viewModel: SkillSelectionViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onStartTraining: (mainSkill: String, subcatId: String) -> Unit = { _, _ -> },
    onStartAssessmentClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val categoryScrollState = rememberScrollState()

    val currentCategory = uiState.skills.firstOrNull { it.id == uiState.selectedSkillId }
        ?: uiState.skills.firstOrNull()
        ?: defaultSkillCategoriesCatalog().first()

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
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBackground)
                            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.onBackClick()
                                onBackClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        BackArrowIcon(size = 18.dp, color = HeadingPurple)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = uiState.headerTitle,
                            color = HeadingPurple,
                            fontSize = 22.sp,
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

                Spacer(modifier = Modifier.height(18.dp))

                // Active Training Path Card (if user has active subcategory)
                uiState.activeSavedSubcategory?.let { activeSub ->
                    ActiveTrainingPathCard(
                        activeSub = activeSub,
                        onContinue = {
                            onStartTraining(activeSub.mainSkill, activeSub.subcategoryId)
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Tagline & Headline
                Text(
                    text = uiState.tagline,
                    color = TaglineText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.headline,
                    color = HeadingPurple,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Main Skill Category Horizontal Selector (Music, Photography, Drawing, Coding, Public Speaking)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(categoryScrollState),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.skills.forEach { category ->
                        val isSelected = category.id == uiState.selectedSkillId
                        CategoryChip(
                            category = category,
                            isSelected = isSelected,
                            onClick = { viewModel.selectCategory(category.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Selected Category Title & Subtitle Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currentCategory.iconEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${currentCategory.name} · ${currentCategory.subtitle}",
                                color = HeadingPurple,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentCategory.description,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(TaglineBackground)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${currentCategory.subcategories.size} Paths",
                            color = AccentPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subcategories List for Selected Category
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    currentCategory.subcategories.forEach { subcategory ->
                        val isActive = uiState.activeSavedSubcategory?.subcategoryId == subcategory.id
                        SubcategoryCard(
                            subcategory = subcategory,
                            isActive = isActive,
                            onSelect = {
                                viewModel.openSubcategoryOnboarding(subcategory)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // General AI Assessment Card Option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TaglineBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                SparkleMiniIcon(size = 20.dp, color = AccentPurple)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Skill Mirror Benchmark",
                                    color = HeadingPurple,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "10 AI evaluated questions · 8 mins",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.onStartAssessmentClick()
                                onStartAssessmentClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(text = "Assess", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Fixed Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.ASSESS,
                onTabSelected = onTabSelected
            )
        }

        // Subcategory Onboarding & Learning Sheet Dialog
        if (uiState.isSubcategoryDialogOpen && uiState.selectedSubcategory != null) {
            SubcategoryOnboardingDialog(
                subcategory = uiState.selectedSubcategory!!,
                selectedLevel = uiState.selectedProficiencyLevel,
                selectedGoal = uiState.selectedLearningGoal,
                availableGoals = uiState.availableGoals,
                isLoading = uiState.isLoading,
                onLevelSelected = { viewModel.selectLevel(it) },
                onGoalSelected = { viewModel.selectGoal(it) },
                onStartLearning = {
                    val sub = uiState.selectedSubcategory!!
                    viewModel.startLearning()
                    onStartTraining(sub.mainSkillId, sub.id)
                },
                onDismiss = { viewModel.closeSubcategoryOnboarding() }
            )
        }
    }
}

@Composable
fun ActiveTrainingPathCard(
    activeSub: com.simats.aiskillmirror.data.network.SubcategoryItemData,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground)
            .border(1.5.dp, Brush.horizontalGradient(listOf(PrimaryGradientStart, PrimaryGradientEnd)), RoundedCornerShape(18.dp))
            .clickable { onContinue() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACTIVE TRAINING PATH",
                        color = AccentPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = activeSub.proficiencyLevel,
                        color = AccentPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = activeSub.subcategoryName,
                color = HeadingPurple,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Goal: ${activeSub.learningGoal}",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "⚡ +${activeSub.xpEarned} XP",
                        color = HeadingPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "📚 ${activeSub.lessonsCompleted} Lessons",
                        color = HeadingPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "⏱️ ${activeSub.practiceMinutes} min",
                        color = HeadingPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "Continue Daily Training →",
                    color = AccentPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: SkillCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerBg = if (isSelected) AccentPurple else CardBackground
    val textColor = if (isSelected) Color.White else HeadingPurple
    val borderColor = if (isSelected) AccentPurple else CardBorder

    Box(
        modifier = Modifier
            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(14.dp))
            .background(containerBg)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = category.iconEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category.name,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun SubcategoryCard(
    subcategory: SkillSubcategory,
    isActive: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isActive) AccentPurple else CardBorder
    val borderWidth = if (isActive) 1.5.dp else 1.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isActive) 6.dp else 2.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground)
            .border(borderWidth, borderColor, RoundedCornerShape(18.dp))
            .clickable { onSelect() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(TaglineBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = subcategory.iconEmoji, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = subcategory.name,
                            color = HeadingPurple,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subcategory.tagline,
                            color = AccentPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (isActive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentPurple)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subcategory.description,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Badges & Call to action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF0EBF8))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⏱️ ${subcategory.durationText}",
                            color = HeadingPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE9F9EE))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⚡ ${subcategory.xpText}",
                            color = Color(0xFF059669),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = { onSelect() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) AccentPurple else Color(0xFFF0EBF8)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isActive) "Train Now" else "Select & Train",
                        color = if (isActive) Color.White else AccentPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SubcategoryOnboardingDialog(
    subcategory: SkillSubcategory,
    selectedLevel: String,
    selectedGoal: String,
    availableGoals: List<String>,
    isLoading: Boolean,
    onLevelSelected: (String) -> Unit,
    onGoalSelected: (String) -> Unit,
    onStartLearning: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF7545C8))
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(22.dp)
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.Start
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = subcategory.iconEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = subcategory.name,
                                color = HeadingPurple,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "AI PERSONALIZED ONBOARDING",
                                color = AccentPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0EBF8))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", color = HeadingPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = subcategory.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Proficiency Level Selection
                Text(
                    text = "1. SELECT YOUR PROFICIENCY LEVEL",
                    color = HeadingPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Beginner", "Intermediate", "Advanced").forEach { level ->
                        val isLevelSelected = level == selectedLevel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isLevelSelected) AccentPurple else Color(0xFFF9F6FF))
                                .border(
                                    width = 1.dp,
                                    color = if (isLevelSelected) AccentPurple else CardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onLevelSelected(level) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = level,
                                color = if (isLevelSelected) Color.White else HeadingPurple,
                                fontSize = 12.sp,
                                fontWeight = if (isLevelSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Learning Goal Selection
                Text(
                    text = "2. CHOOSE YOUR LEARNING GOAL",
                    color = HeadingPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableGoals.forEach { goal ->
                        val isGoalSelected = goal == selectedGoal
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isGoalSelected) Color(0xFFF3EBFF) else CardBackground)
                                .border(
                                    width = if (isGoalSelected) 1.5.dp else 1.dp,
                                    color = if (isGoalSelected) AccentPurple else CardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onGoalSelected(goal) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(if (isGoalSelected) AccentPurple else Color.Transparent)
                                        .border(2.dp, if (isGoalSelected) AccentPurple else Color(0xFFC7B8E6), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = goal,
                                    color = HeadingPurple,
                                    fontSize = 13.sp,
                                    fontWeight = if (isGoalSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. AI Generated Practice Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFAF7FF))
                        .border(1.dp, Color(0xFFE5D9F8), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Personalized Training Preview",
                                color = AccentPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Daily Practice: ${subcategory.dailyPracticeTitle}",
                            color = HeadingPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Exercise: ${subcategory.beginnerExercise}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "AI Tip: ${subcategory.aiTip}",
                            color = Color(0xFF6B4EA0),
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Start Learning Button
                Button(
                    onClick = onStartLearning,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGradientStart),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
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
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Start Learning ${subcategory.name}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "→", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SparkleMiniIcon(size: Dp = 16.dp, color: Color = AccentPurple) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            quadraticTo(w * 0.5f, h * 0.5f, w, h * 0.5f)
            quadraticTo(w * 0.5f, h * 0.5f, w * 0.5f, h)
            quadraticTo(w * 0.5f, h * 0.5f, 0f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 0.5f, w * 0.5f, 0f)
            close()
        }
        drawPath(path = path, color = color)
    }
}
