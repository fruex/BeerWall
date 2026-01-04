package org.fruex.beerwall.ui.models

// Balance models
data class VenueBalance(
    val venueName: String,
    val balance: Double
)

// Card models
data class UserCard(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val isPhysical: Boolean
)

// History models
data class Transaction(
    val id: String,
    val beerName: String,
    val date: String,
    val time: String,
    val amount: Double,
    val cardNumber: String
)

data class DailyTransactions(
    val date: String,
    val transactions: List<Transaction>
)

// Profile models
data class UserProfile(
    val name: String,
    val email: String,
    val initials: String,
    val activeCards: Int,
    val loyaltyPoints: Int,
    val photoUrl: String? = null
)
