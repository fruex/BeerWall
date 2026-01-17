package org.fruex.beerwall.ui.screens.balance

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.fruex.beerwall.data.remote.dto.operators.PaymentMethod
import org.fruex.beerwall.ui.components.BeerWallButton
import org.fruex.beerwall.ui.components.BeerWallTextField
import org.fruex.beerwall.ui.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsScreen(
    availablePaymentMethods: List<PaymentMethod>,
    onBackClick: () -> Unit,
    onAddFunds: (paymentMethodId: Int, balance: Double) -> Unit,
    premisesName: String? = null,
) {
    var selectedAmount by rememberSaveable { mutableStateOf("") }
    var customAmount by rememberSaveable { mutableStateOf("") }
    var blikCode by rememberSaveable { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember {
        mutableStateOf(availablePaymentMethods.firstOrNull())
    }

    val predefinedAmounts = listOf("10", "20", "50", "100", "200", "Inna")
    val finalAmount = if (selectedAmount == "Inna") customAmount else selectedAmount
    val isBlikCodeValid = blikCode.length == 6 && blikCode.all { it.isDigit() }

    if (isProcessing) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(3000)
            finalAmount.toDoubleOrNull()?.let { balanceValue ->
                selectedPaymentMethod?.let { method ->
                    onAddFunds(method.paymentMethodId, balanceValue)
                }
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
                    text = if (premisesName != null) "Doładuj saldo w $premisesName" else "Doładuj saldo Beer Wall",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Payment Method Selection
                Text(
                    text = "Metoda płatności",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))

                availablePaymentMethods.forEach { method ->
                    PaymentMethodCard(
                        paymentMethod = method,
                        isSelected = selectedPaymentMethod?.paymentMethodId == method.paymentMethodId,
                        onClick = { selectedPaymentMethod = method }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Amount Selection
                Text(
                    text = "Wybierz kwotę",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    predefinedAmounts.take(3).forEach { amount ->
                        AmountChip(
                            amount = amount,
                            isSelected = selectedAmount == amount,
                            onClick = {
                                selectedAmount = amount
                                if (amount != "Inna") customAmount = ""
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    predefinedAmounts.drop(3).forEach { amount ->
                        AmountChip(
                            amount = amount,
                            isSelected = selectedAmount == amount,
                            onClick = {
                                selectedAmount = amount
                                if (amount != "Inna") customAmount = ""
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Spacer for layout balance
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Custom Amount Input
                if (selectedAmount == "Inna") {
                    Spacer(modifier = Modifier.height(16.dp))
                    BeerWallTextField(
                        value = customAmount,
                        onValueChange = { customAmount = it },
                        placeholder = "Wprowadź kwotę",
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
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BLIK Code Input
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
                    placeholder = "000 000",
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
                    text = "Dodaj ${finalAmount.ifBlank { "0.00" }} PLN",
                    onClick = {
                        finalAmount.toDoubleOrNull()?.let { balanceValue ->
                            if (balanceValue > 0 && isBlikCodeValid && selectedPaymentMethod != null) {
                                isProcessing = true
                            }
                        }
                    },
                    enabled = finalAmount.toDoubleOrNull()?.let { it > 0 } == true &&
                             isBlikCodeValid &&
                             selectedPaymentMethod != null &&
                             !isProcessing
                )
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GoldPrimary.copy(alpha = 0.1f) else CardBackground
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                AsyncImage(
                    model = paymentMethod.image,
                    contentDescription = paymentMethod.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentScale = ContentScale.Fit
                )
                Column {
                    Text(
                        text = paymentMethod.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = paymentMethod.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Wybrano",
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AmountChip(
    amount: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) GoldPrimary else CardBackground
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) GoldPrimary else androidx.compose.ui.graphics.Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (amount == "Inna") amount else "$amount PLN",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) DarkBackground else TextPrimary
        )
    }
}

@Preview
@Composable
fun AddFundsScreenPreview() {
    BeerWallTheme {
        AddFundsScreen(
            availablePaymentMethods = listOf(
                PaymentMethod(
                    paymentMethodId = 1,
                    name = "BLIK",
                    description = "Szybka płatność kodem",
                    image = "https://example.com/blik.png",
                    status = "ACTIVE"
                )
            ),
            onBackClick = {},
            onAddFunds = { _, _ -> }
        )
    }
}
