package com.fruex.beerwall.ui.models

import androidx.compose.runtime.Immutable

// Balance models
@Immutable
data class VenueBalance(
    val premisesId: Int,
    val premisesName: String,
    val balance: Double,
    val loyaltyPoints: Int
)

// Card models
@Immutable
data class UserCard(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val isPhysical: Boolean
)

// History models
@Immutable
data class Transaction(
    val transactionId: Int,
    val commodityName: String,
    val startDateTime: String,
    val grossPrice: Double,
    val capacity: Int
)

@Immutable
data class DailyTransactions(
    val date: String,
    val transactions: List<Transaction>
)

// Profile models
@Immutable
data class UserProfile(
    val name: String,
    val initials: String = name.split(" ")
        .mapNotNull { it.firstOrNull() }
        .take(2)
        .joinToString("")
        .uppercase()
)

// Payment models
@Immutable
data class PaymentMethod(
    val paymentMethodId: Int,
    val name: String,
    val description: String,
    val image: String,
    val status: String
)

@Immutable
data class PaymentOperator(
    val type: String,
    val paymentMethods: List<PaymentMethod>
)
