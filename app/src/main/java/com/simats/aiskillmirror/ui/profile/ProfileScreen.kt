package com.simats.aiskillmirror.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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

    LaunchedEffect(Unit) {
        viewModel.loadProfile(context)
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

                // Avatar Box with permanent profile photo & edit badge
                ProfileAvatar(
                    imageUrl = uiState.profilePictureUrl,
                    initials = uiState.initials,
                    size = 92.dp,
                    shape = CircleShape,
                    showEditBadge = true,
                    fontSize = 30,
                    onEditClick = { onEditPersonalInfoClick() },
                    modifier = Modifier.clickable { onEditPersonalInfoClick() }
                )

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
                    fontWeight = FontWeight.Normal
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

                // 3 Stats Metrics Row
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

        // Graduation cap diamond top
        drawPath(
            path = Path().apply {
                moveTo(w * 0.50f, h * 0.20f)
                lineTo(w * 0.88f, h * 0.40f)
                lineTo(w * 0.50f, h * 0.60f)
                lineTo(w * 0.12f, h * 0.40f)
                close()

                // Cap base arc
                moveTo(w * 0.28f, h * 0.48f)
                lineTo(w * 0.28f, h * 0.72f)
                quadraticTo(w * 0.50f, h * 0.85f, w * 0.72f, h * 0.72f)
                lineTo(w * 0.72f, h * 0.48f)

                // Tassel line
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

        // Handle
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

        // Box
        drawPath(
            path = Path().apply {
                moveTo(w * 0.15f, h * 0.30f)
                lineTo(w * 0.85f, h * 0.30f)
                lineTo(w * 0.85f, h * 0.82f)
                lineTo(w * 0.15f, h * 0.82f)
                close()

                // Horizontal line
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
