package org.fruex.beerwall.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiEnvelope

typealias GoogleSignInResponse = AuthResponse

typealias GoogleSignInEnvelope = ApiEnvelope<GoogleSignInResponse>
