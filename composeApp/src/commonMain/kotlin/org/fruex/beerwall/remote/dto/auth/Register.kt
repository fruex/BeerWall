package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String
)
