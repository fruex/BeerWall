package com.fruex.beerwall

import android.os.Build
import android.util.Log

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    
    override fun log(message: String, tag: String, severity: LogSeverity) {
        val fullMessage = "${severity.prefix} $message"
        when (severity) {
            LogSeverity.DEBUG, LogSeverity.SUCCESS -> Log.d(tag, fullMessage)
            LogSeverity.INFO -> Log.i(tag, fullMessage)
            LogSeverity.WARN -> Log.w(tag, fullMessage)
            LogSeverity.ERROR -> Log.e(tag, fullMessage)
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()