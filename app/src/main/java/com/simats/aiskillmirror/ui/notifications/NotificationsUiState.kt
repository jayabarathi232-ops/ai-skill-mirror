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
    CHECK
}

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val timestampText: String,
    val badgeText: String? = null,
    val isGreenBadge: Boolean = false,
    val iconType: NotificationIconType,
    val category: String = "general",
    val isRead: Boolean = false
)

data class NotificationsUiState(
    val mainTab: NotificationMainTab = NotificationMainTab.NOTIFICATIONS,
    val selectedFilterTab: NotificationFilterTab = NotificationFilterTab.ALL,
    val isLoading: Boolean = false,
    val unreadCount: Int = 0,
    val headerTitle: String = "Keep your momentum",
    val headerCategory: String = "REMINDERS & NOTIFICATIONS",
    
    // Reminder preferences
    val isDailyReminderEnabled: Boolean = true,
    val reminderTimeText: String = "07:00 PM",
    val reminderHour: Int = 19,
    val reminderMinute: Int = 0,
    val reminderNoticeText: String = "A gentle nudge to invest in yourself.",
    val selectedSkill: String = "Public Speaking",
    val selectedFrequency: String = "Daily",
    val selectedDays: Set<String> = setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    
    // Notification list categories
    val todayNotifications: List<NotificationItem> = emptyList(),
    val yesterdayNotifications: List<NotificationItem> = emptyList(),
    val olderNotifications: List<NotificationItem> = emptyList(),
    
    // Practice Countdown Timer State
    val timerSelectedMinutes: Int = 20,
    val remainingSeconds: Int = 20 * 60,
    val totalSeconds: Int = 20 * 60,
    val isTimerRunning: Boolean = false,
    val isTimerPaused: Boolean = false,
    val hasShownTwoMinuteWarning: Boolean = false,
    val showCompletionDialog: Boolean = false,
    val earnedXp: Int = 50,
    val bannerMessage: String? = null,
    val hasNotificationPermission: Boolean = true
)

sealed interface NotificationsUiEvent {
    data class ShowToast(val message: String) : NotificationsUiEvent
    object NavigateBack : NotificationsUiEvent
    object RequestPermission : NotificationsUiEvent
}
