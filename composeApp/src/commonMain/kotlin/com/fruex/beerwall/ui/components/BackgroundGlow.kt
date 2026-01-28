package com.fruex.beerwall.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.theme.GoldPrimary

@Composable
fun BackgroundGlow(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .drawBehind {
                val center = Offset(size.width / 2, -100f) // Slightly above top
                val radius = size.width * 1.0f
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoldPrimary.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius
                    )
                )
            }
    )
}
