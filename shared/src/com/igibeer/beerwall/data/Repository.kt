package com.igibeer.beerwall.data

class Repository {

    private val client = ApiClient.client

    suspend fun getBalances(): List<LocationBalance> {
        // TODO: Implement actual API call
        return emptyList()
    }

    suspend fun getCards(): List<CardItem> {
        // TODO: Implement actual API call
        return emptyList()
    }

    suspend fun getTransactionGroups(): List<TransactionGroup> {
        // TODO: Implement actual API call
        return emptyList()
    }

    suspend fun getUserProfile(): UserProfile? {
        // TODO: Implement actual API call
        return null
    }
}
