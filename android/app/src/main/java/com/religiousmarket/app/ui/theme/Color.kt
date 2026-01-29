package com.religiousmarket.app.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors - Dark Theme
val Primary = Color(0xFF4A7CC9)        // Lighter blue for dark theme
val PrimaryDark = Color(0xFF1B3A6B)     // Original dark blue
val PrimaryLight = Color(0xFF6B9AE8)

// Accent Colors
val Accent = Color(0xFFD4AF37)
val AccentLight = Color(0xFFE8CF7A)

// Religion Colors
val Orthodox = Color(0xFFE85A6F)        // Lighter red for dark theme
val OrthodoxDark = Color(0xFFC41E3A)
val Islam = Color(0xFF4CAF50)           // Lighter green for dark theme
val IslamDark = Color(0xFF006400)
val Judaism = Color(0xFF5BA8FF)         // Lighter blue for dark theme
val JudaismDark = Color(0xFF1E90FF)

// Dark Theme Background Colors
val BackgroundDark = Color(0xFF121212)
val SurfaceDark = Color(0xFF1E1E1E)
val CardBackgroundDark = Color(0xFF2D2D2D)

// Light Theme Background Colors (fallback)
val BackgroundLight = Color(0xFFF8F9FA)
val SurfaceLight = Color(0xFFFFFFFF)
val CardBackgroundLight = Color(0xFFFFFFFF)

// Text Colors - Dark Theme
val TextPrimaryDark = Color(0xFFE8E8E8)
val TextSecondaryDark = Color(0xFFB0B0B0)
val TextHintDark = Color(0xFF808080)
val TextOnPrimary = Color(0xFFFFFFFF)

// Text Colors - Light Theme (fallback)
val TextPrimaryLight = Color(0xFF1A1A1A)
val TextSecondaryLight = Color(0xFF666666)
val TextHintLight = Color(0xFF999999)

// Status Colors
val Success = Color(0xFF4CAF50)
val Warning = Color(0xFFFFC107)
val Error = Color(0xFFEF5350)

// Other
val Divider = Color(0xFF3D3D3D)
val DividerLight = Color(0xFFE0E0E0)
val Star = Color(0xFFF5A623)

// Composable-accessible colors - will be set based on theme
object AppColors {
    var isDarkTheme = true

    val Background: Color get() = if (isDarkTheme) BackgroundDark else BackgroundLight
    val Surface: Color get() = if (isDarkTheme) SurfaceDark else SurfaceLight
    val CardBackground: Color get() = if (isDarkTheme) CardBackgroundDark else CardBackgroundLight
    val TextPrimary: Color get() = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
    val TextSecondary: Color get() = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
    val TextHint: Color get() = if (isDarkTheme) TextHintDark else TextHintLight
    val DividerColor: Color get() = if (isDarkTheme) Divider else DividerLight
}
