package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class EmailPasswordSignInRequest(
    val email: String,
    val password: String
)

@Serializable
data class EmailPasswordSignInResponse(
    val token: String,
    val tokenExpires: Long,
    val refreshToken: String,
    val refreshTokenExpires: Long
)