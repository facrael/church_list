package com.religiousmarket.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Local composition for app-specific colors
data class AppColorScheme(
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textHint: Color,
    val divider: Color
)

val LocalAppColors = staticCompositionLocalOf {
    AppColorScheme(
        background = BackgroundDark,
        surface = SurfaceDark,
        cardBackground = CardBackgroundDark,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textHint = TextHintDark,
        divider = Divider
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = TextOnPrimary,
    secondary = Accent,
    onSecondary = PrimaryDark,
    secondaryContainer = AccentLight,
    onSecondaryContainer = PrimaryDark,
    tertiary = Orthodox,
    onTertiary = TextOnPrimary,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardBackgroundDark,
    onSurfaceVariant = TextSecondaryDark,
    error = Error,
    onError = TextOnPrimary,
    outline = Divider
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryDark,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = TextOnPrimary,
    secondary = Accent,
    onSecondary = TextPrimaryLight,
    secondaryContainer = AccentLight,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = OrthodoxDark,
    onTertiary = TextOnPrimary,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CardBackgroundLight,
    onSurfaceVariant = TextSecondaryLight,
    error = Error,
    onError = TextOnPrimary,
    outline = DividerLight
)

@Composable
fun ReligiousMarketTheme(
    darkTheme: Boolean = true, // Always dark theme by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val appColors = if (darkTheme) {
        AppColorScheme(
            background = BackgroundDark,
            surface = SurfaceDark,
            cardBackground = CardBackgroundDark,
            textPrimary = TextPrimaryDark,
            textSecondary = TextSecondaryDark,
            textHint = TextHintDark,
            divider = Divider
        )
    } else {
        AppColorScheme(
            background = BackgroundLight,
            surface = SurfaceLight,
            cardBackground = CardBackgroundLight,
            textPrimary = TextPrimaryLight,
            textSecondary = TextSecondaryLight,
            textHint = TextHintLight,
            divider = DividerLight
        )
    }

    // Update global AppColors
    AppColors.isDarkTheme = darkTheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) BackgroundDark.toArgb() else PrimaryDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
