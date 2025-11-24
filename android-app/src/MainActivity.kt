/*
 * Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.igibeer.beerwall

import Screen
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import java.util.UUID

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private val cardId = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            Screen(cardId.value)
        }
    }

    override fun onResume() {
        super.onResume()
        enableForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun enableForegroundDispatch() {
        nfcAdapter?.let { adapter ->
            val intent = Intent(this, javaClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_MUTABLE
            )
            adapter.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    private fun disableForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun handleIntent(intent: Intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {

            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let {
                readCardData(it)
            }
        }
    }

    private fun readCardData(tag: Tag) {
        try {
            val mifareUltralight = MifareUltralight.get(tag)
            mifareUltralight?.let { mifare ->
                mifare.connect()

                // Read page 4 (reads 4 pages starting from page 4: pages 4, 5, 6, 7)
                val bytes = mifare.readPages(4)

                mifare.close()

                // Take first 16 bytes and convert to GUID
                val cardGuid = bytes.toGuidString()
                cardId.value = cardGuid

                Log.d("NFC", "Card GUID from page 4: ${cardId.value}")
                Log.d("NFC", "Raw bytes: ${bytes.joinToString(":") { "%02X".format(it) }}")
            }
        } catch (e: Exception) {
            Log.e("NFC", "Error reading card", e)
        }
    }

    private fun ByteArray.toGuidString(): String {
        return try {
            if (this.size < 16) throw IllegalArgumentException("Insufficient bytes for GUID")
            
            // Convert bytes to GUID format
            // Assuming the GUID is stored in the first 16 bytes
            val buffer = java.nio.ByteBuffer.wrap(this, 0, 16)
            val mostSigBits = buffer.long
            val leastSigBits = buffer.long
            val uuid = UUID(mostSigBits, leastSigBits)

            uuid.toString()
        } catch (e: Exception) {
            Log.e("NFC", "Error creating GUID", e)
            this.joinToString(":") { "%02X".format(it) }
        }
    }
}