package com.simats.aiskillmirror.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SettingsUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onToggleLearningReminders(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isLearningRemindersEnabled = enabled)
    }

    fun onTogglePersonalizedAi(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isPersonalizedAiEnabled = enabled)
    }

    fun onToggleSaveVoiceRecordings(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isSaveVoiceRecordingsEnabled = enabled)
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.Logout)
        }
    }

    fun onPersonalInformationClick() {
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.OpenPersonalInformation)
        }
    }

    fun onPasswordSecurityClick() {
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.OpenPasswordSecurity)
        }
    }

    fun onDataPermissionsClick() {
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.OpenDataPermissions)
        }
    }

    fun onDownloadDataClick() {
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.DownloadData)
        }
    }

    fun onHelpSupportClick() {
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.OpenHelpSupport)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.NavigateBack)
        }
    }
}
