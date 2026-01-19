package com.fruex.beerwall.di

import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger

fun initKoin() {
    startKoin {
        logger(PrintLogger(Level.INFO))
        modules(appModules())
    }
}
