package com.fruex.beerwall.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.theme.GoldPrimary
import kotlin.math.PI

/**
 * A beer mug component that displays a fill level and reacts to tilt.
 *
 * @param fillLevel The level of liquid (0.0 to 1.0).
 * @param tiltAngle The tilt angle in radians. Positive values tilt right.
 * @param modifier Modifier for the composable.
 */
@Composable
fun BeerMug(
    fillLevel: Float,
    tiltAngle: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Define the glass shape (Weizen/Pilsner style)
        // Narrower at bottom, wider at top, slight curve.
        val glassPath = Path().apply {
            val bottomWidth = width * 0.5f
            val topWidth = width * 0.9f
            val bottomStart = (width - bottomWidth) / 2
            val bottomEnd = bottomStart + bottomWidth
            val topStart = (width - topWidth) / 2
            val topEnd = topStart + topWidth

            moveTo(bottomStart, height - 10f) // Bottom Left

            // Left curve
            quadraticTo(
                0f, height * 0.4f, // Control point
                topStart, 0f // Top Left
            )

            // Top rim
            lineTo(topEnd, 0f)

            // Right curve
            quadraticTo(
                width, height * 0.4f, // Control point
                bottomEnd, height - 10f // Bottom Right
            )

            // Bottom base
            quadraticTo(
                width / 2, height, // Slight curve for bottom
                bottomStart, height - 10f
            )

            close()
        }

        // Draw the liquid
        clipPath(glassPath) {
            // Liquid fill calculation
            // fillLevel 0 -> y = height
            // fillLevel 1 -> y = 0
            val liquidHeight = height * fillLevel
            val surfaceY = height - liquidHeight

            // Pivot for rotation is the center of the surface line
            val pivotX = width / 2
            val pivotY = surfaceY

            // Convert radians to degrees for Canvas rotate
            // tiltAngle > 0 (tilted right) -> Liquid should stay horizontal, so it rotates -tiltAngle relative to glass?
            // If phone tilts right (clockwise), the liquid surface (relative to phone) rotates counter-clockwise.
            // So rotate by -tiltAngle.
            val degrees = -(tiltAngle * 180 / PI).toFloat()

            rotate(degrees = degrees, pivot = Offset(pivotX, pivotY)) {
                // Draw Beer
                drawRect(
                    color = GoldPrimary,
                    topLeft = Offset(-width, surfaceY), // Start far left to cover rotation
                    size = Size(width * 3, height * 2) // Large enough to cover
                )

                // Draw Foam
                if (fillLevel > 0.05f) {
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(-width, surfaceY - (height * 0.05f)), // 5% height foam
                        size = Size(width * 3, height * 0.05f)
                    )
                }
            }
        }

        // Draw Glass Highlights/Outline
        drawPath(
            path = glassPath,
            color = Color.White.copy(alpha = 0.5f),
            style = Stroke(width = 4.dp.toPx())
        )

        // Add a subtle shine/reflection
        val shinePath = Path().apply {
            moveTo(width * 0.25f, height * 0.8f)
            quadraticTo(
                width * 0.15f, height * 0.4f,
                width * 0.2f, height * 0.1f
            )
        }
        drawPath(
            path = shinePath,
            color = Color.White.copy(alpha = 0.3f),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}
