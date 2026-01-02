package org.fruex.beerwall

interface Platform {
    val name: String
    val apiBaseUrl: String
}

expect fun getPlatform(): Platform