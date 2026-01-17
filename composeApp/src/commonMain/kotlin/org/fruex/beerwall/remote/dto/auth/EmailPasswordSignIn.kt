package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

/**
 * DTO żądania logowania za pomocą adresu email i hasła.
 *
 * @property email Adres email użytkownika.
 * @property password Hasło użytkownika.
 */
@Serializable
data class EmailPasswordSignInRequest(
    val email: String,
    val password: String
)

/**
 * Alias dla odpowiedzi logowania email/hasło (tożsamy z AuthResponse).
 */
typealias EmailPasswordSignInResponse = AuthResponse

/**
 * Alias dla koperty odpowiedzi logowania email/hasło.
 */
typealias EmailPasswordSignInEnvelope = ApiEnvelope<EmailPasswordSignInResponse>
