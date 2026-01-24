package com.fruex.beerwall

enum class LogSeverity {
    DEBUG, INFO, WARN, ERROR, SUCCESS;

    val prefix: String
        get() = when (this) {
            DEBUG -> "🔹 [DEBUG]"
            INFO -> "ℹ️ [INFO]"
            WARN -> "⚠️ [WARN]"
            ERROR -> "❌ [ERROR]"
            SUCCESS -> "✅ [SUCCESS]"
        }
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
