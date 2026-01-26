package com.fruex.beerwall.domain.auth

import com.fruex.beerwall.domain.model.GoogleUser

/**
 * Interfejs dostawcy autoryzacji Google.
 */
interface GoogleAuthProvider {
    suspend fun signIn(): GoogleUser?
    suspend fun signOut()
    suspend fun getSignedInUser(): GoogleUser?
}
