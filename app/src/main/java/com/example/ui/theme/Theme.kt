package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Slate900,
    onPrimary = SurfaceWhite,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = SurfaceWhite,
    secondary = AmberVibrant,
    onSecondary = Slate900,
    secondaryContainer = AmberHighlight,
    onSecondaryContainer = AmberDeep,
    tertiary = EmeraldVibrant,
    onTertiary = SurfaceWhite,
    tertiaryContainer = EmeraldLightBg,
    onTertiaryContainer = EmeraldDeep,
    background = SlateBg,
    onBackground = Slate900,
    surface = SurfaceWhite,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate300,
    outlineVariant = Slate200
)

private val DarkColorScheme = darkColorScheme(
    primary = Slate900,
    onPrimary = SurfaceWhite,
    primaryContainer = Slate950,
    onPrimaryContainer = SurfaceWhite,
    secondary = AmberVibrant,
    onSecondary = Slate900,
    secondaryContainer = AmberDeep,
    onSecondaryContainer = AmberHighlight,
    tertiary = EmeraldVibrant,
    onTertiary = SurfaceWhite,
    background = Slate950,
    onBackground = SurfaceWhite,
    surface = Slate900,
    onSurface = SurfaceWhite,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    outlineVariant = Slate800
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
