package com.simats.aiskillmirror.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.simats.aiskillmirror.data.network.UserSession

enum class ThemePalette(
    val id: String,
    val title: String,
    val primary: Color,
    val primaryEnd: Color,
    val accent: Color,
    val lightBg: Color,
    val darkBg: Color
) {
    LAVENDER(
        id = "Lavender",
        title = "Lavender Purple",
        primary = Color(0xFF7545C8),
        primaryEnd = Color(0xFF9353E2),
        accent = Color(0xFF7545C8),
        lightBg = Color(0xFFF5F0FF),
        darkBg = Color(0xFF13101C)
    ),
    OCEAN(
        id = "Ocean Blue",
        title = "Ocean Blue",
        primary = Color(0xFF2563EB),
        primaryEnd = Color(0xFF3B82F6),
        accent = Color(0xFF2563EB),
        lightBg = Color(0xFFF0F5FF),
        darkBg = Color(0xFF0F172A)
    ),
    EMERALD(
        id = "Emerald",
        title = "Emerald Green",
        primary = Color(0xFF059669),
        primaryEnd = Color(0xFF10B981),
        accent = Color(0xFF059669),
        lightBg = Color(0xFFF0FDF4),
        darkBg = Color(0xFF062016)
    ),
    SUNSET(
        id = "Sunset",
        title = "Sunset Orange",
        primary = Color(0xFFEA580C),
        primaryEnd = Color(0xFFF97316),
        accent = Color(0xFFEA580C),
        lightBg = Color(0xFFFFF7ED),
        darkBg = Color(0xFF1F140D)
    ),
    ROSE(
        id = "Rose",
        title = "Rose Pink",
        primary = Color(0xFFE11D48),
        primaryEnd = Color(0xFFF43F5E),
        accent = Color(0xFFE11D48),
        lightBg = Color(0xFFFFF1F2),
        darkBg = Color(0xFF1F0E13)
    );

    companion object {
        fun fromId(id: String): ThemePalette {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) }
                ?: LAVENDER
        }
    }
}

data class AppThemeColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val heading: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val primaryStart: Color,
    val primaryEnd: Color,
    val accent: Color,
    val taglineBackground: Color,
    val taglineText: Color,
    val badgeBackground: Color,
    val badgeBorder: Color,
    val inputBackground: Color,
    val divider: Color
)

object ThemeManager {
    private const val PREFS_NAME = "skill_mirror_theme_prefs"
    private const val KEY_IS_DARK = "pref_is_dark_mode"
    private const val KEY_PALETTE = "pref_theme_palette"

    var isDarkMode by mutableStateOf(false)
        private set

    var currentPalette by mutableStateOf(ThemePalette.LAVENDER)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkMode = prefs.getBoolean(KEY_IS_DARK, false)
        val paletteName = prefs.getString(KEY_PALETTE, ThemePalette.LAVENDER.id) ?: ThemePalette.LAVENDER.id
        currentPalette = ThemePalette.fromId(paletteName)
    }

    fun setDarkMode(context: Context?, enabled: Boolean) {
        isDarkMode = enabled
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putBoolean(KEY_IS_DARK, enabled)
            ?.apply()
    }

    fun setPalette(context: Context?, palette: ThemePalette) {
        currentPalette = palette
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(KEY_PALETTE, palette.id)
            ?.apply()
    }

    fun getColors(): AppThemeColors {
        return if (isDarkMode) {
            AppThemeColors(
                isDark = true,
                background = currentPalette.darkBg,
                surface = Color(0xFF1E1B29),
                cardBackground = Color(0xFF1E1B29),
                cardBorder = Color(0xFF2E2744),
                heading = Color(0xFFEDE9FE),
                textPrimary = Color(0xFFF8F6FF),
                textSecondary = Color(0xFFA79FC4),
                primaryStart = currentPalette.primary,
                primaryEnd = currentPalette.primaryEnd,
                accent = currentPalette.accent,
                taglineBackground = Color(0xFF2E2545),
                taglineText = Color(0xFFD8C7F8),
                badgeBackground = Color(0xFF2E2545),
                badgeBorder = Color(0xFF3F355C),
                inputBackground = Color(0xFF272235),
                divider = Color(0xFF2E2744)
            )
        } else {
            AppThemeColors(
                isDark = false,
                background = currentPalette.lightBg,
                surface = Color(0xFFFFFFFF),
                cardBackground = Color(0xFFFFFFFF),
                cardBorder = Color(0xFFE9DDFF),
                heading = Color(0xFF3B1869),
                textPrimary = Color(0xFF25213A),
                textSecondary = Color(0xFF6B6684),
                primaryStart = currentPalette.primary,
                primaryEnd = currentPalette.primaryEnd,
                accent = currentPalette.accent,
                taglineBackground = Color(0xFFE9DDFF),
                taglineText = currentPalette.primary,
                badgeBackground = Color(0xFFE9DDFF),
                badgeBorder = Color(0xFFD8C7F8),
                inputBackground = Color(0xFFF4EFFC),
                divider = Color(0xFFEDE5FA)
            )
        }
    }
}
