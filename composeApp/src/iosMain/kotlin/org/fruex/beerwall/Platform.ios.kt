package org.fruex.beerwall

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
//    override val apiBaseUrl: String = "http://localhost:5254/api"
    override val apiBaseUrl: String = "https://beerwall-apitest-cjcfgfehh9grhne5.polandcentral-01.azurewebsites.net/api"
}

actual fun getPlatform(): Platform = IOSPlatform()