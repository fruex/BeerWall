package org.fruex.beerwall

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val apiBaseUrl: String = "http://localhost:5254/api"
}

actual fun getPlatform(): Platform = AndroidPlatform()