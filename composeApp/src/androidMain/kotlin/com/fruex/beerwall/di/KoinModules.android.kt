package com.fruex.beerwall.di

import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.data.local.TokenManagerImpl
import com.fruex.beerwall.ui.sensor.AndroidDeviceOrientationSensor
import com.fruex.beerwall.ui.sensor.DeviceOrientationSensor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<TokenManager> { TokenManagerImpl(androidContext()) }
    single<DeviceOrientationSensor> { AndroidDeviceOrientationSensor(androidContext()) }
}
