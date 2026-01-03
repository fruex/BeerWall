package org.fruex.beerwall

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val apiBaseUrl: String = "http://localhost:5187"
//    override val apiBaseUrl: String = "http://10.0.2.2:5187"
}

actual fun getPlatform(): Platform = IOSPlatform()