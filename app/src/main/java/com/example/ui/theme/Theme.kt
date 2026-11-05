package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MazoumColorScheme = darkColorScheme(
    primary = MazoumCyan,
    onPrimary = Color(0xFF001B2E),
    primaryContainer = Color(0xFF003852),
    onPrimaryContainer = Color(0xFFBBE9FF),

    secondary = MazoumNeonPurple,
    onSecondary = Color(0xFF28004C),
    secondaryContainer = Color(0xFF491279),
    onSecondaryContainer = Color(0xFFF1DBFF),

    tertiary = MazoumElectricBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF0D3B94),
    onTertiaryContainer = Color(0xFFD6E3FF),

    background = MazoumNight,
    onBackground = MazoumTextPrimary,

    surface = MazoumDarkSurface,
    onSurface = MazoumTextPrimary,
    surfaceVariant = MazoumCardSurface,
    onSurfaceVariant = MazoumTextSecondary,

    outline = MazoumCardBorder,
    outlineVariant = Color(0xFF1B2A4A),

    error = MazoumError,
    onError = Color.White
)

@Composable
fun MazoumTechTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MazoumColorScheme,
        typography = Typography,
        content = content
    )
}
