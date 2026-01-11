package org.fruex.beerwall

import platform.UIKit.UIDevice
import platform.Foundation.NSLog

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    
    override fun log(message: String, tag: String, severity: LogSeverity) {
        val prefix = when (severity) {
            LogSeverity.DEBUG -> "🔹 [DEBUG]"
            LogSeverity.INFO -> "ℹ️ [INFO]"
            LogSeverity.WARN -> "⚠️ [WARN]"
            LogSeverity.ERROR -> "❌ [ERROR]"
        }
        NSLog("$prefix [$tag] $message")
    }
}

actual fun getPlatform(): Platform = IOSPlatform()