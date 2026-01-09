package org.fruex.beerwall.data.repository

import org.fruex.beerwall.auth.GoogleUser
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val dataSource: BeerWallDataSource
) : AuthRepository {
    override suspend fun googleSignIn(idToken: String): Result<GoogleUser> {
        return dataSource.googleSignIn(idToken).map { response ->
            GoogleUser(
                idToken = response.token, // Tutaj przechowujemy token sesji z backendu
                displayName = response.name,
                email = response.email,
                photoUrl = response.pictureUrl
            )
        }
    }
}
