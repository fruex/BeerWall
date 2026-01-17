package org.fruex.beerwall.data.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.data.remote.common.ApiEnvelope

/**
 * DTO żądania odświeżenia tokenu dostępu.
 *
 * @property refreshToken Token odświeżania (refresh token).
 */
@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * Alias dla odpowiedzi odświeżenia tokenu (tożsamy z AuthResponse).
 */
typealias RefreshTokenResponse = AuthResponse

/**
 * Alias dla koperty odpowiedzi odświeżenia tokenu.
 */
typealias RefreshTokenEnvelope = ApiEnvelope<RefreshTokenResponse>
