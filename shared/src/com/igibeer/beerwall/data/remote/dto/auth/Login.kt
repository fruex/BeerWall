package com.igibeer.beerwall.data.remote.dto.auth

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String,
// Opcjonalnie: refreshToken (bardzo zalecane w przyszłości)
    val expiresInSec: Long
)

data class GoogleLoginRequest(
    val idToken: String
)
