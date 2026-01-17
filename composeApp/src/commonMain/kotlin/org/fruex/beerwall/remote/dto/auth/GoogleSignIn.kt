package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

/**
 * Alias dla odpowiedzi logowania przez Google (tożsamy z AuthResponse).
 */
typealias GoogleSignInResponse = AuthResponse

/**
 * Alias dla koperty odpowiedzi logowania przez Google.
 */
typealias GoogleSignInEnvelope = ApiEnvelope<GoogleSignInResponse>
