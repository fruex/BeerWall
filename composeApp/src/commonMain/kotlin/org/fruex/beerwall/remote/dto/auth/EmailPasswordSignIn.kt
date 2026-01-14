package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class EmailPasswordSignInRequest(
    val email: String,
    val password: String
)

typealias EmailPasswordSignInResponse = AuthResponse
