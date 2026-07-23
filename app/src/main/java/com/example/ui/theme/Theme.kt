package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GymPrimary,
    onPrimary = GymTextPrimary,
    primaryContainer = GymPrimaryVariant,
    onPrimaryContainer = GymTextPrimary,
    secondary = GymAccent,
    onSecondary = GymBackground,
    secondaryContainer = GymAccentVariant,
    onSecondaryContainer = GymTextPrimary,
    background = GymBackground,
    onBackground = GymTextPrimary,
    surface = GymCardBackground,
    onSurface = GymTextPrimary,
    surfaceVariant = GymCardElevated,
    onSurfaceVariant = GymTextSecondary,
    outline = GymGlassBorder,
    error = GymError,
    onError = GymTextPrimary
)

@Composable
fun GymFlowTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = GymBackground.toArgb()
            window.navigationBarColor = GymBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
