package com.fruex.beerwall

import android.os.Build
import android.util.Log

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    
    override fun log(message: String, tag: String, severity: LogSeverity) {
        when (severity) {
            LogSeverity.DEBUG -> Log.d(tag, message)
            LogSeverity.INFO -> Log.i(tag, message)
            LogSeverity.WARN -> Log.w(tag, message)
            LogSeverity.ERROR -> Log.e(tag, message)
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()