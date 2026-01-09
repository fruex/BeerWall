package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.auth.GoogleUser

interface AuthRepository {
    suspend fun googleSignIn(idToken: String): Result<GoogleUser>
}
