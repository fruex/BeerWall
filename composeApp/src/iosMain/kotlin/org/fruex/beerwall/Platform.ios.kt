package org.fruex.beerwall

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val apiBaseUrl: String = "http://localhost:5254/api"
}

actual fun getPlatform(): Platform = IOSPlatform()