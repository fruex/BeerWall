package com.fruex.beerwall.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.theme.GoldPrimary
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

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
    // Generate bubbles once. Position is normalized (0..1).
    val bubbles = remember {
        List(40) {
            Bubble(
                radius = Random.nextFloat() * 4f + 2f, // Base radius
                speed = Random.nextFloat() * 0.2f + 0.05f, // Speed in normalized height per second
                offset = Random.nextFloat() * 100f
            )
        }
    }

    var mugSize by remember { mutableStateOf(IntSize.Zero) }
    var lastFrameTime by remember { mutableStateOf(0L) }
    val currentFillLevel by rememberUpdatedState(fillLevel)

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { frameTime ->
                if (lastFrameTime != 0L && mugSize != IntSize.Zero) {
                    val dt = (frameTime - lastFrameTime) / 1000f // seconds

                    // Surface is at (1 - fillLevel) in normalized coordinates (0 is top, 1 is bottom)
                    // Wait, usually Y=0 is top.
                    // Liquid height = h * fillLevel.
                    // Surface Y px = h - liquidHeight = h * (1 - fillLevel).
                    // Normalized Surface Y = 1 - fillLevel.
                    val surfaceYNorm = 1f - currentFillLevel

                    bubbles.forEach { bubble ->
                        // Move up (decrease Y)
                        bubble.normalizedY -= bubble.speed * dt

                        // Reset if above surface
                        if (bubble.normalizedY < surfaceYNorm) {
                            bubble.normalizedY = 1f + Random.nextFloat() * 0.2f // Reset below bottom slightly
                            bubble.normalizedX = Random.nextFloat()
                        }
                    }
                }
                lastFrameTime = frameTime
            }
        }
    }

    Canvas(modifier = modifier.onSizeChanged { mugSize = it }) {
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
            // tiltAngle > 0 (tilted right) -> Liquid follows gravity and tilts right
            // tiltAngle < 0 (tilted left) -> Liquid follows gravity and tilts left
            val degrees = (tiltAngle * 180 / PI).toFloat()

            rotate(degrees = degrees, pivot = Offset(pivotX, pivotY)) {
                // Draw Beer
                drawRect(
                    color = GoldPrimary,
                    topLeft = Offset(-width, surfaceY), // Start far left to cover rotation
                    size = Size(width * 3, height * 2) // Large enough to cover
                )

                // Draw Bubbles
                // Only draw if there is liquid
                if (fillLevel > 0f) {
                    val bubbleColor = Color.White.copy(alpha = 0.3f)
                    val highlightColor = Color.White.copy(alpha = 0.8f)

                    bubbles.forEach { bubble ->
                        // Calculate position
                        // x includes wobble
                        val wobble = sin(bubble.normalizedY * 10f + bubble.offset) * (width * 0.02f)
                        val bx = (bubble.normalizedX * width * 3) - width + wobble // Map 0..1 to -width..2width to match liquid rect width
                        // Wait, logic above mapped 0..1 for X.
                        // Liquid rect is -width .. 2width. Width is 3*width.
                        // Let's just map normalizedX to 0..width (glass width) for simplicity,
                        // as bubbles outside glass are clipped anyway.
                        // But if we tilt, we might see sides.
                        // Let's map to -0.5*width .. 1.5*width to be safe.
                        val bxSafe = (bubble.normalizedX * 2f - 0.5f) * width + wobble

                        val by = bubble.normalizedY * height

                        // Check if bubble is roughly within liquid range (surfaceY .. height)
                        // bubble.normalizedY is 0..1. 0 is top.
                        // We reset when < surfaceYNorm.
                        // So effectively we draw everything.

                        // Only draw if below surface (extra check for safety/visuals)
                        if (by >= surfaceY) {
                            val radiusPx = bubble.radius * density // Scale by density if we assume radius is roughly dp-like?
                            // Or just use raw px from bubble.
                            // Let's treat bubble.radius as dp.

                            drawCircle(
                                color = bubbleColor,
                                radius = radiusPx,
                                center = Offset(bxSafe, by)
                            )

                            // Highlight (stylized)
                            drawCircle(
                                color = highlightColor,
                                radius = radiusPx * 0.3f,
                                center = Offset(bxSafe - radiusPx * 0.3f, by - radiusPx * 0.3f)
                            )
                        }
                    }
                }

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

private class Bubble(
    val radius: Float,
    val speed: Float,
    val offset: Float
) {
    var normalizedX by mutableStateOf(Random.nextFloat())
    var normalizedY by mutableStateOf(Random.nextFloat())
}
