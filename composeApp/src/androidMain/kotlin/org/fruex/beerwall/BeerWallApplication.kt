package org.fruex.beerwall

import android.app.Application
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.auth.TokenManagerImpl
import org.fruex.beerwall.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class BeerWallApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@BeerWallApplication)
            modules(
                module {
                    single<TokenManager> { TokenManagerImpl(get()) }
                }
            )
        }
    }
}
