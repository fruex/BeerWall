package org.fruex.beerwall.data.remote.dto.auth

import kotlinx.serialization.Serializable
import org.fruex.beerwall.data.remote.common.ApiEnvelope

typealias GoogleSignInResponse = AuthResponse

typealias GoogleSignInEnvelope = ApiEnvelope<GoogleSignInResponse>
