package com.fruex.beerwall

import android.os.Build
import android.util.Log

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    
    override fun log(message: String, tag: String, severity: LogSeverity) {
        val formattedMessage = "${severity.prefix} $message"

        when (severity) {
            LogSeverity.DEBUG -> Log.d(tag, formattedMessage)
            LogSeverity.INFO -> Log.i(tag, formattedMessage)
            LogSeverity.WARN -> Log.w(tag, formattedMessage)
            LogSeverity.ERROR -> Log.e(tag, formattedMessage)
            LogSeverity.SUCCESS -> Log.d(tag, formattedMessage)
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()
