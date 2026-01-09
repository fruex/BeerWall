package org.fruex.beerwall

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
