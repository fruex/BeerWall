package org.fruex.beerwall.ui.screens.balance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.fruex.beerwall.ui.components.BeerWallButton
import org.fruex.beerwall.ui.components.BeerWallTextField
import org.fruex.beerwall.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsScreen(
    availableVenues: List<String>,
    selectedVenue: String?,
    onVenueSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    onAddFunds: (venueName: String, amount: Double, blikCode: String) -> Unit,
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var blikCode by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val currentVenue = selectedVenue ?: availableVenues.firstOrNull() ?: ""
    val isBlikCodeValid = blikCode.length == 6 && blikCode.all { it.isDigit() }

    if (isProcessing) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(3000)
            amount.toDoubleOrNull()?.let { amountValue ->
                onAddFunds(currentVenue, amountValue, blikCode)
            }
            isProcessing = false
        }

        AlertDialog(
            onDismissRequest = { },
            confirmButton = { },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Łączenie z bankiem...",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Text(
                    text = "Proszę zaakceptować płatność w aplikacji bankowej.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            containerColor = CardBackground,
            shape = RoundedCornerShape(16.dp)
        )
    }

    BeerWallTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Wstecz",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = "Dodaj środki",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Doładuj saldo Beer Wall",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Miejsce",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Venue Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .background(
                                color = CardBackground,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { expanded = true }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = currentVenue,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        availableVenues.forEach { venue ->
                            DropdownMenuItem(
                                text = { Text(venue) },
                                onClick = {
                                    onVenueSelected(venue)
                                    expanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = TextPrimary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Własna kwota",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))

                BeerWallTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = "0.00",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    trailingIcon = {
                        Text(
                            text = "PLN",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Wybierz kwotę",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Quick amount buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAmountButton(
                        amount = "10",
                        modifier = Modifier.weight(1f),
                        onClick = { amount = "10.00" }
                    )
                    QuickAmountButton(
                        amount = "20",
                        modifier = Modifier.weight(1f),
                        onClick = { amount = "20.00" }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAmountButton(
                        amount = "50",
                        modifier = Modifier.weight(1f),
                        onClick = { amount = "50.00" }
                    )
                    QuickAmountButton(
                        amount = "100",
                        modifier = Modifier.weight(1f),
                        onClick = { amount = "100.00" }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BLIK info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GoldPrimary
                        )
                        Column {
                            Text(
                                text = "BLIK",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Szybka płatność mobilna",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Kod BLIK",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))

                BeerWallTextField(
                    value = blikCode,
                    onValueChange = { newValue ->
                        if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                            blikCode = newValue
                        }
                    },
                    placeholder = "",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = blikCode.isNotEmpty() && !isBlikCodeValid
                )

                if (blikCode.isNotEmpty() && !isBlikCodeValid) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kod BLIK musi składać się z 6 cyfr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                BeerWallButton(
                    text = "Dodaj ${amount.ifBlank { "0.00" }} PLN do salda",
                    onClick = {
                        amount.toDoubleOrNull()?.let { amountValue ->
                            if (amountValue > 0 && isBlikCodeValid) {
                                isProcessing = true
                            }
                        }
                    },
                    enabled = amount.toDoubleOrNull()?.let { it > 0 } == true && isBlikCodeValid && !isProcessing
                )
            }
        }
    }
}

@Composable
fun QuickAmountButton(
    amount: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(2f)
            .background(
                color = CardBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$amount PLN",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
