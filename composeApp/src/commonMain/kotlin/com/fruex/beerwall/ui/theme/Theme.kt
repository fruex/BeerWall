package com.fruex.beerwall.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BeerWallColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = DarkBackground,
    primaryContainer = GoldDark,
    onPrimaryContainer = TextPrimary,
    secondary = GoldLight,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = DarkerBackground,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = TextPrimary,
)

@Composable
fun BeerWallTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BeerWallColorScheme,
        typography = Typography,
        shapes = BeerWallShapes,
        content = content
    )
}
