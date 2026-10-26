package com.simats.aiskillmirror.ui.settings

data class SettingsUiState(
    val headerTitle: String = "Make it yours",
    val headerCategory: String = "ACCOUNT & PRIVACY",
    val isLearningRemindersEnabled: Boolean = true,
    val isPersonalizedAiEnabled: Boolean = true,
    val isSaveVoiceRecordingsEnabled: Boolean = false,
    val currentAppearanceText: String = "Dark",
    val currentLanguageText: String = "English",
    val appVersionText: String = "AI Skills Mirror · Version 1.0.0"
)

sealed interface SettingsUiEvent {
    object Logout : SettingsUiEvent
    object OpenPersonalInformation : SettingsUiEvent
    object OpenPasswordSecurity : SettingsUiEvent
    object OpenDataPermissions : SettingsUiEvent
    object DownloadData : SettingsUiEvent
    object OpenHelpSupport : SettingsUiEvent
    object NavigateBack : SettingsUiEvent
}
