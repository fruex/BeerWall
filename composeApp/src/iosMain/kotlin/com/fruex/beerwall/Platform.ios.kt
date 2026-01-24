package com.fruex.beerwall

import platform.UIKit.UIDevice
import platform.Foundation.NSLog

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    
    override fun log(message: String, tag: String, severity: LogSeverity) {
        NSLog("${severity.prefix} [$tag] $message")
    }
}

actual fun getPlatform(): Platform = IOSPlatform()
