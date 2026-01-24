package com.fruex.beerwall.ui.sensor

import kotlinx.coroutines.flow.Flow

interface DeviceOrientationSensor {
    /**
     * Emits the roll angle of the device in radians.
     * 0 means the device is upright.
     * Positive values mean the device is tilted to the right (clockwise).
     * Negative values mean the device is tilted to the left (counter-clockwise).
     */
    val roll: Flow<Float>
}
