package com.simats.aiskillmirror.ui.notifications

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.simats.aiskillmirror.ui.dashboard.BookNavIcon
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.roadmap.FilterSlidersIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*
import com.simats.aiskillmirror.ui.training.FlameIcon

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onMarkAllAsReadClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
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

                    // Settings Icon Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.onSettingsClick()
                                onSettingsClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        FilterSlidersIcon(size = 18.dp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Notification Filter Segmented Tabs
                NotificationSegmentedTabs(
                    selectedTab = uiState.selectedTab,
                    onSelect = { viewModel.onSelectFilterTab(it) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Daily Learning Time Reminder Toggle Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF181538), Color(0xFF221B4A))
                            )
                        )
                        .border(1.dp, Color(0xFF332B66), RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Column {
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
                                        .background(Color(0xFF2A2252)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AlarmClockIcon(size = 20.dp, color = TaglineText)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "Your daily learning time",
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = uiState.reminderTimeText,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }

                            Switch(
                                checked = uiState.isDailyReminderEnabled,
                                onCheckedChange = { viewModel.onToggleDailyReminder(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryGradientStart,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = SurfaceDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = uiState.reminderNoticeText,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section: Today
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mark all as read",
                        color = TaglineText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            viewModel.onMarkAllAsReadClick()
                            onMarkAllAsReadClick()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Today Notification Items List
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.todayNotifications.forEach { item ->
                        NotificationCardItem(item = item)
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section: Yesterday
                Text(
                    text = "Yesterday",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Yesterday Notification Items List
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.yesterdayNotifications.forEach { item ->
                        NotificationCardItem(item = item)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Fixed Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.HOME,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
fun NotificationCardItem(item: NotificationItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, Color(0xFF22293F), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            if (item.badgeText != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val badgeBg = if (item.isGreenBadge) Color(0xFF132A1B) else Color(0xFF2A2252)
                    val badgeTextColor = if (item.isGreenBadge) GreenNode else TaglineText

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(badgeBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.badgeText,
                            color = badgeTextColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Text(
                        text = item.timestampText,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                val iconBg = if (item.isGreenBadge) Color(0xFF132A1B) else Color(0xFF221A3E)
                val iconColor = if (item.isGreenBadge) GreenNode else TaglineText

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    when (item.iconType) {
                        NotificationIconType.BOOK -> BookNavIcon(size = 20.dp, color = iconColor)
                        NotificationIconType.STREAK -> FlameIcon(size = 20.dp, color = iconColor)
                        NotificationIconType.SPARKLE -> SparkleIcon(size = 20.dp, color = iconColor)
                        NotificationIconType.ALARM -> AlarmClockIcon(size = 20.dp, color = iconColor)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationSegmentedTabs(
    selectedTab: NotificationFilterTab,
    onSelect: (NotificationFilterTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, Color(0xFF22293F), RoundedCornerShape(14.dp))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NotificationTabItem(
                label = "All",
                isSelected = selectedTab == NotificationFilterTab.ALL,
                onClick = { onSelect(NotificationFilterTab.ALL) },
                modifier = Modifier.weight(1f)
            )
            NotificationTabItem(
                label = "Learning",
                isSelected = selectedTab == NotificationFilterTab.LEARNING,
                onClick = { onSelect(NotificationFilterTab.LEARNING) },
                modifier = Modifier.weight(1f)
            )
            NotificationTabItem(
                label = "Milestones",
                isSelected = selectedTab == NotificationFilterTab.MILESTONES,
                onClick = { onSelect(NotificationFilterTab.MILESTONES) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NotificationTabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabBackground = if (isSelected) Color(0xFF1E1A3D) else Color.Transparent
    val borderModifier = if (isSelected) {
        Modifier.border(1.dp, Color(0xFF332B66), RoundedCornerShape(10.dp))
    } else Modifier

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(tabBackground)
            .then(borderModifier)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) TextPrimary else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun AlarmClockIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Clock body
        drawCircle(
            color = color,
            radius = w * 0.38f,
            center = Offset(w / 2, h * 0.55f),
            style = Stroke(width = strokeWidth)
        )

        // Alarm ears
        drawLine(
            color = color,
            start = Offset(w * 0.20f, h * 0.25f),
            end = Offset(w * 0.35f, h * 0.32f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = Offset(w * 0.80f, h * 0.25f),
            end = Offset(w * 0.65f, h * 0.32f),
            strokeWidth = strokeWidth
        )

        // Hands
        drawLine(
            color = color,
            start = Offset(w / 2, h * 0.55f),
            end = Offset(w / 2, h * 0.38f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = Offset(w / 2, h * 0.55f),
            end = Offset(w * 0.65f, h * 0.55f),
            strokeWidth = strokeWidth
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationsScreenPreview() {
    AISkillsMirrorTheme {
        NotificationsScreen(
            viewModel = NotificationsViewModel()
        )
    }
}
