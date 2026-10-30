package com.simats.aiskillmirror.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.repository.ProgressRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val progressRepository: ProgressRepository = ProgressRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<NotificationsUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            val result = progressRepository.getNotifications(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                fun mapList(items: List<com.simats.aiskillmirror.data.network.NotificationItemData>): List<NotificationItem> {
                    return items.map {
                        val icon = when (it.iconType.uppercase()) {
                            "STREAK" -> NotificationIconType.STREAK
                            "SPARKLE" -> NotificationIconType.SPARKLE
                            "ALARM" -> NotificationIconType.ALARM
                            else -> NotificationIconType.BOOK
                        }
                        NotificationItem(
                            id = it.id,
                            title = it.title,
                            description = it.description,
                            timestampText = it.timestampText,
                            badgeText = it.badgeText,
                            isGreenBadge = it.isGreenBadge,
                            iconType = icon
                        )
                    }
                }
                _uiState.value = _uiState.value.copy(
                    isDailyReminderEnabled = d.isDailyReminderEnabled,
                    reminderTimeText = d.reminderTimeText,
                    reminderNoticeText = d.reminderNoticeText,
                    todayNotifications = mapList(d.todayNotifications),
                    yesterdayNotifications = mapList(d.yesterdayNotifications)
                )
            }
        }
    }

    fun onSelectFilterTab(tab: NotificationFilterTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun onToggleDailyReminder(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isDailyReminderEnabled = enabled)
    }

    fun onMarkAllAsReadClick() {
        viewModelScope.launch {
            _eventChannel.send(NotificationsUiEvent.MarkAllAsRead)
        }
    }

    fun onSettingsClick() {
        viewModelScope.launch {
            _eventChannel.send(NotificationsUiEvent.OpenSettings)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(NotificationsUiEvent.NavigateBack)
        }
    }
}
