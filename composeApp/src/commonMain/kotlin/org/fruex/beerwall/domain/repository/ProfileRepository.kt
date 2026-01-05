package org.fruex.beerwall.domain.repository

interface ProfileRepository {
    suspend fun getLoyaltyPoints(): Result<Int>
}
