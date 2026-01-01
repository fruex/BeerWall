package com.igibeer.beerwall.data.remote.dto.history

data class HistoryItem(
    val id: String,
    val type: String,        // e.g., "purchase", "topup"
    val amount: Long,
    val currency: String,
    val createdAt: String,
    val description: String? = null
)

data class GetHistoryResponse(
    val items: List<HistoryItem>
)
