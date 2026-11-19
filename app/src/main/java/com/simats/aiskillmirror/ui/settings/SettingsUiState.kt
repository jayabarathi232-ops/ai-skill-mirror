package com.simats.aiskillmirror.ui.settings

import com.simats.aiskillmirror.ui.localization.LanguageManager
import com.simats.aiskillmirror.ui.theme.ThemeManager

data class SettingsUiState(
    val headerTitle: String = LanguageManager.t("make_it_yours"),
    val headerCategory: String = LanguageManager.t("account_privacy"),
    val isDarkMode: Boolean = ThemeManager.isDarkMode,
    val selectedThemePalette: String = ThemeManager.currentPalette.id,
    val isLearningRemindersEnabled: Boolean = true,
    val reminderTimeText: String = "07:00 PM",
    val isPersonalizedAiEnabled: Boolean = true,
    val isSaveVoiceRecordingsEnabled: Boolean = false,
    val isSoundEffectsEnabled: Boolean = true,
    val isAudioGuidanceEnabled: Boolean = true,
    val currentLanguageText: String = LanguageManager.currentLanguage.displayName,
    val appVersionText: String = LanguageManager.t("app_version"),
    val userEmail: String = "",
    val userName: String = "",
    val showLanguageDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val showTimePickerDialog: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val showDataPermissionsDialog: Boolean = false,
    val showDownloadDataDialog: Boolean = false,
    val showHelpSupportDialog: Boolean = false,
    val showLogoutConfirmationDialog: Boolean = false,
    val toastMessage: String? = null
)

sealed interface SettingsUiEvent {
    object Logout : SettingsUiEvent
    object OpenPersonalInformation : SettingsUiEvent
    object NavigateBack : SettingsUiEvent
    data class ShowToast(val message: String) : SettingsUiEvent
}
