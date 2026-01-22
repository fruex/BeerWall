package com.fruex.beerwall.di

import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.data.local.TokenManagerImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<TokenManager> { TokenManagerImpl() }
}
