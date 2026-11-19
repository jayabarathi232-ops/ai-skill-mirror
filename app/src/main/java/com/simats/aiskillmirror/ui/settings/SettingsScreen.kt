package com.simats.aiskillmirror.ui.settings

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.simats.aiskillmirror.ui.dashboard.BellIcon
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.dashboard.MicIcon
import com.simats.aiskillmirror.ui.dashboard.RightArrowHeadIcon
import com.simats.aiskillmirror.ui.localization.AppLanguage
import com.simats.aiskillmirror.ui.localization.LanguageManager
import com.simats.aiskillmirror.ui.login.ShieldCheckIcon
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.register.LockIcon
import com.simats.aiskillmirror.ui.register.PersonIcon
import com.simats.aiskillmirror.ui.report.ExportTrayIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onPersonalInformationClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Listen for single-shot events (toasts, logout, navigation)
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is SettingsUiEvent.Logout -> {
                    onLogoutClick()
                }
                is SettingsUiEvent.OpenPersonalInformation -> {
                    onPersonalInformationClick()
                }
                is SettingsUiEvent.NavigateBack -> {
                    onBackClick()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Scrollable Settings Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBackground)
                            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.onBackClick()
                                onBackClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        BackArrowIcon(size = 20.dp, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = LanguageManager.t("make_it_yours"),
                            color = HeadingPurple,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = LanguageManager.t("account_privacy"),
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // SECTION 1: ACCOUNT & NOTIFICATIONS
                // ==========================================
                Text(
                    text = LanguageManager.t("account_section"),
                    color = HeadingPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsCardContainer {
                    // Personal info
                    SettingsClickableRow(
                        icon = { PersonIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("personal_info"),
                        valueText = uiState.userName.ifBlank { "View profile" },
                        onClick = {
                            viewModel.onPersonalInformationClick()
                            onPersonalInformationClick()
                        }
                    )

                    SettingsDivider()

                    // Password & security
                    SettingsClickableRow(
                        icon = { LockIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("password_security"),
                        valueText = "Updated recently",
                        onClick = { viewModel.setShowPasswordDialog(true) }
                    )

                    SettingsDivider()

                    // Learning reminders toggle
                    SettingsToggleRow(
                        icon = { BellIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("learning_reminders"),
                        subtitle = "Daily practice alerts & streak keeper",
                        isChecked = uiState.isLearningRemindersEnabled,
                        onCheckedChange = { viewModel.onToggleLearningReminders(it, context) }
                    )

                    // Daily Practice Reminder Time (shown when reminders enabled)
                    if (uiState.isLearningRemindersEnabled) {
                        SettingsDivider()

                        SettingsClickableRow(
                            icon = { ClockIcon(size = 20.dp, color = AccentPurple) },
                            title = LanguageManager.t("reminder_time"),
                            valueText = uiState.reminderTimeText,
                            onClick = { viewModel.setShowTimePickerDialog(true) }
                        )

                        SettingsDivider()

                        // Test Notification Trigger
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(TaglineBackground)
                                .clickable { viewModel.onSendTestReminder(context) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔔", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = LanguageManager.t("send_test_reminder"),
                                    color = TaglineText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "Test now →",
                                color = TaglineText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // SECTION 2: PREFERENCES & APPEARANCE
                // ==========================================
                Text(
                    text = LanguageManager.t("preferences_section"),
                    color = HeadingPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsCardContainer {
                    // Dark Mode Toggle
                    SettingsToggleRow(
                        icon = { MoonIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("dark_mode"),
                        subtitle = if (uiState.isDarkMode) "Enabled · Deep contrast" else "Disabled · Soft lavender",
                        isChecked = uiState.isDarkMode,
                        onCheckedChange = { viewModel.onToggleDarkMode(it, context) }
                    )

                    SettingsDivider()

                    // Theme Customization
                    SettingsClickableRow(
                        icon = { PaletteIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("theme_customization"),
                        valueText = uiState.selectedThemePalette,
                        onClick = { viewModel.setShowThemeDialog(true) }
                    )

                    SettingsDivider()

                    // Language Selector
                    SettingsClickableRow(
                        icon = { GlobeIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("language"),
                        valueText = uiState.currentLanguageText,
                        onClick = { viewModel.setShowLanguageDialog(true) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // SECTION 3: SOUND & AUDIO GUIDANCE
                // ==========================================
                Text(
                    text = LanguageManager.t("sound_settings"),
                    color = HeadingPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsCardContainer {
                    // Sound Effects
                    SettingsToggleRow(
                        icon = { VolumeIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("sound_effects"),
                        subtitle = "Audio cues on quiz & task completion",
                        isChecked = uiState.isSoundEffectsEnabled,
                        onCheckedChange = { viewModel.onToggleSoundEffects(it, context) }
                    )

                    SettingsDivider()

                    // Audio Guidance
                    SettingsToggleRow(
                        icon = { SpeakerIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("audio_guidance"),
                        subtitle = "AI voice coaching & playback instructions",
                        isChecked = uiState.isAudioGuidanceEnabled,
                        onCheckedChange = { viewModel.onToggleAudioGuidance(it, context) }
                    )

                    SettingsDivider()

                    // Save Voice Recordings
                    SettingsToggleRow(
                        icon = { MicIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("save_voice"),
                        subtitle = "Keep recording audio takes for replay",
                        isChecked = uiState.isSaveVoiceRecordingsEnabled,
                        onCheckedChange = { viewModel.onToggleSaveVoiceRecordings(it) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // SECTION 4: PRIVACY & DATA
                // ==========================================
                Text(
                    text = LanguageManager.t("privacy_intelligence"),
                    color = HeadingPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsCardContainer {
                    // Personalized AI
                    SettingsToggleRow(
                        icon = { SparkleIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("personalized_ai"),
                        subtitle = "Tailor drills to your speech & code gaps",
                        isChecked = uiState.isPersonalizedAiEnabled,
                        onCheckedChange = { viewModel.onTogglePersonalizedAi(it) }
                    )

                    SettingsDivider()

                    // Data & Permissions
                    SettingsClickableRow(
                        icon = { ShieldCheckIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("data_permissions"),
                        valueText = "Camera, Mic & Notifications",
                        onClick = { viewModel.setShowDataPermissionsDialog(true) }
                    )

                    SettingsDivider()

                    // Download Data
                    SettingsClickableRow(
                        icon = { ExportTrayIcon(size = 20.dp, color = AccentPurple) },
                        title = LanguageManager.t("download_data"),
                        valueText = "Export summary",
                        onClick = { viewModel.setShowDownloadDataDialog(true) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer Links: Help & Support & Log out
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageManager.t("help_support"),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.setShowHelpSupportDialog(true) }
                    )

                    Text(
                        text = LanguageManager.t("logout"),
                        color = TaglineText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.setShowLogoutConfirmationDialog(true) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = LanguageManager.t("app_version"),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Fixed Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.PROFILE,
                onTabSelected = onTabSelected
            )
        }

        // ==========================================
        // DIALOGS
        // ==========================================

        // 1. Language Selection Dialog
        if (uiState.showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = AppLanguage.fromCodeOrName(uiState.currentLanguageText),
                onSelectLanguage = { lang ->
                    viewModel.onSelectLanguage(lang, context)
                },
                onDismiss = { viewModel.setShowLanguageDialog(false) }
            )
        }

        // 2. Theme Customization Dialog
        if (uiState.showThemeDialog) {
            ThemeCustomizationDialog(
                currentPalette = ThemePalette.fromId(uiState.selectedThemePalette),
                onSelectPalette = { palette ->
                    viewModel.onSelectThemePalette(palette, context)
                },
                onDismiss = { viewModel.setShowThemeDialog(false) }
            )
        }

        // 3. Reminder Time Picker Dialog
        if (uiState.showTimePickerDialog) {
            ReminderTimePickerDialog(
                currentTimeText = uiState.reminderTimeText,
                onConfirmTime = { timeText, hour, minute ->
                    viewModel.onSetReminderTime(timeText, hour, minute, context)
                },
                onDismiss = { viewModel.setShowTimePickerDialog(false) }
            )
        }

        // 4. Password & Security Dialog
        if (uiState.showPasswordDialog) {
            PasswordSecurityDialog(
                userEmail = uiState.userEmail,
                onDismiss = { viewModel.setShowPasswordDialog(false) }
            )
        }

        // 5. Data & Permissions Dialog
        if (uiState.showDataPermissionsDialog) {
            DataPermissionsDialog(
                onDismiss = { viewModel.setShowDataPermissionsDialog(false) }
            )
        }

        // 6. Download Data Dialog
        if (uiState.showDownloadDataDialog) {
            DownloadDataDialog(
                onConfirm = { viewModel.onDownloadData(context) },
                onDismiss = { viewModel.setShowDownloadDataDialog(false) }
            )
        }

        // 7. Help & Support Dialog
        if (uiState.showHelpSupportDialog) {
            HelpSupportDialog(
                onDismiss = { viewModel.setShowHelpSupportDialog(false) }
            )
        }

        // 8. Logout Confirmation Dialog
        if (uiState.showLogoutConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.setShowLogoutConfirmationDialog(false) },
                title = {
                    Text(
                        text = LanguageManager.t("confirm_logout"),
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "You will need to sign back in to access your skills, daily streaks, and progress reports.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.onLogoutConfirmed() }) {
                        Text(
                            text = LanguageManager.t("logout").replace(" →", ""),
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setShowLogoutConfirmationDialog(false) }) {
                        Text(text = LanguageManager.t("cancel"), color = TextSecondary)
                    }
                },
                containerColor = CardBackground
            )
        }
    }
}

// ==========================================
// REUSABLE CONTAINERS & ROWS
// ==========================================

@Composable
fun SettingsCardContainer(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        color = CardBorder.copy(alpha = 0.6f),
        thickness = 1.dp
    )
}

@Composable
fun SettingsClickableRow(
    icon: @Composable () -> Unit,
    title: String,
    valueText: String = "",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            icon()
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (valueText.isNotBlank()) {
                Text(
                    text = valueText,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            RightArrowHeadIcon(size = 16.dp, color = TextSecondary)
        }
    }
}

@Composable
fun SettingsToggleRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            icon()
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryGradientStart,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = if (AppTheme.isDark) Color(0xFF2E2744) else Color(0xFFEDE5FA)
            )
        )
    }
}

// ==========================================
// DIALOG IMPLEMENTATIONS
// ==========================================

@Composable
fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageManager.t("select_language"),
                        color = HeadingPurple,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✕",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AppLanguage.entries.forEach { lang ->
                    val isSelected = lang == currentLanguage
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) TaglineBackground else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) AccentPurple else CardBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectLanguage(lang) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = lang.displayName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = lang.nativeName,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(AccentPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGradientStart)
                ) {
                    Text(text = LanguageManager.t("close"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ThemeCustomizationDialog(
    currentPalette: ThemePalette,
    onSelectPalette: (ThemePalette) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageManager.t("select_theme"),
                        color = HeadingPurple,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✕",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Choose your preferred primary color palette:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                ThemePalette.entries.forEach { palette ->
                    val isSelected = palette == currentPalette
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) TaglineBackground else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) palette.primary else CardBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectPalette(palette) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(palette.primary, palette.primaryEnd)
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = palette.title,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }

                        if (isSelected) {
                            Text(text = "✓ Active", color = palette.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGradientStart)
                ) {
                    Text(text = LanguageManager.t("close"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReminderTimePickerDialog(
    currentTimeText: String,
    onConfirmTime: (String, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val presets = listOf(
        Triple("07:00 AM", 7, 0),
        Triple("08:30 AM", 8, 30),
        Triple("12:00 PM", 12, 0),
        Triple("05:30 PM", 17, 30),
        Triple("07:00 PM", 19, 0),
        Triple("08:30 PM", 20, 30),
        Triple("09:30 PM", 21, 30)
    )

    var selectedPreset by remember {
        mutableStateOf(presets.firstOrNull { it.first.equals(currentTimeText, ignoreCase = true) } ?: presets[4])
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageManager.t("set_reminder_time"),
                        color = HeadingPurple,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✕",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select your daily practice reminder time. The app will remind you at this exact time every day:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                presets.forEach { preset ->
                    val isSelected = preset.first == selectedPreset.first
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) TaglineBackground else Color.Transparent)
                            .border(1.dp, if (isSelected) AccentPurple else CardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedPreset = preset }
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = preset.first,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        if (isSelected) {
                            Text(text = "✓ Selected", color = AccentPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = LanguageManager.t("cancel"), color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            onConfirmTime(selectedPreset.first, selectedPreset.second, selectedPreset.third)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGradientStart)
                    ) {
                        Text(text = LanguageManager.t("save"), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordSecurityDialog(
    userEmail: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageManager.t("password_security"),
                        color = HeadingPurple,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✕",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(TaglineBackground)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(text = "Account Security Status: High", color = HeadingPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Signed in as: ${userEmail.ifBlank { "teststudent@example.com" }}", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Password encryption: bcrypt (salted & hashed)", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "To change your password, use the Account Recovery / Forgot Password screen from the login page with your email verification code.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGradientStart)
                ) {
                    Text(text = LanguageManager.t("close"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DataPermissionsDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageManager.t("data_permissions"),
                        color = HeadingPurple,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✕",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                PermissionItem(
                    name = "Microphone",
                    desc = "Required for speech analysis & vocal training drills",
                    icon = "🎙️",
                    status = "Active"
                )

                Spacer(modifier = Modifier.height(8.dp))

                PermissionItem(
                    name = "Camera / Gallery",
                    desc = "Used for performance uploads & photography practice",
                    icon = "📷",
                    status = "Active"
                )

                Spacer(modifier = Modifier.height(8.dp))

                PermissionItem(
                    name = "Notifications",
                    desc = "Used for scheduled practice reminders & streak alerts",
                    icon = "🔔",
                    status = "Active"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGradientStart)
                ) {
                    Text(text = LanguageManager.t("close"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PermissionItem(name: String, desc: String, icon: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TaglineBackground)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Text(text = icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = desc, color = TextSecondary, fontSize = 10.sp)
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF22C55E).copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(text = status, color = Color(0xFF16A34A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DownloadDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageManager.t("download_data"),
                        color = HeadingPurple,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✕",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Export a comprehensive package of your AI Skill Mirror journey including:\n\n• Assessed skill scores & benchmark radar\n• Daily practice session logs & speech metrics\n• Quiz attempt scores & roadmap progress\n• Badges & streak history",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = LanguageManager.t("cancel"), color = TextSecondary)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGradientStart)
                    ) {
                        Text(text = "Export Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HelpSupportDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageManager.t("help_support"),
                        color = HeadingPurple,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✕",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "AI Skill Mirror Support Center\n\nNeed assistance with your practice sessions, acoustic analysis, or roadmap quizzes?\n\n• Ask your AI Coach in the AI Coach tab for instant guidance.\n• Check your Weekly Progress and Milestone tabs for streak updates.\n• For technical support: support@skillmirror.simats.edu",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGradientStart)
                ) {
                    Text(text = LanguageManager.t("close"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// CUSTOM VECTOR ICONS
// ==========================================

@Composable
fun MoonIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.75f, h * 0.15f)
                quadraticTo(w * 0.30f, h * 0.20f, w * 0.30f, h * 0.65f)
                quadraticTo(w * 0.30f, h * 0.85f, w * 0.65f, h * 0.88f)
                quadraticTo(w * 0.85f, h * 0.88f, w * 0.85f, h * 0.80f)
                quadraticTo(w * 0.50f, h * 0.75f, w * 0.50f, h * 0.45f)
                quadraticTo(w * 0.50f, h * 0.25f, w * 0.75f, h * 0.15f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun GlobeIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.42f,
            center = Offset(w / 2, h / 2),
            style = Stroke(width = strokeWidth)
        )

        // Latitude line
        drawLine(color = color, start = Offset(w * 0.10f, h / 2), end = Offset(w * 0.90f, h / 2), strokeWidth = strokeWidth)

        // Longitude oval arc
        drawPath(
            path = Path().apply {
                moveTo(w / 2, h * 0.08f)
                quadraticTo(w * 0.25f, h / 2, w / 2, h * 0.92f)
                quadraticTo(w * 0.75f, h / 2, w / 2, h * 0.08f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun ClockIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.42f,
            center = Offset(w / 2, h / 2),
            style = Stroke(width = strokeWidth)
        )
        // Hour hand
        drawLine(
            color = color,
            start = Offset(w / 2, h / 2),
            end = Offset(w / 2, h * 0.28f),
            strokeWidth = strokeWidth
        )
        // Minute hand
        drawLine(
            color = color,
            start = Offset(w / 2, h / 2),
            end = Offset(w * 0.68f, h / 2),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun PaletteIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.42f,
            center = Offset(w / 2, h / 2),
            style = Stroke(width = strokeWidth)
        )
        drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(w * 0.35f, h * 0.35f))
        drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(w * 0.65f, h * 0.35f))
        drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(w * 0.50f, h * 0.65f))
    }
}

@Composable
fun VolumeIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.20f, h * 0.40f)
                lineTo(w * 0.40f, h * 0.40f)
                lineTo(w * 0.65f, h * 0.20f)
                lineTo(w * 0.65f, h * 0.80f)
                lineTo(w * 0.40f, h * 0.60f)
                lineTo(w * 0.20f, h * 0.60f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
        // Sound wave
        drawPath(
            path = Path().apply {
                moveTo(w * 0.78f, h * 0.35f)
                quadraticTo(w * 0.88f, h * 0.50f, w * 0.78f, h * 0.65f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun SpeakerIcon(size: Dp, color: Color) {
    VolumeIcon(size = size, color = color)
}
