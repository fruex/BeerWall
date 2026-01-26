package com.fruex.beerwall.nfc

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.getPlatform
import com.fruex.beerwall.log
import java.io.IOException

object NfcCardReader {
    private const val PAGE_OFFSET = 4
    private const val GUID_MIN_LENGTH = 16
    private val platform = getPlatform()

    /**
     * Reads the card ID from a Mifare Ultralight tag.
     * Reads 4 pages starting from page 4 and converts the bytes to a GUID string.
     */
    fun readCardId(tag: Tag): String? {
        val mifareUltralight = MifareUltralight.get(tag) ?: return null

        return try {
            mifareUltralight.use { mifare ->
                mifare.connect()
                
                // Read page 4 (reads 4 pages starting from page 4: pages 4, 5, 6, 7)
                val bytes = mifare.readPages(PAGE_OFFSET)
                
                platform.log("Raw bytes: ${bytes.joinToString(":") { "%02X".format(it) }}", this, LogSeverity.DEBUG)
                
                bytes.toGuidString()
            }
        } catch (e: IOException) {
            platform.log("Error reading card IO: ${e.message}", this, LogSeverity.ERROR)
            null
        } catch (e: Exception) {
            platform.log("Error reading card: ${e.message}", this, LogSeverity.ERROR)
            null
        }
    }
}