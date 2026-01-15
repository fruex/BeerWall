package org.fruex.beerwall.domain.repository

interface SupportRepository {
    suspend fun sendMessage(message: String): Result<Unit>
}
