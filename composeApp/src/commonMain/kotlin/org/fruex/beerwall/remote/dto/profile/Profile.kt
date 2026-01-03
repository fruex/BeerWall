package org.fruex.beerwall.remote.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val loyaltyPoints: Int
)
