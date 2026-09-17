package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TorqDarkColorScheme = darkColorScheme(
    primary = TorqGold,
    onPrimary = TorqTextDark,
    primaryContainer = TorqGoldDark,
    onPrimaryContainer = TorqTextPrimary,
    secondary = TorqSlateGrey,
    onSecondary = TorqNavyBackground,
    secondaryContainer = TorqSlateDark,
    onSecondaryContainer = TorqTextPrimary,
    tertiary = TorqSky,
    onTertiary = TorqNavyBackground,
    background = TorqNavyBackground,
    onBackground = TorqTextPrimary,
    surface = TorqNavySurface,
    onSurface = TorqTextPrimary,
    surfaceVariant = TorqNavyCard,
    onSurfaceVariant = TorqTextSecondary,
    outline = TorqNavyCardBorder,
    outlineVariant = TorqSlateBorder,
    error = TorqRed,
    onError = Color.White
)

@Composable
fun TorqfixTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TorqDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) = TorqfixTheme(content = content)

