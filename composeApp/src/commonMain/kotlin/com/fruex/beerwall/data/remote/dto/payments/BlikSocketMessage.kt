package com.fruex.beerwall.data.remote.dto.payments

import kotlinx.serialization.Serializable

/**
 * Statusy transakcji BLIK.
 */
enum class BlikStatus {
    PENDING,
    SUCCESS,
    FAILURE,
    EXPIRED,
    CANCELLED
}

/**
 * Wiadomość otrzymywana przez WebSocket dotycząca statusu BLIK.
 */
@Serializable
data class BlikSocketMessage(
    val transactionId: String,
    val status: BlikStatus,
    val message: String? = null
)
