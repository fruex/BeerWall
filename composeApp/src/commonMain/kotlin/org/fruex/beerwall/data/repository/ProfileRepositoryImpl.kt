package org.fruex.beerwall.data.repository

import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val dataSource: BeerWallDataSource
) : ProfileRepository {
    
    override suspend fun getLoyaltyPoints(): Result<Int> {
        return dataSource.getProfile().map { it.loyaltyPoints }
    }
}
