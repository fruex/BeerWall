package com.fruex.beerwall.di

import com.fruex.beerwall.auth.TokenManager
import com.fruex.beerwall.auth.TokenManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<TokenManager> { TokenManagerImpl(androidContext()) }
}
