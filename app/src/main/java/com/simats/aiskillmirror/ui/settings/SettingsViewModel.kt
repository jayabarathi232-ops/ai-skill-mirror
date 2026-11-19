package com.simats.aiskillmirror.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.notifications.SmartNotificationManager
import com.simats.aiskillmirror.data.repository.SettingsRepository
import com.simats.aiskillmirror.ui.localization.AppLanguage
import com.simats.aiskillmirror.ui.localization.LanguageManager
import com.simats.aiskillmirror.ui.theme.ThemeManager
import com.simats.aiskillmirror.ui.theme.ThemePalette
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository = SettingsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            isDarkMode = ThemeManager.isDarkMode,
            selectedThemePalette = ThemeManager.currentPalette.id,
            currentLanguageText = LanguageManager.currentLanguage.displayName,
            reminderTimeText = UserSession.getReminderTime(),
            isSoundEffectsEnabled = UserSession.getSoundEffectsEnabled(),
            isAudioGuidanceEnabled = UserSession.getAudioGuidanceEnabled(),
            userName = UserSession.userName,
            userEmail = UserSession.userEmail
        )
    )
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
                val isDark = s.currentAppearanceText.equals("Dark", ignoreCase = true)
                val palette = ThemePalette.fromId(s.themePaletteText)
                val lang = AppLanguage.fromCodeOrName(s.currentLanguageText)

                _uiState.value = _uiState.value.copy(
                    isLearningRemindersEnabled = s.isLearningRemindersEnabled,
                    isPersonalizedAiEnabled = s.isPersonalizedAiEnabled,
                    isSaveVoiceRecordingsEnabled = s.isSaveVoiceRecordingsEnabled,
                    isDarkMode = isDark,
                    selectedThemePalette = palette.id,
                    reminderTimeText = s.reminderTimeText.ifBlank { UserSession.getReminderTime() },
                    isSoundEffectsEnabled = s.isSoundEffectsEnabled,
                    isAudioGuidanceEnabled = s.isAudioGuidanceEnabled,
                    currentLanguageText = lang.displayName,
                    appVersionText = s.appVersionText,
                    userName = UserSession.userName,
                    userEmail = UserSession.userEmail
                )
            }
        }
    }

    fun onToggleDarkMode(enabled: Boolean, context: Context?) {
        _uiState.value = _uiState.value.copy(isDarkMode = enabled)
        ThemeManager.setDarkMode(context, enabled)
        val appearance = if (enabled) "Dark" else "Light"
        val msg = if (enabled) {
            LanguageManager.t("toast_dark_mode_on")
        } else {
            LanguageManager.t("toast_dark_mode_off")
        }
        showToast(msg)

        viewModelScope.launch {
            settingsRepository.updateSettings(
                userId = UserSession.userId,
                appearance = appearance
            )
        }
    }

    fun onSelectThemePalette(palette: ThemePalette, context: Context?) {
        _uiState.value = _uiState.value.copy(
            selectedThemePalette = palette.id,
            showThemeDialog = false
        )
        ThemeManager.setPalette(context, palette)
        val msg = LanguageManager.t("toast_theme_changed", palette.title)
        showToast(msg)

        viewModelScope.launch {
            settingsRepository.updateSettings(
                userId = UserSession.userId,
                themePalette = palette.id
            )
        }
    }

    fun onSelectLanguage(language: AppLanguage, context: Context?) {
        LanguageManager.setLanguage(context, language)
        _uiState.value = _uiState.value.copy(
            currentLanguageText = language.displayName,
            headerTitle = LanguageManager.t("make_it_yours"),
            headerCategory = LanguageManager.t("account_privacy"),
            appVersionText = LanguageManager.t("app_version"),
            showLanguageDialog = false
        )
        val msg = LanguageManager.t("toast_lang_changed", "${language.displayName} (${language.nativeName})")
        showToast(msg)

        viewModelScope.launch {
            settingsRepository.updateSettings(
                userId = UserSession.userId,
                language = language.displayName
            )
        }
    }

    fun onToggleLearningReminders(enabled: Boolean, context: Context?) {
        _uiState.value = _uiState.value.copy(isLearningRemindersEnabled = enabled)

        if (context != null) {
            if (enabled) {
                if (!SmartNotificationManager.hasNotificationPermission(context)) {
                    showToast(LanguageManager.t("permission_msg"))
                }
                // Schedule alarm using saved reminder time
                val timeStr = _uiState.value.reminderTimeText
                val (hour, minute) = parseTime(timeStr)
                SmartNotificationManager.scheduleDailyPracticeReminder(
                    context = context,
                    hourOfDay = hour,
                    minute = minute,
                    skillName = "AI Skill Practice",
                    userId = UserSession.userId
                )
                showToast(LanguageManager.t("toast_reminder_enabled", timeStr))
            } else {
                SmartNotificationManager.cancelPracticeReminder(context, UserSession.userId)
                showToast(LanguageManager.t("toast_reminder_disabled"))
            }
        }

        viewModelScope.launch {
            settingsRepository.updateSettings(
                userId = UserSession.userId,
                reminders = enabled
            )
        }
    }

    fun onSetReminderTime(timeText: String, hour: Int, minute: Int, context: Context?) {
        _uiState.value = _uiState.value.copy(
            reminderTimeText = timeText,
            showTimePickerDialog = false
        )
        UserSession.setReminderTime(context, timeText)

        if (context != null && _uiState.value.isLearningRemindersEnabled) {
            SmartNotificationManager.scheduleDailyPracticeReminder(
                context = context,
                hourOfDay = hour,
                minute = minute,
                skillName = "AI Skill Practice",
                userId = UserSession.userId
            )
        }
        showToast(LanguageManager.t("toast_reminder_enabled", timeText))

        viewModelScope.launch {
            settingsRepository.updateSettings(
                userId = UserSession.userId,
                reminderTime = timeText
            )
        }
    }

    fun onSendTestReminder(context: Context) {
        if (!SmartNotificationManager.hasNotificationPermission(context)) {
            showToast(LanguageManager.t("permission_msg"))
            return
        }
        SmartNotificationManager.showPracticeReminder(
            context = context,
            timeText = _uiState.value.reminderTimeText,
            skillName = "AI Skill Practice"
        )
        showToast("Test reminder delivered! Check your notification shade.")
    }

    fun onTogglePersonalizedAi(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isPersonalizedAiEnabled = enabled)
        val msg = if (enabled) "Personalized AI insights enabled" else "Personalized AI insights disabled"
        showToast(msg)

        viewModelScope.launch {
            settingsRepository.updateSettings(
                userId = UserSession.userId,
                ai = enabled
            )
        }
    }

    fun onToggleSaveVoiceRecordings(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isSaveVoiceRecordingsEnabled = enabled)
        val msg = if (enabled) "Voice recordings will be saved locally" else "Voice recordings will not be stored"
        showToast(msg)

        viewModelScope.launch {
            settingsRepository.updateSettings(
                userId = UserSession.userId,
                voice = enabled
            )
        }
    }

    fun onToggleSoundEffects(enabled: Boolean, context: Context?) {
        _uiState.value = _uiState.value.copy(isSoundEffectsEnabled = enabled)
        UserSession.setSoundEffectsEnabled(context, enabled)
        val msg = if (enabled) "Sound effects enabled" else "Sound effects disabled"
        showToast(msg)

        viewModelScope.launch {
            settingsRepository.updateSettings(
                userId = UserSession.userId,
                sound = enabled
            )
        }
    }

    fun onToggleAudioGuidance(enabled: Boolean, context: Context?) {
        _uiState.value = _uiState.value.copy(isAudioGuidanceEnabled = enabled)
        UserSession.setAudioGuidanceEnabled(context, enabled)
        val msg = if (enabled) "Audio guidance enabled" else "Audio guidance disabled"
        showToast(msg)

        viewModelScope.launch {
            settingsRepository.updateSettings(
                userId = UserSession.userId,
                audio = enabled
            )
        }
    }

    fun onDownloadData(context: Context) {
        _uiState.value = _uiState.value.copy(showDownloadDataDialog = false)
        showToast(LanguageManager.t("toast_data_downloaded"))
    }

    // Dialog Visibilities
    fun setShowLanguageDialog(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showLanguageDialog = visible)
    }

    fun setShowThemeDialog(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showThemeDialog = visible)
    }

    fun setShowTimePickerDialog(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showTimePickerDialog = visible)
    }

    fun setShowPasswordDialog(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showPasswordDialog = visible)
    }

    fun setShowDataPermissionsDialog(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showDataPermissionsDialog = visible)
    }

    fun setShowDownloadDataDialog(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showDownloadDataDialog = visible)
    }

    fun setShowHelpSupportDialog(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showHelpSupportDialog = visible)
    }

    fun setShowLogoutConfirmationDialog(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showLogoutConfirmationDialog = visible)
    }

    fun onPersonalInformationClick() {
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.OpenPersonalInformation)
        }
    }

    fun onLogoutConfirmed() {
        _uiState.value = _uiState.value.copy(showLogoutConfirmationDialog = false)
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.Logout)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.NavigateBack)
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _eventChannel.send(SettingsUiEvent.ShowToast(message))
        }
    }

    private fun parseTime(timeStr: String): Pair<Int, Int> {
        return try {
            val parts = timeStr.trim().split(" ")
            val timeParts = parts[0].split(":")
            var hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            val isPm = parts.getOrNull(1)?.equals("PM", ignoreCase = true) == true
            if (isPm && hour < 12) hour += 12
            if (!isPm && hour == 12) hour = 0
            Pair(hour, minute)
        } catch (e: Exception) {
            Pair(19, 0) // Default 07:00 PM
        }
    }
}
