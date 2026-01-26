package com.fruex.beerwall.nfc

/**
 * Converts a byte array to a GUID string in the mixed-endian format required by the backend.
 * Format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
 * Data1 (4 bytes): Little-endian
 * Data2 (2 bytes): Little-endian
 * Data3 (2 bytes): Little-endian
 * Data4 (2 bytes): Big-endian
 * Data5 (6 bytes): Big-endian
 */
fun ByteArray.toGuidString(): String {
    if (this.size < 16) throw IllegalArgumentException("Insufficient bytes for GUID (needs 16, got ${this.size})")

    // Helper to format byte to 2-char hex string
    fun Byte.toHex(): String {
        val u = this.toInt() and 0xFF
        val hex = u.toString(16)
        return if (hex.length == 1) "0$hex" else hex
    }

    // First 4 bytes (little-endian) - Data1
    // Android: this[3], this[2], this[1], this[0]
    val part1 = "${this[3].toHex()}${this[2].toHex()}${this[1].toHex()}${this[0].toHex()}"

    // Next 2 bytes (little-endian) - Data2
    // Android: this[5], this[4]
    val part2 = "${this[5].toHex()}${this[4].toHex()}"

    // Next 2 bytes (little-endian) - Data3
    // Android: this[7], this[6]
    val part3 = "${this[7].toHex()}${this[6].toHex()}"

    // Next 2 bytes (big-endian) - Data4[0-1]
    // Android: this[8], this[9]
    val part4 = "${this[8].toHex()}${this[9].toHex()}"

    // Last 6 bytes (big-endian) - Data4[2-7]
    // Android: this[10], this[11], this[12], this[13], this[14], this[15]
    val part5 = "${this[10].toHex()}${this[11].toHex()}${this[12].toHex()}${this[13].toHex()}${this[14].toHex()}${this[15].toHex()}"

    return "$part1-$part2-$part3-$part4-$part5"
}
