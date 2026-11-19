package com.simats.aiskillmirror.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * AI Skills Mirror — Dynamic Brand Palette
 * Automatically reactive to Dark Mode and selected Theme Palette!
 */

// Background & Surfaces
val DarkBackground: Color
    get() = ThemeManager.getColors().background

val SurfaceDark: Color
    get() = ThemeManager.getColors().surface

val CardBackground: Color
    get() = ThemeManager.getColors().cardBackground

val CardBorder: Color
    get() = ThemeManager.getColors().cardBorder

val OrbitRing: Color
    get() = if (ThemeManager.isDarkMode) Color(0xFF3E3656) else Color(0xFFD8C7F8)

val TaglineBackground: Color
    get() = ThemeManager.getColors().taglineBackground

val TaglineText: Color
    get() = ThemeManager.getColors().taglineText

// Primary Gradient
val PrimaryGradientStart: Color
    get() = ThemeManager.getColors().primaryStart

val PrimaryGradientEnd: Color
    get() = ThemeManager.getColors().primaryEnd

val CenterCardGradientStart: Color
    get() = ThemeManager.getColors().primaryStart

val CenterCardGradientEnd: Color
    get() = ThemeManager.getColors().primaryEnd

// Accent Colors & Indicators
val GreenNode: Color = Color(0xFF16A34A)
val CyanNode: Color = Color(0xFF0284C7)

val BadgeBackground: Color
    get() = ThemeManager.getColors().badgeBackground

val BadgeBorder: Color
    get() = ThemeManager.getColors().badgeBorder

// Typography
val HeadingPurple: Color
    get() = ThemeManager.getColors().heading

val TextPrimary: Color
    get() = ThemeManager.getColors().textPrimary

val TextSecondary: Color
    get() = ThemeManager.getColors().textSecondary

val AccentPurple: Color
    get() = ThemeManager.getColors().accent

val LightPurple: Color
    get() = ThemeManager.getColors().badgeBackground
