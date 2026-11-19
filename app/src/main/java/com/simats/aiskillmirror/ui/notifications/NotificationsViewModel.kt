package com.simats.aiskillmirror.ui.notifications

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.notifications.SmartNotificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<NotificationsUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var timerJob: Job? = null

    init {
        loadNotifications()
    }

    private fun getUserId(): Int {
        return UserSession.userId
    }

    fun checkPermission(context: Context) {
        val hasPermission = SmartNotificationManager.hasNotificationPermission(context)
        val hasExact = SmartNotificationManager.canScheduleExactAlarms(context)
        _uiState.value = _uiState.value.copy(
            hasNotificationPermission = hasPermission,
            hasExactAlarmPermission = hasExact
        )
    }

    fun initLocalSettings(context: Context) {
        val userId = getUserId()
        val prefs = SmartNotificationManager.getReminderPreferences(context, userId)
        val nextText = formatNextTrigger(prefs.hour, prefs.minute, prefs.isEnabled)
        _uiState.value = _uiState.value.copy(
            isDailyReminderEnabled = prefs.isEnabled,
            reminderHour = prefs.hour,
            reminderMinute = prefs.minute,
            reminderTimeText = prefs.timeText,
            selectedSkill = prefs.skillName,
            selectedDays = prefs.daysText.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet(),
            nextScheduledTimeFormatted = nextText
        )
    }

    fun loadNotifications() {
        val userId = getUserId()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = ApiClient.fetchNotifications(userId)
            if (result.isSuccess && result.data != null) {
                val data = result.data
                val today = data.todayNotifications.map { it.toUiItem() }
                val yesterday = data.yesterdayNotifications.map { it.toUiItem() }
                val older = data.olderNotifications.map { it.toUiItem() }

                val (parsedHour, parsedMinute) = parseTimeString(data.reminderTimeText)
                val resolvedHour = if (parsedHour >= 0) parsedHour else _uiState.value.reminderHour
                val resolvedMinute = if (parsedMinute >= 0) parsedMinute else _uiState.value.reminderMinute
                val resolvedTimeText = if (data.reminderTimeText.isNotEmpty() && !data.reminderTimeText.contains("Every day at", ignoreCase = true)) {
                    data.reminderTimeText
                } else {
                    formatDisplayTime(resolvedHour, resolvedMinute)
                }

                val nextText = formatNextTrigger(resolvedHour, resolvedMinute, data.isDailyReminderEnabled)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    unreadCount = data.unreadCount,
                    isDailyReminderEnabled = data.isDailyReminderEnabled,
                    reminderTimeText = resolvedTimeText,
                    reminderHour = resolvedHour,
                    reminderMinute = resolvedMinute,
                    nextScheduledTimeFormatted = nextText,
                    todayNotifications = today,
                    yesterdayNotifications = yesterday,
                    olderNotifications = older
                )
            } else {
                // Seed fallback notifications if empty or offline
                seedDefaultNotificationsIfEmpty()
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun parseTimeString(rawText: String): Pair<Int, Int> {
        return try {
            val clean = rawText.replace("Every day at", "", ignoreCase = true).trim()
            val parts = clean.split(" ", ":")
            if (parts.size >= 2) {
                var h = parts[0].toIntOrNull() ?: -1
                val m = parts[1].toIntOrNull() ?: 0
                val amPm = parts.getOrNull(2)
                if (amPm != null) {
                    if (amPm.equals("PM", ignoreCase = true) && h < 12) h += 12
                    if (amPm.equals("AM", ignoreCase = true) && h == 12) h = 0
                }
                Pair(h, m)
            } else {
                Pair(-1, -1)
            }
        } catch (_: Exception) {
            Pair(-1, -1)
        }
    }

    private fun formatDisplayTime(hour: Int, minute: Int): String {
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        return String.format(Locale.US, "%02d:%02d %s", displayHour, minute, amPm)
    }

    private fun formatNextTrigger(hour: Int, minute: Int, isEnabled: Boolean): String {
        if (!isEnabled) return "Reminders currently paused"
        val now = Calendar.getInstance()
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        val isToday = cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
                cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        val prefix = if (isToday) "Today at" else "Tomorrow at"
        val amPm = if (hour >= 12) "PM" else "AM"
        val h = if (hour % 12 == 0) 12 else hour % 12
        return "Next alarm: $prefix " + String.format(Locale.US, "%02d:%02d %s", h, minute, amPm)
    }

    private fun seedDefaultNotificationsIfEmpty() {
        if (_uiState.value.todayNotifications.isEmpty() && _uiState.value.yesterdayNotifications.isEmpty()) {
            val defaultToday = listOf(
                NotificationItem(
                    id = "1",
                    title = "Your Public Speaking practice is waiting.",
                    description = "Spend 10 minutes on today's impromptu speech drill to keep your momentum.",
                    timestampText = "Today, ${_uiState.value.reminderTimeText}",
                    badgeText = "REMINDER",
                    isGreenBadge = false,
                    iconType = NotificationIconType.ALARM,
                    category = "LEARNING",
                    isRead = false
                ),
                NotificationItem(
                    id = "2",
                    title = "Welcome to AI Skills Mirror",
                    description = "Start your skill assessment and build your customized Public Speaking learning plan.",
                    timestampText = "Today, 10:00 AM",
                    badgeText = "NEW",
                    isGreenBadge = true,
                    iconType = NotificationIconType.SPARKLE,
                    category = "LEARNING",
                    isRead = false
                )
            )
            val defaultYesterday = listOf(
                NotificationItem(
                    id = "3",
                    title = "Great job! You completed today's practice.",
                    description = "Your speech clarity reached 88% and earned +50 XP toward your next milestone.",
                    timestampText = "Yesterday, 06:20 PM",
                    badgeText = "COMPLETED",
                    isGreenBadge = true,
                    iconType = NotificationIconType.CHECK,
                    category = "MILESTONES",
                    isRead = true
                ),
                NotificationItem(
                    id = "4",
                    title = "Daily learning reminder",
                    description = "Continue your daily speech training to maintain your 5-day practice streak.",
                    timestampText = "Yesterday, 06:00 PM",
                    badgeText = "STREAK",
                    isGreenBadge = false,
                    iconType = NotificationIconType.STREAK,
                    category = "LEARNING",
                    isRead = true
                )
            )
            val defaultOlder = listOf(
                NotificationItem(
                    id = "5",
                    title = "Progress update",
                    description = "Weekly summary: You've completed 5 sessions with 88% average clarity.",
                    timestampText = "3 days ago",
                    badgeText = "REPORT",
                    isGreenBadge = false,
                    iconType = NotificationIconType.BOOK,
                    category = "MILESTONES",
                    isRead = true
                )
            )
            _uiState.value = _uiState.value.copy(
                todayNotifications = defaultToday,
                yesterdayNotifications = defaultYesterday,
                olderNotifications = defaultOlder,
                unreadCount = 2
            )
        }
    }

    fun onSelectMainTab(tab: NotificationMainTab) {
        _uiState.value = _uiState.value.copy(mainTab = tab)
    }

    fun onSelectFilterTab(tab: NotificationFilterTab) {
        _uiState.value = _uiState.value.copy(selectedFilterTab = tab)
    }

    fun onToggleDailyReminder(enabled: Boolean, context: Context? = null) {
        val nextText = formatNextTrigger(_uiState.value.reminderHour, _uiState.value.reminderMinute, enabled)
        _uiState.value = _uiState.value.copy(
            isDailyReminderEnabled = enabled,
            nextScheduledTimeFormatted = nextText
        )
        val userId = getUserId()
        val timeText = _uiState.value.reminderTimeText
        val daysText = _uiState.value.selectedDays.joinToString(", ")

        if (enabled && context != null) {
            SmartNotificationManager.scheduleDailyPracticeReminder(
                context = context,
                hourOfDay = _uiState.value.reminderHour,
                minute = _uiState.value.reminderMinute,
                skillName = _uiState.value.selectedSkill,
                userId = userId
            )
            viewModelScope.launch {
                _eventChannel.send(NotificationsUiEvent.ShowToast("Daily practice reminder enabled for $timeText"))
            }
        } else if (!enabled && context != null) {
            SmartNotificationManager.cancelPracticeReminder(context, userId)
            viewModelScope.launch {
                _eventChannel.send(NotificationsUiEvent.ShowToast("Daily practice reminder paused"))
            }
        }

        viewModelScope.launch {
            ApiClient.saveReminder(
                userId = userId,
                title = "${_uiState.value.selectedSkill} Practice",
                time = timeText,
                days = daysText,
                isEnabled = enabled
            )
            ApiClient.updateSettings(
                userId = userId,
                reminders = enabled
            )
        }
    }

    fun onSetReminderTime(hour: Int, minute: Int, context: Context? = null) {
        val timeText = formatDisplayTime(hour, minute)
        val nextText = formatNextTrigger(hour, minute, _uiState.value.isDailyReminderEnabled)

        _uiState.value = _uiState.value.copy(
            reminderHour = hour,
            reminderMinute = minute,
            reminderTimeText = timeText,
            nextScheduledTimeFormatted = nextText
        )
    }

    /**
     * Explicit Save Reminder Action
     * Locks in the chosen time, schedules with AlarmManager, saves locally and syncs to backend.
     */
    fun saveReminderSettings(context: Context) {
        val userId = getUserId()
        val timeText = _uiState.value.reminderTimeText
        val daysText = _uiState.value.selectedDays.joinToString(", ")
        val isEnabled = _uiState.value.isDailyReminderEnabled
        val hour = _uiState.value.reminderHour
        val minute = _uiState.value.reminderMinute
        val skill = _uiState.value.selectedSkill

        _uiState.value = _uiState.value.copy(isSavingReminder = true)

        if (isEnabled) {
            SmartNotificationManager.scheduleDailyPracticeReminder(
                context = context,
                hourOfDay = hour,
                minute = minute,
                title = "Daily Practice Reminder",
                message = "It's $timeText! Time for your $skill practice.",
                skillName = skill,
                userId = userId
            )
        } else {
            SmartNotificationManager.cancelPracticeReminder(context, userId)
        }

        val nextText = formatNextTrigger(hour, minute, isEnabled)

        viewModelScope.launch {
            ApiClient.saveReminder(
                userId = userId,
                title = "$skill Practice",
                time = timeText,
                days = daysText,
                isEnabled = isEnabled
            )
            ApiClient.updateSettings(
                userId = userId,
                reminders = isEnabled
            )
            _uiState.value = _uiState.value.copy(
                isSavingReminder = false,
                isSettingsDialogOpen = false,
                nextScheduledTimeFormatted = nextText
            )
            val msg = if (isEnabled) "Reminder saved for $timeText daily!" else "Reminder preferences saved."
            _eventChannel.send(NotificationsUiEvent.ShowToast(msg))
        }
    }

    /**
     * Immediately triggers a high-priority test push notification
     */
    fun triggerInstantTest(context: Context) {
        val title = "AI Skill Mirror Practice Test"
        val message = SmartNotificationManager.MOTIVATIONAL_MESSAGES.random()
        val currentTs = SimpleDateFormat("h:mm a", Locale.US).format(Date())

        SmartNotificationManager.showLocalNotification(
            context = context,
            title = title,
            message = message,
            targetScreen = "DAILY_TRAINING"
        )

        val newItem = NotificationItem(
            id = System.currentTimeMillis().toString(),
            title = title,
            description = message,
            timestampText = "Today, $currentTs",
            badgeText = "TEST",
            isGreenBadge = true,
            iconType = NotificationIconType.ALARM,
            category = "PRACTICE",
            isRead = false
        )
        _uiState.value = _uiState.value.copy(
            todayNotifications = listOf(newItem) + _uiState.value.todayNotifications,
            unreadCount = _uiState.value.unreadCount + 1
        )

        viewModelScope.launch {
            ApiClient.createNotification(
                userId = getUserId(),
                title = title,
                description = message,
                badgeText = "TEST",
                iconType = "ALARM",
                category = "PRACTICE"
            )
            _eventChannel.send(NotificationsUiEvent.ShowToast("Test notification sent! Check your notification shade."))
        }
    }

    /**
     * Schedules a test reminder for exactly 2 minutes ahead using AlarmManager
     */
    fun scheduleTestReminderTwoMinutes(context: Context) {
        val userId = getUserId()
        val triggerEpoch = SmartNotificationManager.scheduleDailyPracticeReminder(
            context = context,
            hourOfDay = _uiState.value.reminderHour,
            minute = _uiState.value.reminderMinute,
            title = "Daily Practice Test (2-Min)",
            message = "Hey! Your 2-minute test practice reminder just fired. Notifications are fully working!",
            skillName = _uiState.value.selectedSkill,
            userId = userId,
            delayMinutes = 2
        )

        val triggerTimeText = SimpleDateFormat("h:mm:ss a", Locale.US).format(Date(triggerEpoch))
        viewModelScope.launch {
            _eventChannel.send(
                NotificationsUiEvent.ShowToast(
                    "Test reminder scheduled for $triggerTimeText (2 mins). You can lock your phone or close the app to test!"
                )
            )
        }
    }

    fun onRequestPermissionClick() {
        viewModelScope.launch {
            _eventChannel.send(NotificationsUiEvent.RequestPermission)
        }
    }

    fun onOpenAppSettingsClick() {
        viewModelScope.launch {
            _eventChannel.send(NotificationsUiEvent.OpenAppSettings)
        }
    }

    fun onOpenExactAlarmSettingsClick() {
        viewModelScope.launch {
            _eventChannel.send(NotificationsUiEvent.OpenExactAlarmSettings)
        }
    }

    // Practice Countdown Timer logic (10, 15, 20, 30 min)
    fun onSelectDuration(minutes: Int) {
        if (_uiState.value.isTimerRunning) {
            timerJob?.cancel()
        }
        val totalSec = minutes * 60
        _uiState.value = _uiState.value.copy(
            timerSelectedMinutes = minutes,
            remainingSeconds = totalSec,
            totalSeconds = totalSec,
            isTimerRunning = false,
            isTimerPaused = false,
            hasShownFiveMinuteWarning = false,
            showCompletionDialog = false
        )
    }

    fun startTimer(context: Context) {
        if (_uiState.value.isTimerRunning && !_uiState.value.isTimerPaused) return

        _uiState.value = _uiState.value.copy(
            isTimerRunning = true,
            isTimerPaused = false
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1000L)
                val newSeconds = _uiState.value.remainingSeconds - 1
                var warningShown = _uiState.value.hasShownFiveMinuteWarning

                // Send notification when exactly 5 minutes (300 seconds) remain
                if (newSeconds == 300 && !warningShown) {
                    warningShown = true
                    SmartNotificationManager.showFiveMinuteWarning(context)
                    val currentTs = SimpleDateFormat("h:mm a", Locale.US).format(Date())
                    val alertItem = NotificationItem(
                        id = System.currentTimeMillis().toString(),
                        title = "5 Minutes Remaining!",
                        description = "5 minutes left in your session. Finish strong!",
                        timestampText = "Today, $currentTs",
                        badgeText = "TIMER",
                        isGreenBadge = false,
                        iconType = NotificationIconType.ALARM,
                        category = "LEARNING"
                    )
                    _uiState.value = _uiState.value.copy(
                        todayNotifications = listOf(alertItem) + _uiState.value.todayNotifications,
                        unreadCount = _uiState.value.unreadCount + 1
                    )
                    launch {
                        ApiClient.createNotification(
                            userId = getUserId(),
                            title = "5 Minutes Remaining!",
                            description = "5 minutes left in your session. Finish strong!",
                            badgeText = "TIMER",
                            iconType = "ALARM",
                            category = "LEARNING"
                        )
                    }
                }

                _uiState.value = _uiState.value.copy(
                    remainingSeconds = newSeconds,
                    hasShownFiveMinuteWarning = warningShown
                )

                if (newSeconds <= 0) {
                    SmartNotificationManager.showSessionCompleted(context)
                    val finishTs = SimpleDateFormat("h:mm a", Locale.US).format(Date())
                    val finishItem = NotificationItem(
                        id = System.currentTimeMillis().toString(),
                        title = "Practice Session Over!",
                        description = "Your practice time is over! Great job completing today's session.",
                        timestampText = "Today, $finishTs",
                        badgeText = "COMPLETED",
                        isGreenBadge = true,
                        iconType = NotificationIconType.CHECK,
                        category = "MILESTONES"
                    )
                    _uiState.value = _uiState.value.copy(
                        isTimerRunning = false,
                        isTimerPaused = false,
                        showCompletionDialog = true,
                        todayNotifications = listOf(finishItem) + _uiState.value.todayNotifications,
                        unreadCount = _uiState.value.unreadCount + 1
                    )
                    launch {
                        ApiClient.createNotification(
                            userId = getUserId(),
                            title = "Practice Session Over!",
                            description = "Your practice time is over! Great job completing today's session.",
                            badgeText = "COMPLETED",
                            iconType = "CHECK",
                            category = "MILESTONES",
                            isGreenBadge = true
                        )
                    }
                    break
                }
            }
        }
    }

    fun resumeTimer(context: Context) {
        startTimer(context)
    }

    fun pauseTimer() {
        if (!_uiState.value.isTimerRunning) return
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isTimerRunning = false,
            isTimerPaused = true
        )
    }

    fun resetTimer() {
        timerJob?.cancel()
        val totalSec = _uiState.value.timerSelectedMinutes * 60
        _uiState.value = _uiState.value.copy(
            remainingSeconds = totalSec,
            totalSeconds = totalSec,
            isTimerRunning = false,
            isTimerPaused = false,
            hasShownFiveMinuteWarning = false,
            showCompletionDialog = false
        )
    }

    fun dismissCompletionDialog() {
        _uiState.value = _uiState.value.copy(showCompletionDialog = false)
    }

    fun triggerSmartNotification(context: Context, type: String) {
        val userId = getUserId()
        val timeText = _uiState.value.reminderTimeText
        val currentTs = SimpleDateFormat("h:mm a", Locale.US).format(Date())

        val (title, message, badge, icon, category, isGreen) = when (type) {
            "DAILY_PRACTICE" -> Tuple6(
                "Daily Practice Reminder",
                "It's $timeText! Time for your ${_uiState.value.selectedSkill} practice.",
                "REMINDER",
                NotificationIconType.ALARM,
                "LEARNING",
                false
            )
            "MISSED_PRACTICE" -> Tuple6(
                "Missed Practice Alert",
                "You haven't completed today's practice yet. Keep your streak alive!",
                "MISSED",
                NotificationIconType.ALARM,
                "LEARNING",
                false
            )
            "PRACTICE_COMPLETED" -> Tuple6(
                "Practice Completed",
                "Great job! You completed today's practice. +50 XP earned.",
                "COMPLETED",
                NotificationIconType.CHECK,
                "MILESTONES",
                true
            )
            "WEEKLY_PROGRESS" -> Tuple6(
                "Weekly Comparison Update",
                "Weekly summary: You've completed practice sessions with verified score improvements.",
                "WEEKLY",
                NotificationIconType.BOOK,
                "MILESTONES",
                false
            )
            "AI_COACH_TIP" -> Tuple6(
                "AI Coach Recommendation",
                "Coach Tip: Work on reducing pauses in your 2-minute impromptu speech.",
                "COACH",
                NotificationIconType.SPARKLE,
                "LEARNING",
                false
            )
            else -> Tuple6(
                "Daily Training",
                "Your training studio is ready for your next practice.",
                "PRACTICE",
                NotificationIconType.MIC,
                "LEARNING",
                true
            )
        }

        SmartNotificationManager.showLocalNotification(
            context = context,
            title = title,
            message = message,
            targetScreen = "DAILY_TRAINING"
        )

        val newItem = NotificationItem(
            id = System.currentTimeMillis().toString(),
            title = title,
            description = message,
            timestampText = "Today, $currentTs",
            badgeText = badge,
            isGreenBadge = isGreen,
            iconType = icon,
            category = category,
            isRead = false
        )
        _uiState.value = _uiState.value.copy(
            todayNotifications = listOf(newItem) + _uiState.value.todayNotifications,
            unreadCount = _uiState.value.unreadCount + 1
        )

        viewModelScope.launch {
            ApiClient.createNotification(
                userId = userId,
                title = title,
                description = message,
                badgeText = badge,
                iconType = icon.name,
                category = category,
                isGreenBadge = isGreen
            )
            _eventChannel.send(NotificationsUiEvent.ShowToast("Notification sent: $title"))
        }
    }

    // Settings Dialog handlers
    fun openSettingsDialog() {
        _uiState.value = _uiState.value.copy(isSettingsDialogOpen = true)
    }

    fun closeSettingsDialog() {
        _uiState.value = _uiState.value.copy(isSettingsDialogOpen = false)
    }

    fun toggleDay(day: String) {
        val current = _uiState.value.selectedDays.toMutableSet()
        if (current.contains(day)) {
            if (current.size > 1) current.remove(day)
        } else {
            current.add(day)
        }
        _uiState.value = _uiState.value.copy(selectedDays = current)
    }

    fun toggleNotificationType(type: String) {
        val current = _uiState.value.enabledNotificationTypes.toMutableSet()
        if (current.contains(type)) {
            current.remove(type)
        } else {
            current.add(type)
        }
        _uiState.value = _uiState.value.copy(enabledNotificationTypes = current)
    }

    fun saveSettings(context: Context) {
        saveReminderSettings(context)
    }

    // Mark single notification as read
    fun markAsRead(notificationId: String) {
        val updateItem = { item: NotificationItem ->
            if (item.id == notificationId) item.copy(isRead = true) else item
        }
        val today = _uiState.value.todayNotifications.map(updateItem)
        val yesterday = _uiState.value.yesterdayNotifications.map(updateItem)
        val older = _uiState.value.olderNotifications.map(updateItem)
        val newUnread = (today + yesterday + older).count { !it.isRead }

        _uiState.value = _uiState.value.copy(
            todayNotifications = today,
            yesterdayNotifications = yesterday,
            olderNotifications = older,
            unreadCount = newUnread
        )

        viewModelScope.launch {
            ApiClient.markNotificationRead(getUserId(), notificationId)
        }
    }

    // Mark all as read
    fun onMarkAllAsRead() {
        val markList = { list: List<NotificationItem> -> list.map { it.copy(isRead = true) } }
        _uiState.value = _uiState.value.copy(
            todayNotifications = markList(_uiState.value.todayNotifications),
            yesterdayNotifications = markList(_uiState.value.yesterdayNotifications),
            olderNotifications = markList(_uiState.value.olderNotifications),
            unreadCount = 0
        )
        viewModelScope.launch {
            ApiClient.markNotificationRead(getUserId(), "all")
            _eventChannel.send(NotificationsUiEvent.ShowToast("All notifications marked as read"))
        }
    }

    // Delete single notification
    fun deleteNotification(notificationId: String) {
        val today = _uiState.value.todayNotifications.filterNot { it.id == notificationId }
        val yesterday = _uiState.value.yesterdayNotifications.filterNot { it.id == notificationId }
        val older = _uiState.value.olderNotifications.filterNot { it.id == notificationId }
        val newUnread = (today + yesterday + older).count { !it.isRead }

        _uiState.value = _uiState.value.copy(
            todayNotifications = today,
            yesterdayNotifications = yesterday,
            olderNotifications = older,
            unreadCount = newUnread
        )

        viewModelScope.launch {
            ApiClient.clearNotifications(getUserId(), notificationId)
            _eventChannel.send(NotificationsUiEvent.ShowToast("Notification removed"))
        }
    }

    // Clear all notifications
    fun onClearAllNotifications() {
        _uiState.value = _uiState.value.copy(
            todayNotifications = emptyList(),
            yesterdayNotifications = emptyList(),
            olderNotifications = emptyList(),
            unreadCount = 0
        )
        viewModelScope.launch {
            ApiClient.clearNotifications(getUserId())
            _eventChannel.send(NotificationsUiEvent.ShowToast("All notifications cleared"))
        }
    }

    fun onStartPracticeClick() {
        viewModelScope.launch {
            _eventChannel.send(NotificationsUiEvent.NavigateToPractice)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(NotificationsUiEvent.NavigateBack)
        }
    }

    private fun com.simats.aiskillmirror.data.network.NotificationItemData.toUiItem(): NotificationItem {
        val icon = when (this.iconType.uppercase()) {
            "BOOK" -> NotificationIconType.BOOK
            "STREAK" -> NotificationIconType.STREAK
            "SPARKLE" -> NotificationIconType.SPARKLE
            "ALARM" -> NotificationIconType.ALARM
            "MIC" -> NotificationIconType.MIC
            "CHECK" -> NotificationIconType.CHECK
            else -> NotificationIconType.BELL
        }
        return NotificationItem(
            id = this.id,
            title = this.title,
            description = this.description,
            timestampText = this.timestampText,
            badgeText = this.badgeText,
            isGreenBadge = this.isGreenBadge,
            iconType = icon,
            category = this.category,
            isRead = this.isRead
        )
    }

    private data class Tuple6<A, B, C, D, E, F>(
        val a: A, val b: B, val c: C, val d: D, val e: E, val f: F
    )
}
