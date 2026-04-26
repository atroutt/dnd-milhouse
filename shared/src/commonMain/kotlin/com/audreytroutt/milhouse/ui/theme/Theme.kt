package com.audreytroutt.milhouse.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DndColorScheme = darkColorScheme(
    primary = Crimson,
    onPrimary = Parchment,
    primaryContainer = CrimsonDark,
    onPrimaryContainer = Parchment,
    secondary = Gold,
    onSecondary = DeepBrown,
    secondaryContainer = Color(0xFF4A3800),
    onSecondaryContainer = GoldLight,
    tertiary = ForestGreen,
    onTertiary = Parchment,
    background = DeepBrown,
    onBackground = OnDarkText,
    surface = DarkSurface,
    onSurface = OnDarkText,
    surfaceVariant = MediumSurface,
    onSurfaceVariant = ParchmentDark,
    outline = SubtleText,
    error = Color(0xFFCF6679)
)

@Composable
fun MilhouseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DndColorScheme,
        typography = Typography,
        content = content
    )
}
