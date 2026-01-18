package org.fruex.beerwall

import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.auth.TokenManagerImpl
import org.fruex.beerwall.di.initKoin
import org.koin.dsl.module

fun initKoinIos() {
    initKoin {
        modules(
            module {
                single<TokenManager> { TokenManagerImpl() }
            }
        )
    }
}
