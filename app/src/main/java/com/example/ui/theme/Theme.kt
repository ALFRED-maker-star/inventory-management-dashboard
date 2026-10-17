package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Slate700,
    onPrimary = SurfaceWhite,
    primaryContainer = Slate100,
    onPrimaryContainer = Slate900,
    secondary = AccentOrange,
    onSecondary = SurfaceWhite,
    secondaryContainer = AccentOrangeLight,
    onSecondaryContainer = AccentOrangeDark,
    tertiary = SuccessGreen,
    onTertiary = SurfaceWhite,
    tertiaryContainer = SuccessGreenLight,
    onTertiaryContainer = SuccessGreenDark,
    background = Slate50,
    onBackground = Slate900,
    surface = SurfaceWhite,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    outlineVariant = Slate300,
    error = DangerRed,
    onError = SurfaceWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = Slate300,
    onPrimary = Slate900,
    primaryContainer = Slate800,
    onPrimaryContainer = Slate100,
    secondary = AccentOrange,
    onSecondary = SurfaceWhite,
    secondaryContainer = Slate800,
    onSecondaryContainer = AccentOrangeLight,
    tertiary = SuccessGreen,
    onTertiary = SurfaceWhite,
    background = Slate900,
    onBackground = Slate50,
    surface = Slate800,
    onSurface = Slate50,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate300,
    outline = Slate600,
    outlineVariant = Slate700,
    error = DangerRed,
    onError = SurfaceWhite
)

@Composable
fun StockPilotTheme(
    darkTheme: Boolean = false, // Desktop admin tool aesthetic defaults to crisp clean light mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
