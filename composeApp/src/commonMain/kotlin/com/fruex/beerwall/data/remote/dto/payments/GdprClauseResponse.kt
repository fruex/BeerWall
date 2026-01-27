package com.fruex.beerwall.data.remote.dto.payments

import kotlinx.serialization.Serializable
import com.fruex.beerwall.data.remote.common.ApiEnvelope

@Serializable
data class GdprClauseResponse(
    val title: String,
    val content: String,
    val locale: String
)

typealias GetGdprClauseEnvelope = ApiEnvelope<GdprClauseResponse>
