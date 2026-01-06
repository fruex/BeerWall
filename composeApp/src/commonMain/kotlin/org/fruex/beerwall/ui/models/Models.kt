package org.fruex.beerwall.ui.models

import androidx.compose.runtime.Immutable

// Balance models
@Immutable
data class VenueBalance(
    val venueName: String,
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
    val id: String,
    val beerName: String,
    val date: String,
    val time: String,
    val amount: Double,
    val cardNumber: String
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
    val initials: String,
    val activeCards: Int,
    val loyaltyPoints: Int,
    val photoUrl: String? = null
)
