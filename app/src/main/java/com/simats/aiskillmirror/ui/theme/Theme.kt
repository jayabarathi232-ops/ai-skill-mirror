package com.simats.aiskillmirror.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = lightColorScheme(
    primary = PrimaryGradientStart,
    secondary = AccentPurple,
    background = DarkBackground,
    surface = SurfaceDark,
    onPrimary = Color(0xFFFFFFFF),
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun AISkillsMirrorTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}
