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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.fruex.beerwall.ui.theme.GoldPrimary
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.min
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

    // Wave and Slosh Physics
    val currentTilt by rememberUpdatedState(tiltAngle)
    var wavePhase by remember { mutableStateOf(0f) }
    var sloshEnergy by remember { mutableStateOf(0f) }
    var lastTilt by remember { mutableStateOf(tiltAngle) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { frameTime ->
                if (lastFrameTime != 0L) {
                    val dt = (frameTime - lastFrameTime) / 1000f // seconds

                    // Update Bubbles
                    if (mugSize != IntSize.Zero) {
                        val surfaceYNorm = 1f - currentFillLevel
                        bubbles.forEach { bubble ->
                            bubble.normalizedY -= bubble.speed * dt
                            if (bubble.normalizedY < surfaceYNorm) {
                                bubble.normalizedY = 1f + Random.nextFloat() * 0.2f
                                bubble.normalizedX = Random.nextFloat()
                            }
                        }
                    }

                    // Update Wave
                    wavePhase += dt * 5f // Wave speed

                    // Update Slosh
                    val tiltDelta = currentTilt - lastTilt
                    sloshEnergy += abs(tiltDelta) * 20f // Sensitivity
                    sloshEnergy *= 0.92f // Decay/Dampening
                    if (sloshEnergy < 0.001f) sloshEnergy = 0f

                    lastTilt = currentTilt
                }
                lastFrameTime = frameTime
            }
        }
    }

    Canvas(modifier = modifier.onSizeChanged { mugSize = it }) {
        val width = size.width
        val height = size.height

        // Define the glass shape (Weizen/Pilsner style)
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
            val liquidHeight = height * fillLevel
            val surfaceY = height - liquidHeight

            // Pivot for rotation is the center of the surface line
            val pivotX = width / 2
            val pivotY = surfaceY

            val degrees = (tiltAngle * 180 / PI).toFloat()

            rotate(degrees = degrees, pivot = Offset(pivotX, pivotY)) {

                // Calculate Wave Parameters
                val baseAmplitude = width * 0.02f
                val sloshAmplitude = width * 0.05f * min(sloshEnergy, 1f)
                val totalAmplitude = baseAmplitude + sloshAmplitude
                // Use a smaller multiplier for frequency to get fewer, wider waves
                val waveFrequency = 2 * PI / width * 1.5

                // Generate Liquid Path
                val liquidPath = Path().apply {
                    val startX = -width
                    val endX = width * 2f
                    val step = 10f

                    moveTo(startX, height * 2f) // Deep bottom left
                    lineTo(endX, height * 2f)   // Deep bottom right

                    // Top surface (Right to Left)
                    var x = endX
                    while (x >= startX) {
                        // Using sine wave relative to x
                        val waveOffset = totalAmplitude * sin(x * waveFrequency + wavePhase).toFloat()
                        lineTo(x, surfaceY + waveOffset)
                        x -= step
                    }
                    close()
                }

                // Draw Beer
                drawPath(
                    path = liquidPath,
                    color = GoldPrimary
                )

                // Draw Bubbles
                if (fillLevel > 0f) {
                    val bubbleColor = Color.White.copy(alpha = 0.3f)
                    val highlightColor = Color.White.copy(alpha = 0.8f)

                    bubbles.forEach { bubble ->
                        val wobble = sin(bubble.normalizedY * 10f + bubble.offset) * (width * 0.02f)
                        val bxSafe = (bubble.normalizedX * 2f - 0.5f) * width + wobble
                        val by = bubble.normalizedY * height

                        if (by >= surfaceY) {
                            val radiusPx = bubble.radius * density
                            drawCircle(
                                color = bubbleColor,
                                radius = radiusPx,
                                center = Offset(bxSafe, by)
                            )
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
                    val foamHeight = height * 0.05f

                    val foamPath = Path().apply {
                        val startX = -width
                        val endX = width * 2f
                        val step = 10f

                        // Top Edge (Left to Right)
                        var x = startX
                        moveTo(startX, surfaceY - foamHeight + totalAmplitude * sin(startX * waveFrequency + wavePhase).toFloat())

                        while (x <= endX) {
                            val waveOffset = totalAmplitude * sin(x * waveFrequency + wavePhase).toFloat()
                            lineTo(x, surfaceY - foamHeight + waveOffset)
                            x += step
                        }

                        // Bottom Edge (Right to Left)
                        x = endX
                        while (x >= startX) {
                            val waveOffset = totalAmplitude * sin(x * waveFrequency + wavePhase).toFloat()
                            lineTo(x, surfaceY + waveOffset)
                            x -= step
                        }
                        close()
                    }

                    drawPath(
                        path = foamPath,
                        color = Color.White
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
