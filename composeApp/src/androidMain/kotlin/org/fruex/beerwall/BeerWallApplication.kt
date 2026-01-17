package org.fruex.beerwall

import android.app.Application
import org.fruex.beerwall.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class BeerWallApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@BeerWallApplication)
        }
    }
}
