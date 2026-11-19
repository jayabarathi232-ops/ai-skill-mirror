package com.simats.aiskillmirror.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.data.network.DailyProgressItem
import com.simats.aiskillmirror.data.network.ProfileBadgeItem
import com.simats.aiskillmirror.data.network.ProfileWeeklyProgressData
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.dashboard.RightArrowHeadIcon
import com.simats.aiskillmirror.ui.onboarding.TargetScanIcon
import com.simats.aiskillmirror.ui.register.EmailIcon
import com.simats.aiskillmirror.ui.roadmap.FilterSlidersIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onEditPersonalInfoClick: () -> Unit = {},
    onNextMilestoneClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var isUploadingLocal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile(context)
    }

    // Direct Photo Picker launcher for the camera icon on the avatar
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                val ext = when {
                    mimeType.contains("png", ignoreCase = true) -> "png"
                    mimeType.contains("webp", ignoreCase = true) -> "webp"
                    else -> "jpg"
                }

                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null && bytes.isNotEmpty()) {
                    if (bytes.size > 5 * 1024 * 1024) {
                        Toast.makeText(context, "Image exceeds 5MB size limit", Toast.LENGTH_SHORT).show()
                    } else {
                        val fileName = "profile_${UserSession.userId}_${System.currentTimeMillis()}.$ext"
                        isUploadingLocal = true
                        viewModel.uploadPhotoDirectly(context, bytes, fileName) {
                            isUploadingLocal = false
                            viewModel.loadProfile(context)
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to read selected image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                isUploadingLocal = false
                Toast.makeText(context, "Failed to load image: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
            // Scrollable Profile Body Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row with Title and Settings Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Profile",
                        color = HeadingPurple,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, CircleShape, spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                            .clip(CircleShape)
                            .background(CardBackground)
                            .border(1.dp, CardBorder, CircleShape)
                            .clickable { onSettingsClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        FilterSlidersIcon(size = 18.dp, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 1. Circular Profile Photo with Permanent Edit Camera Badge
                Box(contentAlignment = Alignment.Center) {
                    ProfileAvatar(
                        imageUrl = uiState.profilePictureUrl,
                        initials = uiState.initials,
                        size = 96.dp,
                        shape = CircleShape,
                        showEditBadge = true,
                        fontSize = 32,
                        onEditClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
                    )

                    if (isUploadingLocal || uiState.isUploadingPhoto) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(0x66000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // User Name
                Text(
                    text = uiState.userName,
                    color = HeadingPurple,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // User Bio
                Text(
                    text = uiState.userBio,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Learner Badge Tagline Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = uiState.learnerBadgeText,
                        color = TaglineText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3 Stats Metrics Row (Skill score, Streak, Sessions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Skill score",
                        value = "${uiState.skillScore}"
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Streak",
                        value = "${uiState.dayStreak}d"
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Sessions",
                        value = "${uiState.totalSessions}"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. DEDICATED WEEKLY PROGRESS SECTION
                WeeklyProgressCard(
                    weeklyProgress = uiState.weeklyProgress,
                    selectedDayIndex = uiState.selectedDayIndex,
                    selectedDay = uiState.selectedDay,
                    currentWeekOffset = uiState.currentWeekOffset,
                    isLoading = uiState.isLoadingWeeklyProgress,
                    onDayClick = { idx -> viewModel.selectDay(idx) },
                    onPrevWeekClick = { viewModel.changeWeekOffset(-1) },
                    onNextWeekClick = { viewModel.changeWeekOffset(1) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3. DEDICATED ACHIEVED BADGES & UPCOMING ACHIEVEMENTS
                AchievedBadgesSection(
                    achievedBadges = uiState.achievedBadges,
                    lockedBadges = uiState.lockedBadges
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Personal Info Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Personal info",
                                color = HeadingPurple,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Edit",
                                color = TaglineText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    viewModel.onEditPersonalInfoClick()
                                    onEditPersonalInfoClick()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Item
                        ProfileInfoItem(
                            icon = { EmailIcon(size = 20.dp, color = TaglineText) },
                            title = "Email",
                            subtitle = uiState.email
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFEDE5FA),
                            thickness = 1.dp
                        )

                        // Phone Item
                        ProfileInfoItem(
                            icon = { PhoneIcon(size = 20.dp, color = TaglineText) },
                            title = "Phone",
                            subtitle = uiState.phone
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFEDE5FA),
                            thickness = 1.dp
                        )

                        // Education Item
                        ProfileInfoItem(
                            icon = { EducationIcon(size = 20.dp, color = TaglineText) },
                            title = "Education",
                            subtitle = uiState.education
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFEDE5FA),
                            thickness = 1.dp
                        )

                        // Career Goal Item
                        ProfileInfoItem(
                            icon = { BriefcaseIcon(size = 20.dp, color = TaglineText) },
                            title = "Career goal",
                            subtitle = uiState.careerGoal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Next Milestone Banner Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            viewModel.onNextMilestoneClick()
                            onNextMilestoneClick()
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(TaglineBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                TargetScanIcon(size = 20.dp, color = TaglineText)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = uiState.nextMilestoneTitle,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = uiState.nextMilestoneSubtitle,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        RightArrowHeadIcon(size = 18.dp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Member Since Footer Note
                Text(
                    text = uiState.memberSinceText,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Fixed Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.PROFILE,
                onTabSelected = onTabSelected
            )
        }
    }
}

// ==========================================
// WEEKLY PROGRESS COMPOSABLES
// ==========================================

@Composable
fun WeeklyProgressCard(
    weeklyProgress: ProfileWeeklyProgressData,
    selectedDayIndex: Int = -1,
    selectedDay: DailyProgressItem? = null,
    currentWeekOffset: Int = 0,
    isLoading: Boolean = false,
    onDayClick: (Int) -> Unit = {},
    onPrevWeekClick: () -> Unit = {},
    onNextWeekClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            // Header with Week Range and Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TaglineBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        CalendarClockIcon(size = 20.dp, color = TaglineText)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = com.simats.aiskillmirror.ui.localization.LanguageManager.t("weekly_progress"),
                            color = HeadingPurple,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = weeklyProgress.weekLabel.ifBlank {
                                com.simats.aiskillmirror.ui.localization.LanguageManager.t("mon_to_sun_activity")
                            },
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${weeklyProgress.totalWeeklyMinutes}m total",
                        color = TaglineText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Week Switcher Controls (Prev / Next week to preserve and view previous weekly records)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TaglineBackground.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = com.simats.aiskillmirror.ui.localization.LanguageManager.t("prev_week"),
                    color = AccentPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onPrevWeekClick() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )

                Text(
                    text = if (currentWeekOffset == 0) {
                        com.simats.aiskillmirror.ui.localization.LanguageManager.t("this_week")
                    } else if (currentWeekOffset == -1) {
                        "1 Week Ago"
                    } else {
                        "${-currentWeekOffset} Weeks Ago"
                    },
                    color = HeadingPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = com.simats.aiskillmirror.ui.localization.LanguageManager.t("next_week"),
                    color = if (currentWeekOffset < 0) AccentPurple else TextSecondary.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .then(
                            if (currentWeekOffset < 0) Modifier.clickable { onNextWeekClick() } else Modifier
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Mini summary pills: Practice, Tasks Done, XP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeeklyMiniStatPill(
                    modifier = Modifier.weight(1f),
                    label = com.simats.aiskillmirror.ui.localization.LanguageManager.t("practice"),
                    value = "${weeklyProgress.totalWeeklyMinutes}m"
                )
                WeeklyMiniStatPill(
                    modifier = Modifier.weight(1f),
                    label = com.simats.aiskillmirror.ui.localization.LanguageManager.t("tasks_done"),
                    value = "${weeklyProgress.weeklyCompletedTasks}"
                )
                WeeklyMiniStatPill(
                    modifier = Modifier.weight(1f),
                    label = com.simats.aiskillmirror.ui.localization.LanguageManager.t("xp_earned"),
                    value = "+${weeklyProgress.weeklyXpEarned}"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weekly Goal Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${com.simats.aiskillmirror.ui.localization.LanguageManager.t("weekly_goal")} (${weeklyProgress.totalWeeklyMinutes}/${weeklyProgress.weeklyGoalMinutes} min)",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${weeklyProgress.weeklyGoalPercentage}%",
                        color = TaglineText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (AppTheme.isDark) Color(0xFF2E2744) else Color(0xFFEDE5FA))
                ) {
                    val progressFraction = (weeklyProgress.weeklyGoalPercentage / 100f).coerceIn(0f, 1f)
                    if (progressFraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily practice chart headline with tap hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = com.simats.aiskillmirror.ui.localization.LanguageManager.t("daily_practice_chart"),
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tap a day to view details",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 7-day Interactive Bar Chart for Monday to Sunday
            WeeklyBarChart(
                days = weeklyProgress.days,
                selectedIndex = selectedDayIndex,
                onDayClick = onDayClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Day's Actual Practice Details Section
            val effectiveSelectedDay = if (selectedDayIndex in weeklyProgress.days.indices) {
                weeklyProgress.days[selectedDayIndex]
            } else {
                selectedDay ?: weeklyProgress.days.firstOrNull { d -> d.isToday } ?: weeklyProgress.days.firstOrNull()
            }

            if (effectiveSelectedDay != null) {
                SelectedDayDetailsCard(
                    day = effectiveSelectedDay
                )
            }
        }
    }
}

@Composable
fun WeeklyMiniStatPill(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(TaglineBackground.copy(alpha = 0.4f))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun WeeklyBarChart(
    days: List<DailyProgressItem>,
    selectedIndex: Int = -1,
    onDayClick: (Int) -> Unit = {}
) {
    val safeDays = if (days.size == 7) days else {
        val names = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val fullNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        names.mapIndexed { idx, it -> DailyProgressItem(day = it, dayFull = fullNames[idx], minutes = 0) }
    }

    val maxDayMin = safeDays.maxOfOrNull { it.minutes } ?: 0
    val scaleMax = maxOf(30, maxDayMin)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        safeDays.forEachIndexed { index, item ->
            val isSelected = (index == selectedIndex)
            DayBarColumn(
                item = item,
                scaleMax = scaleMax,
                isSelected = isSelected,
                onClick = { onDayClick(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DayBarColumn(
    item: DailyProgressItem,
    scaleMax: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chartHeight = 84.dp
    val minBarHeight = 6.dp
    val barFraction = if (scaleMax > 0 && item.minutes > 0) {
        (item.minutes.toFloat() / scaleMax).coerceIn(0.08f, 1f)
    } else 0f

    val calculatedHeight = if (item.minutes > 0) {
        maxOf(minBarHeight, chartHeight * barFraction)
    } else {
        minBarHeight
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Daily minute label above bar
        Text(
            text = if (item.minutes > 0) "${item.minutes}m" else "0m",
            color = if (isSelected) AccentPurple else if (item.isToday) TaglineText else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected || item.isToday) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Bar container
        Box(
            modifier = Modifier
                .height(chartHeight)
                .width(20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Track slot outline
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (AppTheme.isDark) Color(0xFF2E2744) else Color(0xFFF4EFFC))
                    .border(
                        width = if (isSelected) 1.5.dp else 0.dp,
                        color = if (isSelected) AccentPurple else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
            )

            // Actual practice fill
            Box(
                modifier = Modifier
                    .height(calculatedHeight)
                    .width(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (item.minutes > 0) {
                            Brush.verticalGradient(
                                listOf(PrimaryGradientEnd, PrimaryGradientStart)
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    if (AppTheme.isDark) Color(0xFF383150) else Color(0xFFE2D6F5),
                                    if (AppTheme.isDark) Color(0xFF383150) else Color(0xFFE2D6F5)
                                )
                            )
                        }
                    )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Day name below bar
        val translatedDay = com.simats.aiskillmirror.ui.localization.LanguageManager.translateDay(item.day)
        Box(
            modifier = if (isSelected) {
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PrimaryGradientStart)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            } else if (item.isToday) {
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TaglineBackground)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            } else Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = translatedDay,
                color = if (isSelected) Color.White else if (item.isToday) HeadingPurple else TextPrimary,
                fontSize = 11.sp,
                fontWeight = if (isSelected || item.isToday) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun SelectedDayDetailsCard(
    day: DailyProgressItem
) {
    val translatedDayFull = com.simats.aiskillmirror.ui.localization.LanguageManager.translateFullDay(day.dayFull)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(TaglineBackground.copy(alpha = 0.5f))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            // Day Header with Today Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$translatedDayFull${if (day.formattedDate.isNotBlank()) ", ${day.formattedDate}" else ""}",
                        color = HeadingPurple,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (day.isToday) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryGradientStart)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = com.simats.aiskillmirror.ui.localization.LanguageManager.t("today_tag"),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = com.simats.aiskillmirror.ui.localization.LanguageManager.t("practice_details"),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (day.hasActivity) {
                // 3 Quick Stat Badges: Practice Time, Completed Tasks, XP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DayStatChip(
                        modifier = Modifier.weight(1f),
                        icon = "⏱️",
                        title = "Practice",
                        value = "${day.minutes} min"
                    )
                    DayStatChip(
                        modifier = Modifier.weight(1f),
                        icon = "✅",
                        title = "Tasks",
                        value = "${day.tasks} done"
                    )
                    DayStatChip(
                        modifier = Modifier.weight(1f),
                        icon = "⭐",
                        title = "Earned",
                        value = "+${day.xp} XP"
                    )
                }

                // Skills Practiced Section
                if (day.skillsPracticed.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = com.simats.aiskillmirror.ui.localization.LanguageManager.t("skills_practiced"),
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        day.skillsPracticed.forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardBackground)
                                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = skill,
                                    color = HeadingPurple,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Detailed Task List
                if (day.tasksDetail.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        day.tasksDetail.take(5).forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CardBackground)
                                    .border(1.dp, CardBorder.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${task.skill} · ${task.durationMinutes}m${if (task.score != null) " · Score: ${task.score}/100" else ""}",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(TaglineBackground)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+${task.xpAwarded} XP",
                                        color = TaglineText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Empty State when this day has no recorded practice activity
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎯", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = com.simats.aiskillmirror.ui.localization.LanguageManager.t("no_activity_today"),
                        color = HeadingPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = com.simats.aiskillmirror.ui.localization.LanguageManager.t("no_activity_desc"),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun DayStatChip(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    value: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
            Text(text = value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// ACHIEVED BADGES & LOCKED BADGES COMPOSABLES
// ==========================================

@Composable
fun AchievedBadgesSection(
    achievedBadges: List<ProfileBadgeItem>,
    lockedBadges: List<ProfileBadgeItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Achieved Badges",
                    color = HeadingPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(TaglineBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${achievedBadges.size} Earned",
                        color = TaglineText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (achievedBadges.isEmpty()) {
            // Clean Empty State when user hasn't met any badge conditions yet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(TaglineBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        BadgeTrophyIcon(size = 24.dp, color = TaglineText)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No Badges Earned Yet",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Complete practice sessions and pass roadmap quizzes to unlock your first achievement!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // List of achieved badges
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                achievedBadges.forEach { badge ->
                    AchievedBadgeCard(badge = badge)
                }
            }
        }

        // Locked Badges / Upcoming Achievements Section
        if (lockedBadges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Achievements",
                    color = HeadingPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFFEDE5FA))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${lockedBadges.size} Locked",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                lockedBadges.forEach { badge ->
                    LockedBadgeCard(badge = badge)
                }
            }
        }
    }
}

@Composable
fun AchievedBadgeCard(badge: ProfileBadgeItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.2.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Badge Icon Box with Gradient
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(4.dp, CircleShape, spotColor = PrimaryGradientStart)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(PrimaryGradientStart, PrimaryGradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                BadgeIconByType(iconType = badge.iconType, size = 22.dp, color = Color.White)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = badge.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!badge.earnedDate.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(TaglineBackground)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Earned ${badge.earnedDate}",
                                color = TaglineText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = badge.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun LockedBadgeCard(badge: ProfileBadgeItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFAF7FD))
            .border(1.dp, Color(0xFFEDE5FA), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Locked Icon Box
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE5FA)),
                contentAlignment = Alignment.Center
            ) {
                LockIcon(size = 18.dp, color = Color(0xFF8E889E))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = badge.name,
                        color = Color(0xFF524D63),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFE5DEEE))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Locked",
                            color = Color(0xFF756D85),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = badge.description,
                    color = Color(0xFF8E889E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun BadgeIconByType(iconType: String, size: Dp, color: Color) {
    when (iconType.uppercase()) {
        "ROCKET" -> BadgeRocketIcon(size = size, color = color)
        "STAR" -> BadgeStarIcon(size = size, color = color)
        "STREAK", "FLAME" -> BadgeFlameIcon(size = size, color = color)
        "BOLT" -> BadgeBoltIcon(size = size, color = color)
        "TIMER" -> BadgeTimerIcon(size = size, color = color)
        "MEDAL" -> BadgeMedalIcon(size = size, color = color)
        else -> BadgeTrophyIcon(size = size, color = color)
    }
}

// ==========================================
// COMMON PROFILE HELPER COMPOSABLES
// ==========================================

@Composable
fun ProfileStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(18.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(vertical = 14.dp, horizontal = 12.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileInfoItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(TaglineBackground),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

// ==========================================
// CUSTOM VECTOR ICONS
// ==========================================

@Composable
fun CalendarClockIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()

        // Calendar body
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.12f, h * 0.22f),
            size = Size(w * 0.76f, h * 0.68f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = stroke)
        )

        // Top bar line
        drawLine(
            color = color,
            start = Offset(w * 0.12f, h * 0.40f),
            end = Offset(w * 0.88f, h * 0.40f),
            strokeWidth = stroke
        )

        // Top binder pins
        drawLine(
            color = color,
            start = Offset(w * 0.32f, h * 0.10f),
            end = Offset(w * 0.32f, h * 0.26f),
            strokeWidth = stroke
        )
        drawLine(
            color = color,
            start = Offset(w * 0.68f, h * 0.10f),
            end = Offset(w * 0.68f, h * 0.26f),
            strokeWidth = stroke
        )

        // Clock circle inside calendar
        drawCircle(
            color = color,
            radius = w * 0.16f,
            center = Offset(w * 0.62f, h * 0.65f),
            style = Stroke(width = stroke)
        )
        // Clock hands
        drawLine(
            color = color,
            start = Offset(w * 0.62f, h * 0.65f),
            end = Offset(w * 0.62f, h * 0.56f),
            strokeWidth = stroke
        )
        drawLine(
            color = color,
            start = Offset(w * 0.62f, h * 0.65f),
            end = Offset(w * 0.70f, h * 0.65f),
            strokeWidth = stroke
        )
    }
}

@Composable
fun LockIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()

        // Shackle loop
        drawPath(
            path = Path().apply {
                moveTo(w * 0.30f, h * 0.45f)
                lineTo(w * 0.30f, h * 0.30f)
                quadraticTo(w * 0.30f, h * 0.12f, w * 0.50f, h * 0.12f)
                quadraticTo(w * 0.70f, h * 0.12f, w * 0.70f, h * 0.30f)
                lineTo(w * 0.70f, h * 0.45f)
            },
            color = color,
            style = Stroke(width = stroke)
        )

        // Body
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.20f, h * 0.42f),
            size = Size(w * 0.60f, h * 0.46f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = stroke)
        )

        // Keyhole dot
        drawCircle(
            color = color,
            radius = w * 0.06f,
            center = Offset(w * 0.50f, h * 0.62f)
        )
    }
}

@Composable
fun BadgeTrophyIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()

        // Cup
        drawPath(
            path = Path().apply {
                moveTo(w * 0.28f, h * 0.18f)
                lineTo(w * 0.72f, h * 0.18f)
                lineTo(w * 0.68f, h * 0.52f)
                quadraticTo(w * 0.50f, h * 0.68f, w * 0.32f, h * 0.52f)
                close()
            },
            color = color,
            style = Stroke(width = stroke)
        )

        // Handles
        drawPath(
            path = Path().apply {
                moveTo(w * 0.28f, h * 0.24f)
                quadraticTo(w * 0.14f, h * 0.28f, w * 0.16f, h * 0.42f)
                quadraticTo(w * 0.18f, h * 0.50f, w * 0.30f, h * 0.48f)

                moveTo(w * 0.72f, h * 0.24f)
                quadraticTo(w * 0.86f, h * 0.28f, w * 0.84f, h * 0.42f)
                quadraticTo(w * 0.82f, h * 0.50f, w * 0.70f, h * 0.48f)
            },
            color = color,
            style = Stroke(width = stroke)
        )

        // Stem & Base
        drawLine(
            color = color,
            start = Offset(w * 0.50f, h * 0.65f),
            end = Offset(w * 0.50f, h * 0.78f),
            strokeWidth = stroke
        )
        drawLine(
            color = color,
            start = Offset(w * 0.32f, h * 0.80f),
            end = Offset(w * 0.68f, h * 0.80f),
            strokeWidth = stroke
        )
    }
}

@Composable
fun BadgeRocketIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()

        // Rocket body
        drawPath(
            path = Path().apply {
                moveTo(w * 0.50f, h * 0.12f)
                quadraticTo(w * 0.74f, h * 0.30f, w * 0.66f, h * 0.65f)
                lineTo(w * 0.34f, h * 0.65f)
                quadraticTo(w * 0.26f, h * 0.30f, w * 0.50f, h * 0.12f)
                close()
            },
            color = color,
            style = Stroke(width = stroke)
        )

        // Porch window
        drawCircle(
            color = color,
            radius = w * 0.08f,
            center = Offset(w * 0.50f, h * 0.38f),
            style = Stroke(width = stroke)
        )

        // Side fins
        drawLine(
            color = color,
            start = Offset(w * 0.34f, h * 0.58f),
            end = Offset(w * 0.18f, h * 0.72f),
            strokeWidth = stroke
        )
        drawLine(
            color = color,
            start = Offset(w * 0.66f, h * 0.58f),
            end = Offset(w * 0.82f, h * 0.72f),
            strokeWidth = stroke
        )

        // Engine flame
        drawPath(
            path = Path().apply {
                moveTo(w * 0.42f, h * 0.66f)
                lineTo(w * 0.50f, h * 0.86f)
                lineTo(w * 0.58f, h * 0.66f)
            },
            color = color,
            style = Stroke(width = stroke)
        )
    }
}

@Composable
fun BadgeStarIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.50f, h * 0.14f)
            lineTo(w * 0.61f, h * 0.38f)
            lineTo(w * 0.87f, h * 0.39f)
            lineTo(w * 0.66f, h * 0.56f)
            lineTo(w * 0.74f, h * 0.82f)
            lineTo(w * 0.50f, h * 0.66f)
            lineTo(w * 0.26f, h * 0.82f)
            lineTo(w * 0.34f, h * 0.56f)
            lineTo(w * 0.13f, h * 0.39f)
            lineTo(w * 0.39f, h * 0.38f)
            close()
        }
        drawPath(path = path, color = color, style = Stroke(width = stroke))
    }
}

@Composable
fun BadgeFlameIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.50f, h * 0.12f)
                quadraticTo(w * 0.78f, h * 0.35f, w * 0.76f, h * 0.62f)
                quadraticTo(w * 0.74f, h * 0.86f, w * 0.50f, h * 0.88f)
                quadraticTo(w * 0.24f, h * 0.86f, w * 0.24f, h * 0.62f)
                quadraticTo(w * 0.24f, h * 0.45f, w * 0.38f, h * 0.35f)
                quadraticTo(w * 0.34f, h * 0.52f, w * 0.44f, h * 0.50f)
                quadraticTo(w * 0.46f, h * 0.32f, w * 0.50f, h * 0.12f)
                close()
            },
            color = color,
            style = Stroke(width = stroke)
        )
    }
}

@Composable
fun BadgeBoltIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.56f, h * 0.12f)
                lineTo(w * 0.28f, h * 0.50f)
                lineTo(w * 0.50f, h * 0.50f)
                lineTo(w * 0.42f, h * 0.88f)
                lineTo(w * 0.74f, h * 0.46f)
                lineTo(w * 0.52f, h * 0.46f)
                close()
            },
            color = color,
            style = Stroke(width = stroke)
        )
    }
}

@Composable
fun BadgeTimerIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()

        // Clock face
        drawCircle(
            color = color,
            radius = w * 0.34f,
            center = Offset(w * 0.50f, h * 0.56f),
            style = Stroke(width = stroke)
        )

        // Top button
        drawLine(
            color = color,
            start = Offset(w * 0.42f, h * 0.14f),
            end = Offset(w * 0.58f, h * 0.14f),
            strokeWidth = stroke
        )
        drawLine(
            color = color,
            start = Offset(w * 0.50f, h * 0.14f),
            end = Offset(w * 0.50f, h * 0.22f),
            strokeWidth = stroke
        )

        // Clock hands
        drawLine(
            color = color,
            start = Offset(w * 0.50f, h * 0.56f),
            end = Offset(w * 0.50f, h * 0.36f),
            strokeWidth = stroke
        )
        drawLine(
            color = color,
            start = Offset(w * 0.50f, h * 0.56f),
            end = Offset(w * 0.65f, h * 0.56f),
            strokeWidth = stroke
        )
    }
}

@Composable
fun BadgeMedalIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()

        // Ribbons
        drawPath(
            path = Path().apply {
                moveTo(w * 0.32f, h * 0.12f)
                lineTo(w * 0.44f, h * 0.45f)
                lineTo(w * 0.24f, h * 0.45f)
                close()

                moveTo(w * 0.68f, h * 0.12f)
                lineTo(w * 0.56f, h * 0.45f)
                lineTo(w * 0.76f, h * 0.45f)
                close()
            },
            color = color,
            style = Stroke(width = stroke)
        )

        // Medal circle
        drawCircle(
            color = color,
            radius = w * 0.25f,
            center = Offset(w * 0.50f, h * 0.65f),
            style = Stroke(width = stroke)
        )

        // Star inside medal
        drawCircle(
            color = color,
            radius = w * 0.08f,
            center = Offset(w * 0.50f, h * 0.65f)
        )
    }
}

@Composable
fun PhoneIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.28f, h * 0.15f)
                lineTo(w * 0.72f, h * 0.15f)
                lineTo(w * 0.72f, h * 0.85f)
                lineTo(w * 0.28f, h * 0.85f)
                close()

                moveTo(w * 0.45f, h * 0.75f)
                lineTo(w * 0.55f, h * 0.75f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun EducationIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.50f, h * 0.20f)
                lineTo(w * 0.88f, h * 0.40f)
                lineTo(w * 0.50f, h * 0.60f)
                lineTo(w * 0.12f, h * 0.40f)
                close()

                moveTo(w * 0.28f, h * 0.48f)
                lineTo(w * 0.28f, h * 0.72f)
                quadraticTo(w * 0.50f, h * 0.85f, w * 0.72f, h * 0.72f)
                lineTo(w * 0.72f, h * 0.48f)

                moveTo(w * 0.82f, h * 0.43f)
                lineTo(w * 0.82f, h * 0.75f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun BriefcaseIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.35f, h * 0.30f)
                lineTo(w * 0.35f, h * 0.20f)
                lineTo(w * 0.65f, h * 0.20f)
                lineTo(w * 0.65f, h * 0.30f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )

        drawPath(
            path = Path().apply {
                moveTo(w * 0.15f, h * 0.30f)
                lineTo(w * 0.85f, h * 0.30f)
                lineTo(w * 0.85f, h * 0.82f)
                lineTo(w * 0.15f, h * 0.82f)
                close()

                moveTo(w * 0.15f, h * 0.55f)
                lineTo(w * 0.85f, h * 0.55f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    AISkillsMirrorTheme {
        ProfileScreen(
            viewModel = ProfileViewModel()
        )
    }
}