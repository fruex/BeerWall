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
import org.fruex.beerwall.ui.models.LocationBalance

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

    suspend fun getBalance(): Result<List<LocationBalance>> = try {
        val response: ApiEnvelope<List<BalanceItem>> = client.get("$baseUrl/balance").body()
        if (response.data != null) {
            Result.success(response.data.map {
                LocationBalance(
                    locationName = it.venueName,
                    balance = it.amount
                )
            })
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
