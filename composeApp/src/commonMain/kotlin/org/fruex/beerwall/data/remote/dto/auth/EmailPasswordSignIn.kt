package org.fruex.beerwall.data.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.data.remote.common.ApiEnvelope

@Serializable
data class EmailPasswordSignInRequest(
    val email: String,
    val password: String
)

typealias EmailPasswordSignInResponse = AuthResponse
typealias EmailPasswordSignInEnvelope = ApiEnvelope<EmailPasswordSignInResponse>
