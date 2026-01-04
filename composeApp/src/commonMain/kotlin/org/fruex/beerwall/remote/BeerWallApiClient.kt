package org.fruex.beerwall.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.fruex.beerwall.getPlatform
import org.fruex.beerwall.remote.common.ApiEnvelope
import org.fruex.beerwall.remote.dto.balance.BalanceItem
import org.fruex.beerwall.remote.dto.cards.CardItemDto
import org.fruex.beerwall.remote.dto.history.TransactionDto
import org.fruex.beerwall.remote.dto.profile.ProfileDto
import org.fruex.beerwall.ui.models.UserCard
import org.fruex.beerwall.ui.models.VenueBalance
import org.fruex.beerwall.ui.models.Transaction

class BeerWallApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private val baseUrl = getPlatform().apiBaseUrl

    suspend fun getBalance(): Result<List<VenueBalance>> = try {
        val response: ApiEnvelope<List<BalanceItem>> = client.get("$baseUrl/balance").body()
        if (response.data != null) {
            Result.success(response.data.map {
                VenueBalance(
                    venueName = it.locationName,
                    balance = it.balance
                )
            })
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getCards(): Result<List<UserCard>> = try {
        val response: ApiEnvelope<List<CardItemDto>> = client.get("$baseUrl/cards").body()
        if (response.data != null) {
            Result.success(response.data.map {
                UserCard(
                    id = it.id,
                    name = it.name,
                    isActive = it.isActive,
                    isPhysical = it.isPhysical
                )
            })
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getHistory(): Result<List<Transaction>> = try {
        val response: ApiEnvelope<List<TransactionDto>> = client.get("$baseUrl/history").body()
        if (response.data != null) {
            Result.success(response.data.map {
                Transaction(
                    id = it.id,
                    beerName = it.beerName,
                    date = it.date,
                    time = it.time,
                    amount = it.amount,
                    cardNumber = it.cardNumber
                )
            })
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getProfile(): Result<Int> = try {
        val response: ApiEnvelope<ProfileDto> = client.get("$baseUrl/profile").body()
        if (response.data != null) {
            Result.success(response.data.loyaltyPoints)
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
