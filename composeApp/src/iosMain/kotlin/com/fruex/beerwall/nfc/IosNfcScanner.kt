package com.fruex.beerwall.nfc

import com.fruex.beerwall.domain.repository.NfcRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.cinterop.*
import platform.CoreNFC.*
import platform.Foundation.*
import platform.darwin.NSObject

class IosNfcScanner(
    private val nfcRepository: NfcRepository
) : NSObject(), NFCTagReaderSessionDelegateProtocol {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var session: NFCTagReaderSession? = null

    fun startScanning() {
        if (session != null) return

        // Polling for ISO14443 (which covers Mifare Ultralight)
        session = NFCTagReaderSession(
            pollingOption = NFCPollingISO14443,
            delegate = this,
            queue = null // Main queue by default if null? Or serial queue.
        ).apply {
            alertMessage = "Zbliż kartę do górnej części telefonu"
            beginSession()
        }
    }

    fun stopScanning() {
        session?.invalidateSession()
        session = null
    }

    override fun tagReaderSessionDidBecomeActive(session: NFCTagReaderSession) {
        // Session is ready
    }

    override fun tagReaderSession(session: NFCTagReaderSession, didInvalidateWithError: NSError) {
        // If the error is user cancel, we might want to just stop.
        // NFCReaderError.Code.readerSessionInvalidationErrorUserCanceled
        this.session = null
        scope.launch {
             // Optionally tell repo to stop scanning if it was a system error
             // But usually we just let the UI state drive.
             // If user cancelled, maybe we should toggle the switch off?
             // For now, keep it simple.
        }
    }

    override fun tagReaderSession(session: NFCTagReaderSession, didDetectTags: List<*>) {
        if (didDetectTags.size > 1) {
            session.alertMessage = "Wykryto więcej niż jedną kartę. Zbliż tylko jedną."
            session.restartPolling()
            return
        }

        val tag = didDetectTags.firstOrNull() as? NFCTagProtocol ?: return

        session.connectToTag(tag) { error ->
            if (error != null) {
                session.invalidateSession(errorMessage = "Błąd połączenia: ${error.localizedDescription}")
                return@connectToTag
            }

            if (tag.type == NFCTagTypeMiFare) {
                val mifareTag = tag as? NFCMifareTagProtocol
                if (mifareTag != null) {
                    readMifareTag(mifareTag, session)
                } else {
                    session.invalidateSession(errorMessage = "Nieznany format Mifare")
                }
            } else {
                session.invalidateSession(errorMessage = "Nieobsługiwany typ karty")
            }
        }
    }

    private fun readMifareTag(tag: NFCMifareTagProtocol, session: NFCTagReaderSession) {
        // Read 16 bytes (4 blocks of 4 bytes) starting from block 4
        // NSRange location=4, length=4
        val range = NSMakeRange(4.toULong(), 4.toULong())

        tag.readMultipleBlocksWithRequestFlags(
            flags = 0u, // None
            blockRange = range
        ) { data, error ->
            if (error != null) {
                session.invalidateSession(errorMessage = "Błąd odczytu: ${error.localizedDescription}")
                return@readMultipleBlocksWithRequestFlags
            }

            if (data != null) {
                try {
                    val bytes = data.toByteArray()
                    val guid = bytes.toGuidString()

                    scope.launch {
                        nfcRepository.setScannedCardId(guid)
                    }

                    session.alertMessage = "Karta zeskanowana!"
                    session.invalidateSession()
                } catch (e: Exception) {
                    session.invalidateSession(errorMessage = "Błąd danych: ${e.message}")
                }
            } else {
                session.invalidateSession(errorMessage = "Puste dane")
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray {
        val length = this.length.toInt()
        val bytes = ByteArray(length)
        if (length > 0) {
            return this.bytes?.reinterpret<ByteVar>()?.readBytes(length) ?: ByteArray(0)
        }
        return bytes
    }
}
