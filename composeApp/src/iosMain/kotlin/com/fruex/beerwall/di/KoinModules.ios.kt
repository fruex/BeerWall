package com.fruex.beerwall.di

import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.data.local.TokenManagerImpl
import com.fruex.beerwall.ui.sensor.DeviceOrientationSensor
import com.fruex.beerwall.ui.sensor.IosDeviceOrientationSensor
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<TokenManager> { TokenManagerImpl() }
    single<DeviceOrientationSensor> { IosDeviceOrientationSensor() }
}
