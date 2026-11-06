package com.simats.aiskillmirror.ui.notifications

enum class NotificationMainTab {
    NOTIFICATIONS,
    PRACTICE_TIMER
}

enum class NotificationFilterTab {
    ALL,
    LEARNING,
    MILESTONES
}

enum class NotificationIconType {
    BOOK,
    STREAK,
    SPARKLE,
    ALARM,
    MIC,
    CHECK,
    BELL
}

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val timestampText: String,
    val badgeText: String? = null,
    val isGreenBadge: Boolean = false,
    val iconType: NotificationIconType = NotificationIconType.BELL,
    val category: String = "general",
    val isRead: Boolean = false
)

data class NotificationsUiState(
    val mainTab: NotificationMainTab = NotificationMainTab.NOTIFICATIONS,
    val selectedFilterTab: NotificationFilterTab = NotificationFilterTab.ALL,
    val isLoading: Boolean = false,
    val unreadCount: Int = 0,
    val headerTitle: String = "Notifications & Smart Reminders",
    val headerCategory: String = "STAY CONSISTENT",
    
    // Daily Practice Reminder preferences
    val isDailyReminderEnabled: Boolean = true,
    val reminderTimeText: String = "06:00 PM",
    val reminderHour: Int = 18,
    val reminderMinute: Int = 0,
    val reminderNoticeText: String = "A gentle nudge to invest in your Public Speaking skills.",
    val selectedSkill: String = "Public Speaking",
    val selectedFrequency: String = "Daily",
    val selectedDays: Set<String> = setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    
    // Settings Dialog Modal
    val isSettingsDialogOpen: Boolean = false,
    val enabledNotificationTypes: Set<String> = setOf(
        "Daily Practice Reminders",
        "Missed Practice Alerts",
        "Practice Completion",
        "Weekly Updates",
        "AI Coach Recommendations"
    ),
    
    // Practice Countdown Timer State (10, 15, 20, 30 mins)
    val timerSelectedMinutes: Int = 20,
    val remainingSeconds: Int = 20 * 60,
    val totalSeconds: Int = 20 * 60,
    val isTimerRunning: Boolean = false,
    val isTimerPaused: Boolean = false,
    val hasShownFiveMinuteWarning: Boolean = false,
    val showCompletionDialog: Boolean = false,
    val earnedXp: Int = 50,
    
    // Notification History lists
    val todayNotifications: List<NotificationItem> = emptyList(),
    val yesterdayNotifications: List<NotificationItem> = emptyList(),
    val olderNotifications: List<NotificationItem> = emptyList(),
    
    val bannerMessage: String? = null,
    val hasNotificationPermission: Boolean = true
)

sealed interface NotificationsUiEvent {
    data class ShowToast(val message: String) : NotificationsUiEvent
    object NavigateBack : NotificationsUiEvent
    object NavigateToPractice : NotificationsUiEvent
    object RequestPermission : NotificationsUiEvent
}
