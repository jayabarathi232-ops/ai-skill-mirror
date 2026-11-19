package com.simats.aiskillmirror.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAppColors = staticCompositionLocalOf { ThemeManager.getColors() }

object AppTheme {
    val colors: AppThemeColors
        get() = ThemeManager.getColors()

    val isDark: Boolean
        get() = ThemeManager.isDarkMode
}

@Composable
fun AISkillsMirrorTheme(
    darkTheme: Boolean = ThemeManager.isDarkMode,
    content: @Composable () -> Unit
) {
    val colors = ThemeManager.getColors()
    val colorScheme = if (colors.isDark) {
        darkColorScheme(
            primary = colors.primaryStart,
            secondary = colors.accent,
            background = colors.background,
            surface = colors.surface,
            onPrimary = Color.White,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary
        )
    } else {
        lightColorScheme(
            primary = colors.primaryStart,
            secondary = colors.accent,
            background = colors.background,
            surface = colors.surface,
            onPrimary = Color.White,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary
        )
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
