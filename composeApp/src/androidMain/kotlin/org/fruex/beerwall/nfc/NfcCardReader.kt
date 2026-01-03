package org.fruex.beerwall.nfc

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.util.Log
import java.io.IOException

object NfcCardReader {
    private const val TAG = "NfcCardReader"
    private const val PAGE_OFFSET = 4
    private const val GUID_MIN_LENGTH = 16

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
                
                Log.d(TAG, "Raw bytes: ${bytes.joinToString(":") { "%02X".format(it) }}")
                
                bytes.toGuidString()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading card IO", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error reading card", e)
            null
        }
    }

    private fun ByteArray.toGuidString(): String {
        return try {
            if (this.size < GUID_MIN_LENGTH) throw IllegalArgumentException("Insufficient bytes for GUID")

            // Convert bytes to C# GUID format (mixed-endian)
            // Format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

            // First 4 bytes (little-endian) - Data1
            val part1 = "%02x%02x%02x%02x".format(this[3], this[2], this[1], this[0])

            // Next 2 bytes (little-endian) - Data2
            val part2 = "%02x%02x".format(this[5], this[4])

            // Next 2 bytes (little-endian) - Data3
            val part3 = "%02x%02x".format(this[7], this[6])

            // Next 2 bytes (big-endian) - Data4[0-1]
            val part4 = "%02x%02x".format(this[8], this[9])

            // Last 6 bytes (big-endian) - Data4[2-7]
            val part5 = "%02x%02x%02x%02x%02x%02x".format(this[10], this[11], this[12], this[13], this[14], this[15])

            "$part1-$part2-$part3-$part4-$part5"
        } catch (e: Exception) {
            Log.e(TAG, "Error creating GUID", e)
            // Fallback to raw hex string if conversion fails
            this.joinToString(":") { "%02X".format(it) }
        }
    }
}