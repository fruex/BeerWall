package org.fruex.beerwall.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.fruex.beerwall.ui.theme.DarkBackground
import org.fruex.beerwall.ui.theme.GoldPrimary

@Composable
fun AuthBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .drawBehind {
                val gradientBrush = Brush.radialGradient(
                    colors = listOf(
                        GoldPrimary.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2f, 0f),
                    radius = size.maxDimension / 1.5f
                )
                drawRect(brush = gradientBrush)
            }
    ) {
        content()
    }
}
