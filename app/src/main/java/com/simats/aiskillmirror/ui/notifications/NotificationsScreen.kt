package com.simats.aiskillmirror.ui.notifications

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.theme.*
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onRequestPermission: () -> Unit = {},
    onMarkAllAsReadClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onStartPracticeClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.checkPermission(context)
        viewModel.events.collect { event ->
            when (event) {
                is NotificationsUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is NotificationsUiEvent.NavigateBack -> onBackClick()
                is NotificationsUiEvent.NavigateToPractice -> onStartPracticeClick()
                is NotificationsUiEvent.RequestPermission -> onRequestPermission()
            }
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Notifications",
                            color = HeadingPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (uiState.unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentPurple)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${uiState.unreadCount} unread",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackClick() }) {
                        BackArrowIcon(size = 20.dp, color = HeadingPurple)
                    }
                },
                actions = {
                    // Settings icon button
                    IconButton(onClick = {
                        viewModel.openSettingsDialog()
                        onSettingsClick()
                    }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LightPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            GearIcon(size = 18.dp, color = AccentPurple)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle and quick status
            Text(
                text = "Stay updated with your Public Speaking learning journey",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: "Mark all as read" & "Enable alerts" (matches screenshot requirement)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.onMarkAllAsRead()
                        onMarkAllAsReadClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPurple,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Mark all as read",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = {
                        viewModel.checkPermission(context)
                        if (!uiState.hasNotificationPermission) {
                            onRequestPermission()
                        } else {
                            Toast.makeText(context, "Notifications are already enabled!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CardBackground,
                        contentColor = if (uiState.hasNotificationPermission) GreenNode else AccentPurple
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (uiState.hasNotificationPermission) GreenNode.copy(alpha = 0.5f) else CardBorder
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (uiState.hasNotificationPermission) {
                            CheckmarkIcon(size = 14.dp, color = GreenNode)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Alerts active",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GreenNode
                            )
                        } else {
                            Text(
                                text = "Enable alerts",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentPurple
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Segmented Mode Tabs: Notifications vs Practice Timer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightPurple.copy(alpha = 0.6f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabChip(
                    text = "Notifications & Reminders",
                    isSelected = uiState.mainTab == NotificationMainTab.NOTIFICATIONS,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onSelectMainTab(NotificationMainTab.NOTIFICATIONS) }
                )
                TabChip(
                    text = "Practice Timer (${uiState.timerSelectedMinutes}m)",
                    isSelected = uiState.mainTab == NotificationMainTab.PRACTICE_TIMER,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onSelectMainTab(NotificationMainTab.PRACTICE_TIMER) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Content
            when (uiState.mainTab) {
                NotificationMainTab.NOTIFICATIONS -> {
                    NotificationsTabContent(
                        uiState = uiState,
                        viewModel = viewModel,
                        context = context,
                        onStartPracticeClick = onStartPracticeClick
                    )
                }
                NotificationMainTab.PRACTICE_TIMER -> {
                    PracticeTimerTabContent(
                        uiState = uiState,
                        viewModel = viewModel,
                        context = context,
                        onStartPracticeClick = onStartPracticeClick
                    )
                }
            }
        }
    }

    // Settings Dialog Modal
    if (uiState.isSettingsDialogOpen) {
        NotificationSettingsDialog(
            uiState = uiState,
            viewModel = viewModel,
            context = context,
            onDismiss = { viewModel.closeSettingsDialog() }
        )
    }

    // Practice Completion Modal Dialog
    if (uiState.showCompletionDialog) {
        PracticeCompletionDialog(
            uiState = uiState,
            onDismiss = { viewModel.dismissCompletionDialog() },
            onContinue = {
                viewModel.dismissCompletionDialog()
                onStartPracticeClick()
            }
        )
    }
}

// -------------------------------------------------------------
// NOTIFICATIONS & REMINDERS TAB CONTENT
// -------------------------------------------------------------
@Composable
private fun NotificationsTabContent(
    uiState: NotificationsUiState,
    viewModel: NotificationsViewModel,
    context: android.content.Context,
    onStartPracticeClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1. DAILY PRACTICE REMINDER CARD
        DailyPracticeReminderCard(
            uiState = uiState,
            viewModel = viewModel,
            context = context,
            onStartPracticeClick = onStartPracticeClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. SMART NOTIFICATION ACTION PREVIEWS (Send/Simulate exact required alerts)
        SmartAlertsQuickBar(
            viewModel = viewModel,
            context = context
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 3. NOTIFICATION HISTORY SECTION
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent notifications",
                color = HeadingPurple,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            if (uiState.todayNotifications.isNotEmpty() || uiState.yesterdayNotifications.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.onClearAllNotifications() },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Clear all",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips: All, Learning, Milestones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterBadgeChip(
                text = "All",
                isSelected = uiState.selectedFilterTab == NotificationFilterTab.ALL,
                onClick = { viewModel.onSelectFilterTab(NotificationFilterTab.ALL) }
            )
            FilterBadgeChip(
                text = "Learning",
                isSelected = uiState.selectedFilterTab == NotificationFilterTab.LEARNING,
                onClick = { viewModel.onSelectFilterTab(NotificationFilterTab.LEARNING) }
            )
            FilterBadgeChip(
                text = "Milestones",
                isSelected = uiState.selectedFilterTab == NotificationFilterTab.MILESTONES,
                onClick = { viewModel.onSelectFilterTab(NotificationFilterTab.MILESTONES) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // History items
        val allItems = uiState.todayNotifications + uiState.yesterdayNotifications + uiState.olderNotifications
        val filteredItems = when (uiState.selectedFilterTab) {
            NotificationFilterTab.ALL -> allItems
            NotificationFilterTab.LEARNING -> allItems.filter { it.category.equals("LEARNING", ignoreCase = true) || it.category.equals("PRACTICE", ignoreCase = true) }
            NotificationFilterTab.MILESTONES -> allItems.filter { it.category.equals("MILESTONES", ignoreCase = true) }
        }

        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(LightPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        BellVectorIcon(size = 24.dp, color = AccentPurple)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No notifications right now",
                        color = HeadingPurple,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "You're all caught up with your practice schedule!",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredItems.forEach { item ->
                    NotificationHistoryCard(
                        item = item,
                        onClick = { viewModel.markAsRead(item.id) },
                        onDeleteClick = { viewModel.deleteNotification(item.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------
// 1. DAILY PRACTICE REMINDER CARD
// -------------------------------------------------------------
@Composable
private fun DailyPracticeReminderCard(
    uiState: NotificationsUiState,
    viewModel: NotificationsViewModel,
    context: android.content.Context,
    onStartPracticeClick: () -> Unit
) {
    val quickTimes = listOf("07:00 AM", "09:00 AM", "01:00 PM", "06:00 PM", "08:00 PM")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with toggle switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(LightPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        ClockIcon(size = 20.dp, color = AccentPurple)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Daily Practice Reminder",
                            color = HeadingPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (uiState.isDailyReminderEnabled) "Scheduled for ${uiState.reminderTimeText}" else "Reminders paused",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Switch(
                    checked = uiState.isDailyReminderEnabled,
                    onCheckedChange = { viewModel.onToggleDailyReminder(it, context) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentPurple,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = LightPurple
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic Reminder Message Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightPurple.copy(alpha = 0.5f))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(text = "📢", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "It's ${uiState.reminderTimeText}! Time for your ${uiState.selectedSkill} practice.",
                            color = HeadingPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Daily reminder at your selected time keeps your 5-day streak active.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Select Time Chips
            Text(
                text = "Select daily reminder time:",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickTimes.forEach { timeStr ->
                    val isSelected = uiState.reminderTimeText.equals(timeStr, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) AccentPurple else LightPurple.copy(alpha = 0.5f))
                            .clickable {
                                val parts = timeStr.split(" ", ":")
                                var h = parts[0].toIntOrNull() ?: 18
                                val m = parts[1].toIntOrNull() ?: 0
                                val amPm = parts[2]
                                if (amPm.equals("PM", ignoreCase = true) && h < 12) h += 12
                                if (amPm.equals("AM", ignoreCase = true) && h == 12) h = 0
                                viewModel.onSetReminderTime(h, m, context)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = timeStr,
                            color = if (isSelected) Color.White else HeadingPurple,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // Custom Time Picker button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LightPurple)
                        .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            val calendar = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    viewModel.onSetReminderTime(hourOfDay, minute, context)
                                },
                                uiState.reminderHour,
                                uiState.reminderMinute,
                                false
                            ).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Custom ⏱",
                        color = AccentPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Practice Button (Direct navigation to training)
            Button(
                onClick = {
                    viewModel.onStartPracticeClick()
                    onStartPracticeClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    PlayTriangleIcon(size = 14.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Practice",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. SMART ALERTS QUICK ACTION PREVIEWS
// -------------------------------------------------------------
@Composable
private fun SmartAlertsQuickBar(
    viewModel: NotificationsViewModel,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SparkleVectorIcon(size = 16.dp, color = AccentPurple)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Smart Notifications",
                        color = HeadingPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "Tap to test alert",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal smart alert chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmartAlertPill(
                    icon = "⏰",
                    label = "Daily Reminder",
                    onClick = { viewModel.triggerSmartNotification(context, "DAILY_PRACTICE") }
                )
                SmartAlertPill(
                    icon = "⚠️",
                    label = "Missed Practice",
                    onClick = { viewModel.triggerSmartNotification(context, "MISSED_PRACTICE") }
                )
                SmartAlertPill(
                    icon = "🏆",
                    label = "Practice Completed",
                    onClick = { viewModel.triggerSmartNotification(context, "PRACTICE_COMPLETED") }
                )
                SmartAlertPill(
                    icon = "📊",
                    label = "Weekly Progress",
                    onClick = { viewModel.triggerSmartNotification(context, "WEEKLY_PROGRESS") }
                )
                SmartAlertPill(
                    icon = "💡",
                    label = "AI Coach Tip",
                    onClick = { viewModel.triggerSmartNotification(context, "AI_COACH_TIP") }
                )
                SmartAlertPill(
                    icon = "🎙️",
                    label = "Speech Studio",
                    onClick = { viewModel.triggerSmartNotification(context, "PUBLIC_SPEAKING") }
                )
            }
        }
    }
}

@Composable
private fun SmartAlertPill(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LightPurple.copy(alpha = 0.5f))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = HeadingPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// -------------------------------------------------------------
// 3. NOTIFICATION HISTORY CARD
// -------------------------------------------------------------
@Composable
private fun NotificationHistoryCard(
    item: NotificationItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isRead) CardBackground else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (!item.isRead) AccentPurple.copy(alpha = 0.4f) else CardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category Icon in Light Purple Circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LightPurple),
                contentAlignment = Alignment.Center
            ) {
                when (item.iconType) {
                    NotificationIconType.ALARM -> ClockIcon(size = 18.dp, color = AccentPurple)
                    NotificationIconType.MIC -> MicVectorIcon(size = 18.dp, color = AccentPurple)
                    NotificationIconType.CHECK -> CheckmarkIcon(size = 18.dp, color = GreenNode)
                    NotificationIconType.STREAK -> FireIcon(size = 18.dp, color = Color(0xFFF97316))
                    NotificationIconType.SPARKLE -> SparkleVectorIcon(size = 18.dp, color = AccentPurple)
                    NotificationIconType.BOOK -> BookVectorIcon(size = 18.dp, color = AccentPurple)
                    else -> BellVectorIcon(size = 18.dp, color = AccentPurple)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            color = HeadingPurple,
                            fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!item.isRead) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(AccentPurple)
                            )
                        }
                    }

                    if (!item.badgeText.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (item.isGreenBadge) GreenNode.copy(alpha = 0.15f) else LightPurple)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.badgeText,
                                color = if (item.isGreenBadge) GreenNode else AccentPurple,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.timestampText,
                        color = TextSecondary.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        TrashVectorIcon(size = 13.dp, color = TextSecondary.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PRACTICE TIMER TAB (Remaining Time Alerts: 5-min warning & end)
// -------------------------------------------------------------
@Composable
private fun PracticeTimerTabContent(
    uiState: NotificationsUiState,
    viewModel: NotificationsViewModel,
    context: android.content.Context,
    onStartPracticeClick: () -> Unit
) {
    val durations = listOf(10, 15, 20, 30)
    val minutesLeft = uiState.remainingSeconds / 60
    val secondsLeft = uiState.remainingSeconds % 60
    val formattedTime = String.format(Locale.US, "%02d:%02d", minutesLeft, secondsLeft)
    val progress = if (uiState.totalSeconds > 0) {
        uiState.remainingSeconds.toFloat() / uiState.totalSeconds.toFloat()
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Duration Picker Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Practice Session Duration",
                    color = HeadingPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Select time limit. Smart reminders notify you at 5 minutes and at finish.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durations.forEach { d ->
                        val isSelected = uiState.timerSelectedMinutes == d
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentPurple else LightPurple.copy(alpha = 0.5f))
                                .clickable {
                                    if (!uiState.isTimerRunning) {
                                        viewModel.onSelectDuration(d)
                                    }
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${d}m",
                                color = if (isSelected) Color.White else HeadingPurple,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Timer Dial Display
        Box(
            modifier = Modifier
                .size(220.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(CardBackground)
                .border(2.dp, CardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Circular progress ring
            Canvas(modifier = Modifier.size(190.dp)) {
                val strokeWidth = 10.dp.toPx()
                // Track
                drawCircle(
                    color = LightPurple,
                    radius = (size.minDimension - strokeWidth) / 2,
                    style = Stroke(width = strokeWidth)
                )
                // Remaining progress arc
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(PrimaryGradientStart, PrimaryGradientEnd, PrimaryGradientStart)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formattedTime,
                    color = HeadingPurple,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (uiState.isTimerRunning) "REMAINING TIME" else if (uiState.isTimerPaused) "PAUSED" else "READY",
                    color = AccentPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${minutesLeft}m ${secondsLeft}s left",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Smart Alert Indicator Banners
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(LightPurple.copy(alpha = 0.5f))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(LightPurple),
                contentAlignment = Alignment.Center
            ) {
                BellVectorIcon(size = 16.dp, color = AccentPurple)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "5-Min Warning & Completion Alerts Active",
                    color = HeadingPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "A push notification will alert you at 5 minutes left and when session ends.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Timer Controls: Start, Pause, Resume, Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!uiState.isTimerRunning && !uiState.isTimerPaused) {
                Button(
                    onClick = { viewModel.startTimer(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPurple,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    PlayTriangleIcon(size = 16.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Practice Timer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            } else if (uiState.isTimerRunning && !uiState.isTimerPaused) {
                Button(
                    onClick = { viewModel.pauseTimer() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    PauseBarsIcon(size = 16.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pause", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    ResetArrowIcon(size = 16.dp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset", fontSize = 14.sp, color = TextSecondary)
                }
            } else if (uiState.isTimerPaused) {
                Button(
                    onClick = { viewModel.resumeTimer(context) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    PlayTriangleIcon(size = 16.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Resume", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    ResetArrowIcon(size = 16.dp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset", fontSize = 14.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Button to open Speech Studio while timing
        TextButton(
            onClick = {
                onStartPracticeClick()
            }
        ) {
            Text(
                text = "Open Speech Studio Training →",
                color = AccentPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------
// 4. FUNCTIONAL SETTINGS DIALOG MODAL
// -------------------------------------------------------------
@Composable
private fun NotificationSettingsDialog(
    uiState: NotificationsUiState,
    viewModel: NotificationsViewModel,
    context: android.content.Context,
    onDismiss: () -> Unit
) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val reminderPresets = listOf("07:00 AM", "09:00 AM", "01:00 PM", "06:00 PM", "08:00 PM")
    val durations = listOf(10, 15, 20, 30)

    val notificationTypeList = listOf(
        "Daily Practice Reminders" to "Exact time nudge for Public Speaking",
        "Missed Practice Alerts" to "Notify if not completed by evening",
        "Practice Completion" to "Celebrate daily finished drills & XP",
        "Weekly Updates" to "Weekly progress recap every Monday",
        "AI Coach Recommendations" to "AI-driven personalized speech drills"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .shadow(6.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GearIcon(size = 20.dp, color = AccentPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Notification Settings",
                            color = HeadingPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    TextButton(onClick = onDismiss) {
                        Text("✕", color = TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)

                // 1. Enable/Disable toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Notifications",
                            color = HeadingPurple,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Master switch for alarms & push alerts",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = uiState.isDailyReminderEnabled,
                        onCheckedChange = { viewModel.onToggleDailyReminder(it, context) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentPurple,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = LightPurple
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Reminder Time
                Text(
                    text = "Reminder Time",
                    color = HeadingPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    reminderPresets.forEach { timeStr ->
                        val isSelected = uiState.reminderTimeText.equals(timeStr, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) AccentPurple else LightPurple.copy(alpha = 0.5f))
                                .clickable {
                                    val parts = timeStr.split(" ", ":")
                                    var h = parts[0].toIntOrNull() ?: 18
                                    val m = parts[1].toIntOrNull() ?: 0
                                    val amPm = parts[2]
                                    if (amPm.equals("PM", ignoreCase = true) && h < 12) h += 12
                                    if (amPm.equals("AM", ignoreCase = true) && h == 12) h = 0
                                    viewModel.onSetReminderTime(h, m, context)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = timeStr,
                                color = if (isSelected) Color.White else HeadingPurple,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Reminder Days
                Text(
                    text = "Reminder Days",
                    color = HeadingPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysOfWeek.forEach { day ->
                        val isSelected = uiState.selectedDays.contains(day)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) AccentPurple else LightPurple.copy(alpha = 0.5f))
                                .clickable { viewModel.toggleDay(day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.take(1),
                                color = if (isSelected) Color.White else HeadingPurple,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Default Practice Duration
                Text(
                    text = "Default Practice Duration",
                    color = HeadingPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durations.forEach { d ->
                        val isSelected = uiState.timerSelectedMinutes == d
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AccentPurple else LightPurple.copy(alpha = 0.5f))
                                .clickable { viewModel.onSelectDuration(d) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${d}m",
                                color = if (isSelected) Color.White else HeadingPurple,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Notification Types
                Text(
                    text = "Notification Types",
                    color = HeadingPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    notificationTypeList.forEach { (typeTitle, typeDesc) ->
                        val isChecked = uiState.enabledNotificationTypes.contains(typeTitle)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleNotificationType(typeTitle) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { viewModel.toggleNotificationType(typeTitle) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = AccentPurple,
                                    uncheckedColor = CardBorder
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = typeTitle,
                                    color = HeadingPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = typeDesc,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save Preferences Button
                Button(
                    onClick = { viewModel.saveSettings(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Text(
                        text = "Save Preferences",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PRACTICE COMPLETION DIALOG
// -------------------------------------------------------------
@Composable
private fun PracticeCompletionDialog(
    uiState: NotificationsUiState,
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🎉", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Session Complete!",
                    color = HeadingPurple,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your practice time is over! Great job completing today's session.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightPurple)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "+${uiState.earnedXp} XP Earned",
                        color = AccentPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Text("Go to Speech Studio", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("Close", color = TextSecondary)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPOSABLES
// -------------------------------------------------------------
@Composable
private fun TabChip(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CardBackground else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) HeadingPurple else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun FilterBadgeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) AccentPurple else CardBackground)
            .border(1.dp, if (isSelected) AccentPurple else CardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else HeadingPurple,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// -------------------------------------------------------------
// CLEAN CANVAS-BASED VECTOR ICONS
// -------------------------------------------------------------
@Composable
fun ClockIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        val radius = (w.coerceAtMost(h) - stroke) / 2f
        val center = Offset(w / 2f, h / 2f)

        // Circle
        drawCircle(color = color, radius = radius, center = center, style = Stroke(width = stroke))
        // Hour hand
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x, center.y - radius * 0.55f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        // Minute hand
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x + radius * 0.45f, center.y),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun BellVectorIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            cubicTo(w * 0.35f, h * 0.15f, w * 0.25f, h * 0.35f, w * 0.25f, h * 0.6f)
            lineTo(w * 0.15f, h * 0.75f)
            lineTo(w * 0.85f, h * 0.75f)
            lineTo(w * 0.75f, h * 0.6f)
            cubicTo(w * 0.75f, h * 0.35f, w * 0.65f, h * 0.15f, w * 0.5f, h * 0.15f)
            close()
        }
        drawPath(path, color = color, style = Stroke(width = stroke))
        // Clapper
        drawCircle(
            color = color,
            radius = stroke * 1.2f,
            center = Offset(w * 0.5f, h * 0.85f)
        )
    }
}

@Composable
fun GearIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        val center = Offset(w / 2f, h / 2f)
        val outerRadius = (w.coerceAtMost(h) - stroke * 2) / 2f

        // Outer cogs ring
        drawCircle(
            color = color,
            radius = outerRadius,
            center = center,
            style = Stroke(width = stroke)
        )
        // Inner circle
        drawCircle(
            color = color,
            radius = outerRadius * 0.45f,
            center = center,
            style = Stroke(width = stroke)
        )
        // Spokes
        for (i in 0..3) {
            val angle = Math.toRadians((i * 45).toDouble())
            val dx = (Math.cos(angle) * outerRadius * 1.15f).toFloat()
            val dy = (Math.sin(angle) * outerRadius * 1.15f).toFloat()
            drawLine(
                color = color,
                start = Offset(center.x - dx, center.y - dy),
                end = Offset(center.x + dx, center.y + dy),
                strokeWidth = stroke * 0.8f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun CheckmarkIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.5f)
            lineTo(w * 0.42f, h * 0.75f)
            lineTo(w * 0.85f, h * 0.25f)
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))
    }
}

@Composable
fun PlayTriangleIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.25f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.5f)
            lineTo(w * 0.25f, h * 0.85f)
            close()
        }
        drawPath(path, color = color)
    }
}

@Composable
fun PauseBarsIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = w * 0.22f
        drawLine(
            color = color,
            start = Offset(w * 0.35f, h * 0.2f),
            end = Offset(w * 0.35f, h * 0.8f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.65f, h * 0.2f),
            end = Offset(w * 0.65f, h * 0.8f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ResetArrowIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        val radius = (w - stroke * 2) / 2f
        val center = Offset(w / 2f, h / 2f)
        drawArc(
            color = color,
            startAngle = 45f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        // Arrow head
        val path = Path().apply {
            moveTo(center.x + radius * 0.6f, center.y - radius * 0.7f)
            lineTo(center.x + radius, center.y - radius * 0.7f)
            lineTo(center.x + radius, center.y - radius * 0.3f)
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))
    }
}

@Composable
fun TrashVectorIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        // Lid
        drawLine(
            color = color,
            start = Offset(w * 0.2f, h * 0.25f),
            end = Offset(w * 0.8f, h * 0.25f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        // Can body
        val path = Path().apply {
            moveTo(w * 0.28f, h * 0.25f)
            lineTo(w * 0.32f, h * 0.85f)
            lineTo(w * 0.68f, h * 0.85f)
            lineTo(w * 0.72f, h * 0.25f)
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))
    }
}

@Composable
fun FireIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            cubicTo(w * 0.6f, h * 0.3f, w * 0.85f, h * 0.5f, w * 0.85f, h * 0.7f)
            cubicTo(w * 0.85f, h * 0.9f, w * 0.65f, h * 0.95f, w * 0.5f, h * 0.95f)
            cubicTo(w * 0.35f, h * 0.95f, w * 0.15f, h * 0.9f, w * 0.15f, h * 0.7f)
            cubicTo(w * 0.15f, h * 0.5f, w * 0.4f, h * 0.3f, w * 0.5f, h * 0.1f)
            close()
        }
        drawPath(path, color = color)
    }
}

@Composable
fun SparkleVectorIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w * 0.62f, h * 0.38f)
            lineTo(w, h * 0.5f)
            lineTo(w * 0.62f, h * 0.62f)
            lineTo(w * 0.5f, h)
            lineTo(w * 0.38f, h * 0.62f)
            lineTo(0f, h * 0.5f)
            lineTo(w * 0.38f, h * 0.38f)
            close()
        }
        drawPath(path, color = color)
    }
}

@Composable
fun BookVectorIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        // Left page
        val left = Path().apply {
            moveTo(w * 0.5f, h * 0.25f)
            lineTo(w * 0.2f, h * 0.2f)
            lineTo(w * 0.2f, h * 0.8f)
            lineTo(w * 0.5f, h * 0.85f)
            close()
        }
        // Right page
        val right = Path().apply {
            moveTo(w * 0.5f, h * 0.25f)
            lineTo(w * 0.8f, h * 0.2f)
            lineTo(w * 0.8f, h * 0.8f)
            lineTo(w * 0.5f, h * 0.85f)
            close()
        }
        drawPath(left, color = color, style = Stroke(width = stroke))
        drawPath(right, color = color, style = Stroke(width = stroke))
    }
}

@Composable
fun MicVectorIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        // Mic capsule
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.35f, h * 0.15f),
            size = Size(w * 0.3f, h * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.15f, w * 0.15f),
            style = Stroke(width = stroke)
        )
        // Stand arc
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.25f, h * 0.35f),
            size = Size(w * 0.5f, h * 0.35f),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        // Stand base
        drawLine(
            color = color,
            start = Offset(w * 0.5f, h * 0.7f),
            end = Offset(w * 0.5f, h * 0.88f),
            strokeWidth = stroke
        )
        drawLine(
            color = color,
            start = Offset(w * 0.35f, h * 0.88f),
            end = Offset(w * 0.65f, h * 0.88f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}
