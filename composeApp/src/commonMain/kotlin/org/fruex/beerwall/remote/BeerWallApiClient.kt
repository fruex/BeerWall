package org.fruex.beerwall.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.fruex.beerwall.getPlatform
import org.fruex.beerwall.remote.common.ApiEnvelope
import org.fruex.beerwall.remote.common.ApiResponse
import org.fruex.beerwall.remote.dto.balance.BalanceItem
import org.fruex.beerwall.remote.dto.balance.GetBalanceResponse
import org.fruex.beerwall.remote.dto.balance.TopUpRequest
import org.fruex.beerwall.remote.dto.cards.CardActivationData
import org.fruex.beerwall.remote.dto.cards.CardActivationRequest
import org.fruex.beerwall.remote.dto.cards.CardActivationResponse
import org.fruex.beerwall.remote.dto.cards.CardItemDto
import org.fruex.beerwall.remote.dto.cards.GetCardsResponse
import org.fruex.beerwall.remote.dto.history.GetHistoryResponse
import org.fruex.beerwall.remote.dto.history.TransactionDto
import org.fruex.beerwall.remote.dto.profile.GetProfileResponse
import org.fruex.beerwall.remote.dto.profile.ProfileDto
import org.fruex.beerwall.ui.models.Transaction
import org.fruex.beerwall.ui.models.UserCard
import org.fruex.beerwall.ui.models.VenueBalance

import org.fruex.beerwall.remote.dto.balance.TopUpResponse
import org.fruex.beerwall.remote.dto.balance.TopUpResponseData

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

    private suspend inline fun <reified T : ApiResponse<D>, D, R> safeCall(
        crossinline block: suspend HttpClient.() -> T,
        noinline mapper: (D) -> R
    ): Result<R> = try {
        val response = client.block()
        if (response.data != null) {
            Result.success(mapper(response.data!!))
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getBalance(): Result<List<VenueBalance>> = safeCall<GetBalanceResponse, List<BalanceItem>, List<VenueBalance>>(
        block = { get("$baseUrl/balance").body() },
        mapper = { items ->
            items.map {
                VenueBalance(
                    venueName = it.venueName,
                    balance = it.balance
                )
            }
        }
    )

    suspend fun topUp(amount: Double, venueName: String): Result<Double> = safeCall<TopUpResponse, TopUpResponseData, Double>(
        block = {
            post("$baseUrl/balance") {
                contentType(ContentType.Application.Json)
                setBody(TopUpRequest(amount, "Blik"))
            }.body()
        },
        mapper = { it.newBalance }
    )

    suspend fun getCards(): Result<List<UserCard>> = safeCall<GetCardsResponse, List<CardItemDto>, List<UserCard>>(
        block = { get("$baseUrl/cards").body() },
        mapper = { items ->
            items.map {
                UserCard(
                    id = it.id,
                    name = it.name,
                    isActive = it.isActive,
                    isPhysical = it.isPhysical
                )
            }
        }
    )

    suspend fun toggleCardStatus(cardId: String, activate: Boolean): Result<Boolean> = safeCall<CardActivationResponse, CardActivationData, Boolean>(
        block = {
            post("$baseUrl/card-activation") {
                contentType(ContentType.Application.Json)
                setBody(CardActivationRequest(cardId, activate))
            }.body()
        },
        mapper = { it.isActive }
    )

    suspend fun getHistory(): Result<List<Transaction>> = safeCall<GetHistoryResponse, List<TransactionDto>, List<Transaction>>(
        block = { get("$baseUrl/history").body() },
        mapper = { items ->
            items.map {
                Transaction(
                    id = it.id,
                    beerName = it.beerName,
                    date = it.date,
                    time = it.time,
                    amount = it.amount,
                    cardNumber = it.cardNumber
                )
            }
        }
    )

    suspend fun getProfile(): Result<Int> = safeCall<GetProfileResponse, ProfileDto, Int>(
        block = { get("$baseUrl/profile").body() },
        mapper = { it.loyaltyPoints }
    )
}
