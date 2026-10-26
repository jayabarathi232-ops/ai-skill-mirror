package com.simats.aiskillmirror.ui.notifications

enum class NotificationFilterTab {
    ALL,
    LEARNING,
    MILESTONES
}

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val timestampText: String,
    val badgeText: String? = null,
    val isGreenBadge: Boolean = false,
    val iconType: NotificationIconType
)

enum class NotificationIconType {
    BOOK,
    STREAK,
    SPARKLE,
    ALARM
}

data class NotificationsUiState(
    val headerTitle: String = "Keep your momentum",
    val headerCategory: String = "REMINDERS & UPDATES",
    val selectedTab: NotificationFilterTab = NotificationFilterTab.ALL,
    val isDailyReminderEnabled: Boolean = true,
    val reminderTimeText: String = "Every day at 7:00 PM",
    val reminderNoticeText: String = "A gentle nudge to invest in yourself.",
    val todayNotifications: List<NotificationItem> = listOf(
        NotificationItem(
            id = "1",
            title = "Your next 6 minutes matter",
            description = "Finish today's speech practice and complete your 20-minute goal.",
            timestampText = "7:00 PM",
            badgeText = "COMING UP",
            isGreenBadge = false,
            iconType = NotificationIconType.BOOK
        ),
        NotificationItem(
            id = "2",
            title = "A week of progress. All you.",
            description = "You've earned the Week Warrior badge with a 7-day learning streak.",
            timestampText = "8:15 AM",
            badgeText = "MILESTONE",
            isGreenBadge = true,
            iconType = NotificationIconType.STREAK
        )
    ),
    val yesterdayNotifications: List<NotificationItem> = listOf(
        NotificationItem(
            id = "3",
            title = "Your plan got smarter",
            description = "We added project storytelling to your communication practice.",
            timestampText = "Yesterday",
            badgeText = null,
            isGreenBadge = false,
            iconType = NotificationIconType.SPARKLE
        )
    )
)

sealed interface NotificationsUiEvent {
    object MarkAllAsRead : NotificationsUiEvent
    object OpenSettings : NotificationsUiEvent
    object NavigateBack : NotificationsUiEvent
}
