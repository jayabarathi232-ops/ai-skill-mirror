package com.simats.aiskillmirror.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository = SettingsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SettingsUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            val result = settingsRepository.getSettings(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val s = result.data
                _uiState.value = _uiState.value.copy(
                    isLearningRemindersEnabled = s.isLearningRemindersEnabled,
                    isPersonalizedAiEnabled = s.isPersonalizedAiEnabled,
                    isSaveVoiceRecordingsEnabled = s.isSaveVoiceRecordingsEnabled,
                    currentAppearanceText = s.currentAppearanceText,
                    currentLanguageText = s.currentLanguageText,
                    appVersionText = s.appVersionText
                )
            }
        }
    }

    fun onToggleLearningReminders(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isLearningRemindersEnabled = enabled)
        viewModelScope.launch {
            settingsRepository.updateSettings(UserSession.userId, reminders = enabled)
        }
    }

    fun onTogglePersonalizedAi(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isPersonalizedAiEnabled = enabled)
        viewModelScope.launch {
            settingsRepository.updateSettings(UserSession.userId, ai = enabled)
        }
    }

    fun onToggleSaveVoiceRecordings(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isSaveVoiceRecordingsEnabled = enabled)
        viewModelScope.launch {
            settingsRepository.updateSettings(UserSession.userId, voice = enabled)
        }
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
