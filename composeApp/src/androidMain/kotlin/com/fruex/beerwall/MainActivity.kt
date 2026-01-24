package com.fruex.beerwall

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.fruex.beerwall.domain.repository.NfcRepository
import com.fruex.beerwall.nfc.NfcCardReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private val platform = getPlatform()
    private val nfcRepository: NfcRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        checkNfcStatus()

        lifecycleScope.launch {
            nfcRepository.isScanning.collect { isScanning ->
                if (lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
                    updateForegroundDispatch(isScanning)
                }
            }
        }

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        checkNfcStatus()
        updateForegroundDispatch(nfcRepository.isScanning.value)
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun checkNfcStatus() {
        val isEnabled = nfcAdapter?.isEnabled == true
        lifecycleScope.launch {
            nfcRepository.setNfcEnabled(isEnabled)
        }
    }

    private fun updateForegroundDispatch(isScanning: Boolean) {
        if (isScanning) {
            enableForegroundDispatch()
        } else {
            disableForegroundDispatch()
        }
    }

    private fun enableForegroundDispatch() {
        nfcAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                val intent = Intent(this, javaClass).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                val pendingIntent = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_MUTABLE
                )
                try {
                    adapter.enableForegroundDispatch(this, pendingIntent, null, null)
                } catch (e: Exception) {
                    platform.log("Error enabling NFC foreground dispatch: ${e.message}", this, LogSeverity.ERROR)
                }
            }
        }
    }

    private fun disableForegroundDispatch() {
        try {
            nfcAdapter?.disableForegroundDispatch(this)
        } catch (e: Exception) {
            // Ignore if not enabled or other errors
        }
    }

    private fun handleIntent(intent: Intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {

            val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }
            tag?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    val scannedId = NfcCardReader.readCardId(it)
                    if (scannedId != null) {
                        nfcRepository.setScannedCardId(scannedId)
                        platform.log("Card scanned: $scannedId", this@MainActivity, LogSeverity.INFO)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
