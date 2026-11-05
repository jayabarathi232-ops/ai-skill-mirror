package com.simats.aiskillmirror.ui.notifications

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.notifications.SmartNotificationManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Locale

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<NotificationsUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun checkPermission(context: Context) {
        val hasPermission = SmartNotificationManager.hasNotificationPermission(context)
        _uiState.value = _uiState.value.copy(hasNotificationPermission = hasPermission)
    }

    fun onSelectMainTab(tab: NotificationMainTab) {
        _uiState.value = _uiState.value.copy(mainTab = tab)
    }

    fun onSelectFilterTab(tab: NotificationFilterTab) {
        _uiState.value = _uiState.value.copy(selectedFilterTab = tab)
    }

    fun onToggleDailyReminder(enabled: Boolean, context: Context? = null) {
        _uiState.value = _uiState.value.copy(isDailyReminderEnabled = enabled)
        if (enabled && context != null) {
            SmartNotificationManager.scheduleDailyPracticeReminder(
                context,
                _uiState.value.reminderHour,
                _uiState.value.reminderMinute
            )
        } else if (!enabled && context != null) {
            SmartNotificationManager.cancelPracticeReminder(context)
        }
    }

    fun onSetReminderTime(hour: Int, minute: Int, context: Context? = null) {
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        val timeText = String.format(Locale.US, "%02d:%02d %s", displayHour, minute, amPm)

        _uiState.value = _uiState.value.copy(
            reminderHour = hour,
            reminderMinute = minute,
            reminderTimeText = timeText
        )

        if (_uiState.value.isDailyReminderEnabled && context != null) {
            SmartNotificationManager.scheduleDailyPracticeReminder(context, hour, minute)
        }
    }

    fun onMarkAllAsRead() {
        _uiState.value = _uiState.value.copy(unreadCount = 0)
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(NotificationsUiEvent.NavigateBack)
        }
    }
}
