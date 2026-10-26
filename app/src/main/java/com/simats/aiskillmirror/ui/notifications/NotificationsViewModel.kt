package com.simats.aiskillmirror.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<NotificationsUiEvent>()
    val events = _eventChannel.receiveAsFlow()

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
