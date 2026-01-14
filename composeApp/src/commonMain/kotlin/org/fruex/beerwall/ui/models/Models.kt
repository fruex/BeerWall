package org.fruex.beerwall.ui.models

import androidx.compose.runtime.Immutable

// Balance models
@Immutable
data class VenueBalance(
    val premisesId: Int,
    val premisesName: String,
    val balance: Double,
    val loyalityPoints: Int
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
    val email: String,
    val initials: String
)
