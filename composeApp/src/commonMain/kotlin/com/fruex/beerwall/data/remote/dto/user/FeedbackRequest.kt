package com.fruex.beerwall.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackRequest(
    val message: String
)
