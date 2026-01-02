package com.igibeer.beerwall.data

import kotlinx.serialization.Serializable

@Serializable
data class LocationBalance(
    val locationName: String,
    val balance: Double
)

@Serializable
data class CardItem(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val isPhysical: Boolean
)

@Serializable
data class Transaction(
    val id: String,
    val beerName: String,
    val date: String,
    val time: String,
    val amount: Double,
    val cardNumber: String
)

@Serializable
data class TransactionGroup(
    val date: String,
    val transactions: List<Transaction>
)

@Serializable
data class UserProfile(
    val name: String,
    val email: String,
    val initials: String,
    val activeCards: Int,
    val loyaltyPoints: Int
)
