package com.fruex.beerwall

import android.app.Application
import com.fruex.beerwall.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BeerWallApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BeerWallApplication)
            modules(appModules())
        }
    }
}
