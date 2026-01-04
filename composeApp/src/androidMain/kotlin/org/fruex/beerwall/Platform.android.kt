package org.fruex.beerwall

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
//    override val apiBaseUrl: String = "http://localhost:5254/api"
    override val apiBaseUrl: String = "https://beerwall-apitest-cjcfgfehh9grhne5.polandcentral-01.azurewebsites.net/api"
}

actual fun getPlatform(): Platform = AndroidPlatform()