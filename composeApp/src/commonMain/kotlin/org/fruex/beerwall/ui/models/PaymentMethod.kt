package org.fruex.beerwall.ui.models

/**
 * UI model representing a payment method.
 * Decoupled from API DTOs.
 */
data class PaymentMethod(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val status: String
)
