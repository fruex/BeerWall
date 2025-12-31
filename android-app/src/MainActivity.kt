package com.igibeer.beerwall

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.lang.String.format

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var cardId by mutableStateOf<String?>(null)
    private var isNfcScanning by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            App(
                onRegister = { email, password ->
                    Log.d("MainActivity", "Register: $email")
                },
                onLogin = { email, password ->
                    Log.d("MainActivity", "Login: $email")
                },
                onGoogleSignIn = {
                    Log.d("MainActivity", "Google Sign In")
                },
                onLogout = {
                    Log.d("MainActivity", "Logout")
                },
                onAddFunds = { location, amount ->
                    Log.d("MainActivity", "Add $amount PLN to $location")
                },
                onToggleCardStatus = { cardId ->
                    Log.d("MainActivity", "Toggle card status: $cardId")
                },
                onDeleteCard = { cardId ->
                    Log.d("MainActivity", "Delete card: $cardId")
                },
                onSaveCard = { name, cardId ->
                    Log.d("MainActivity", "Save card: $name with ID: $cardId")
                    this.cardId = null
                    isNfcScanning = false
                },
                onStartNfcScanning = {
                    Log.d("MainActivity", "Start NFC scanning")
                    isNfcScanning = true
                },
                scannedCardId = cardId,
                isNfcScanning = isNfcScanning
            )
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
            //val intent = Intent(this, java.lang.Class.forName("com.igibeer.beerwall.MainActivity")).apply {
            val intent = Intent(this, MainActivity::class.java).apply {
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
            // NTAG 213
            mifareUltralight?.let { mifare ->
                mifare.connect()

                // Read page 4 (reads 4 pages starting from page 4: pages 4, 5, 6, 7)
                val bytes = mifare.readPages(4)

                mifare.close()

                // Take first 16 bytes and convert to GUID
                val cardGuid = bytes.toGuidString()
                cardId = cardGuid
                isNfcScanning = false

                Log.d("NFC", "Card GUID from page 4: $cardId")
                Log.d("NFC", "Raw bytes: ${bytes.joinToString(":") { format("%02X", it) }}")
            }
        } catch (e: Exception) {
            Log.e("NFC", "Error reading card", e)
        }
    }

    private fun ByteArray.toGuidString(): String {
        return try {
            if (this.size < 16) throw IllegalArgumentException("Insufficient bytes for GUID")
            
            // Convert bytes to C# GUID format (mixed-endian)
            // C# Guid constructor uses: little-endian for the first 3 groups, big-endian for last 2
            // Format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
            
            // First 4 bytes (little-endian) - Data1
            val part1 = format("%02x%02x%02x%02x", this[3], this[2], this[1], this[0])
            
            // Next 2 bytes (little-endian) - Data2
            val part2 = format("%02x%02x", this[5], this[4])
            
            // Next 2 bytes (little-endian) - Data3
            val part3 = format("%02x%02x", this[7], this[6])
            
            // Next 2 bytes (big-endian) - Data4[0-1]
            val part4 = format("%02x%02x", this[8], this[9])
            
            // Last 6 bytes (big-endian) - Data4[2-7]
            val part5 = format("%02x%02x%02x%02x%02x%02x", this[10], this[11], this[12], this[13], this[14], this[15])
            
            "$part1-$part2-$part3-$part4-$part5"
        } catch (e: Exception) {
            Log.e("NFC", "Error creating GUID", e)
            this.joinToString(":") { format("%02X", it) }
        }
    }
}