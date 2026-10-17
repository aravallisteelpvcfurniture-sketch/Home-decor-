package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    onPrimary = Color(0xFF0A192F),
    primaryContainer = NavySecondary,
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = GoldAccent,
    onSecondary = Color(0xFF3F2E00),
    secondaryContainer = Color(0xFF5B4300),
    onSecondaryContainer = GoldLight,
    tertiary = SteelSilver,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFF1F5F9),
    outline = Color(0xFF475569)
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = NavySecondary,
    onPrimaryContainer = Color.White,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = GoldLight,
    onSecondaryContainer = GoldDark,
    tertiary = SteelSilver,
    onTertiary = Color.White,
    background = SurfaceLight,
    surface = Color.White,
    surfaceVariant = SteelLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = SteelBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
