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
import org.fruex.beerwall.remote.dto.cards.CardSummary
import org.fruex.beerwall.remote.dto.history.Transaction
import org.fruex.beerwall.ui.models.CardItem
import org.fruex.beerwall.ui.models.LocationBalance
import org.fruex.beerwall.ui.models.TransactionGroup
import org.fruex.beerwall.ui.models.Transaction as TransactionModel

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

    suspend fun getTransactions(): Result<List<TransactionGroup>> = try {
        val response: ApiEnvelope<List<Transaction>> = client.get("$baseUrl/history").body()
        if (response.data != null) {
            Result.success(response.data.groupBy { it.date }.map { (date, transactions) ->
                TransactionGroup(
                    date = date,
                    transactions = transactions.map {
                        TransactionModel(
                            id = it.id.toString(),
                            beerName = it.description,
                            date = it.date,
                            time = "", // Not in API response
                            amount = it.amount,
                            cardNumber = it.operator
                        )
                    }
                )
            })
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getCards(): Result<List<CardItem>> = try {
        val response: ApiEnvelope<List<CardSummary>> = client.get("$baseUrl/cards").body()
        if (response.data != null) {
            Result.success(response.data.map {
                CardItem(
                    id = it.id,
                    name = it.type.name.lowercase().replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() },
                    isActive = it.isActive,
                    isPhysical = !it.isVirtual
                )
            })
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
