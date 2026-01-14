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
    val id: String,
    val beverageName: String,
    val timestamp: String,
    val venueName: String,
    val amount: Double,
    val volumeMilliliters: Int
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
