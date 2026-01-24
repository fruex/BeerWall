package com.fruex.beerwall.ui.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.atan2
import kotlin.math.sqrt

class AndroidDeviceOrientationSensor(
    private val context: Context
) : DeviceOrientationSensor {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    override val roll: Flow<Float> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                        val rotationMatrix = FloatArray(9)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)

                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(rotationMatrix, orientation)

                        // orientation[0] = Azimuth (z)
                        // orientation[1] = Pitch (x)
                        // orientation[2] = Roll (y)

                        // We want Roll.
                        // However, depending on device natural orientation, this might vary.
                        // Assuming standard portrait phone:
                        // Roll is rotation around Y axis? No.
                        // Azimuth (Z), Pitch (X), Roll (Y).
                        // Android docs:
                        // Azimuth: Rotation around -Z
                        // Pitch: Rotation around -X
                        // Roll: Rotation around Y axis.

                        // Let's verify:
                        // Tilted left (counter-clockwise): Roll is positive?
                        // Tilted right (clockwise): Roll is negative?
                        // We want positive for right tilt (clockwise) to match standard math usually?
                        // The interface says: "Positive values mean the device is tilted to the right (clockwise)."
                        // Android Roll:
                        // "Positive values indicate that the bottom edge of the device turns up and the top edge turns down" -> Wait, that's Pitch?

                        // Let's re-read Android docs for getOrientation:
                        // values[1]: Pitch, angle of rotation about the x axis.
                        // values[2]: Roll, angle of rotation about the y axis.
                        // "Positive values indicate a counter-clockwise turn..." -> So negative for clockwise.

                        // If interface expects Positive = Clockwise (Right tilt), then we need -values[2].

                        trySend(-orientation[2])
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No-op
            }
        }

        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (sensor != null) {
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        } else {
            // Fallback to simpler method if needed, or just emit 0
            trySend(0f)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
