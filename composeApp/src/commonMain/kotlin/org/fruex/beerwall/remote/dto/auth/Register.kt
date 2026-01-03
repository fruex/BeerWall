package org.fruex.beerwall.remote.dto.auth

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class GoogleRegisterRequest(
    val idToken: String
)

//data class RegisterResponse(
//    val userId: String,
//    val emailVerified: Boolean
//)
