package com.fruex.beerwall.ui.sensor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class IosDeviceOrientationSensor : DeviceOrientationSensor {
    override val roll: Flow<Float> = flowOf(0f)
}
