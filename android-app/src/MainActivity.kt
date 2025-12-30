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
import com.igibeer.beerwall.ui.models.*
import com.igibeer.beerwall.ui.navigation.BeerWallNavHost
import com.igibeer.beerwall.ui.navigation.NavigationDestination
import com.igibeer.beerwall.ui.theme.BeerWallTheme

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var cardId by mutableStateOf<String?>(null)
    private var isNfcScanning by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            BeerWallTheme {
                // Example data - replace with real data from your backend
            val balances = listOf(
                LocationBalance("Pub Lewe", 125.50),
                LocationBalance("Browariat", 89.00),
                LocationBalance("Biała Małpa", 45.25)
            )

            val cards = listOf(
                CardItem(
                    id = "550e8400-e29b-41d4-a716-446655440000",
                    name = "Karta Wirtualna",
                    isActive = true,
                    isPhysical = false
                ),
                CardItem(
                    id = "750e8460-e29b-41d4-a716-446655440001",
                    name = "Karta fizyczna",
                    isActive = true,
                    isPhysical = true
                )
            )

            val transactionGroups = listOf(
                TransactionGroup(
                    date = "24 LISTOPADA 2025",
                    transactions = listOf(
                        Transaction(
                            id = "1",
                            beerName = "Pilsner Urquell",
                            date = "24 lis",
                            time = "19:30",
                            amount = -12.50,
                            cardNumber = "45:32"
                        ),
                        Transaction(
                            id = "2",
                            beerName = "Wino Chianti Classico",
                            date = "24 lis",
                            time = "20:15",
                            amount = -28.00,
                            cardNumber = "45:32"
                        ),
                        Transaction(
                            id = "3",
                            beerName = "Guinness Draught",
                            date = "24 lis",
                            time = "21:00",
                            amount = -15.00,
                            cardNumber = "89:21"
                        )
                    )
                ),
                TransactionGroup(
                    date = "23 LISTOPADA 2025",
                    transactions = listOf(
                        Transaction(
                            id = "4",
                            beerName = "Corona Extra",
                            date = "23 lis",
                            time = "18:45",
                            amount = -11.00,
                            cardNumber = "89:21"
                        ),
                        Transaction(
                            id = "5",
                            beerName = "Prosecco",
                            date = "23 lis",
                            time = "19:30",
                            amount = -22.00,
                            cardNumber = "45:32"
                        ),
                        Transaction(
                            id = "6",
                            beerName = "Heineken",
                            date = "23 lis",
                            time = "20:15",
                            amount = -10.50,
                            cardNumber = "45:32"
                        ),
                        Transaction(
                            id = "7",
                            beerName = "Stella Artois",
                            date = "23 lis",
                            time = "21:30",
                            amount = -13.50,
                            cardNumber = "89:21"
                        )
                    )
                ),
                TransactionGroup(
                    date = "22 LISTOPADA 2025",
                    transactions = listOf(
                        Transaction(
                            id = "8",
                            beerName = "Tyskie Gronie",
                            date = "22 lis",
                            time = "17:00",
                            amount = -9.50,
                            cardNumber = "45:32"
                        ),
                        Transaction(
                            id = "9",
                            beerName = "Wino Malbec",
                            date = "22 lis",
                            time = "18:30",
                            amount = -32.00,
                            cardNumber = "89:21"
                        ),
                        Transaction(
                            id = "10",
                            beerName = "IPA Craft Beer",
                            date = "22 lis",
                            time = "19:45",
                            amount = -16.00,
                            cardNumber = "45:32"
                        )
                    )
                ),
                TransactionGroup(
                    date = "21 LISTOPADA 2025",
                    transactions = listOf(
                        Transaction(
                            id = "11",
                            beerName = "Żywiec Porter",
                            date = "21 lis",
                            time = "20:00",
                            amount = -14.00,
                            cardNumber = "89:21"
                        ),
                        Transaction(
                            id = "12",
                            beerName = "Wino Sauvignon Blanc",
                            date = "21 lis",
                            time = "20:45",
                            amount = -25.00,
                            cardNumber = "45:32"
                        ),
                        Transaction(
                            id = "13",
                            beerName = "Peroni Nastro Azzurro",
                            date = "21 lis",
                            time = "21:30",
                            amount = -12.00,
                            cardNumber = "89:21"
                        ),
                        Transaction(
                            id = "14",
                            beerName = "Desperados",
                            date = "21 lis",
                            time = "22:15",
                            amount = -11.50,
                            cardNumber = "45:32"
                        )
                    )
                ),
                TransactionGroup(
                    date = "20 LISTOPADA 2025",
                    transactions = listOf(
                        Transaction(
                            id = "15",
                            beerName = "Wino Cabernet Sauvignon",
                            date = "20 lis",
                            time = "19:00",
                            amount = -30.00,
                            cardNumber = "45:32"
                        ),
                        Transaction(
                            id = "16",
                            beerName = "Carlsberg",
                            date = "20 lis",
                            time = "20:30",
                            amount = -10.00,
                            cardNumber = "89:21"
                        ),
                        Transaction(
                            id = "17",
                            beerName = "Hoegaarden",
                            date = "20 lis",
                            time = "21:15",
                            amount = -13.00,
                            cardNumber = "45:32"
                        )
                    )
                )
            )

            val userProfile = UserProfile(
                name = "Waldek Waldemord",
                email = "w.w@email.com",
                initials = "WW",
                activeCards = 2,
                loyaltyPoints = 2
            )

            BeerWallNavHost(
                startDestination = NavigationDestination.Main.route,
                balances = balances,
                cards = cards,
                transactionGroups = transactionGroups,
                userProfile = userProfile,
                onRegister = { email, password ->
                    Log.d("MainActivity", "Register: $email")
                    // TODO: Implement registration logic
                },
                onLogin = { email, password ->
                    Log.d("MainActivity", "Login: $email")
                    // TODO: Implement login logic
                },
                onGoogleSignIn = {
                    Log.d("MainActivity", "Google Sign In")
                    // TODO: Implement Google Sign In
                },
                onLogout = {
                    Log.d("MainActivity", "Logout")
                    // TODO: Implement logout logic
                },
                onAddFunds = { location, amount ->
                    Log.d("MainActivity", "Add $amount PLN to $location")
                    // TODO: Implement add funds logic
                },
                onToggleCardStatus = { cardId ->
                    Log.d("MainActivity", "Toggle card status: $cardId")
                    // TODO: Implement toggle card status
                },
                onDeleteCard = { cardId ->
                    Log.d("MainActivity", "Delete card: $cardId")
                    // TODO: Implement delete card
                },
                onSaveCard = { name, cardId ->
                    Log.d("MainActivity", "Save card: $name with ID: $cardId")
                    // TODO: Implement save card logic
                    this.cardId = null
                    isNfcScanning = false
                },
                onStartNfcScanning = {
                    Log.d("MainActivity", "Start NFC scanning")
                    isNfcScanning = true
                    // NFC scanning is handled automatically by the activity
                },
                scannedCardId = cardId,
                isNfcScanning = isNfcScanning
            )
            }
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
                Log.d("NFC", "Raw bytes: ${bytes.joinToString(":") { "%02X".format(it) }}")
            }
        } catch (e: Exception) {
            Log.e("NFC", "Error reading card", e)
        }
    }

    private fun ByteArray.toGuidString(): String {
        return try {
            if (this.size < 16) throw IllegalArgumentException("Insufficient bytes for GUID")
            
            // Convert bytes to C# GUID format (mixed-endian)
            // C# Guid constructor uses: little-endian for first 3 groups, big-endian for last 2
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
            Log.e("NFC", "Error creating GUID", e)
            this.joinToString(":") { "%02X".format(it) }
        }
    }
}