package org.fruex.beerwall

import kotlin.reflect.KClass

enum class LogSeverity {
    DEBUG, INFO, WARN, ERROR
}

interface Platform {
    val name: String
    fun log(message: String, tag: String = "BeerWall", severity: LogSeverity = LogSeverity.DEBUG)
}

expect fun getPlatform(): Platform

/**
 * Extension function dla łatwiejszego logowania z kontekstem klasy.
 * Użycie: platform.log("Wiadomość", this)
 */
fun Platform.log(message: String, context: Any, severity: LogSeverity = LogSeverity.DEBUG) {
    val tag = context::class.simpleName ?: "BeerWall"
    log(message, tag, severity)
}
