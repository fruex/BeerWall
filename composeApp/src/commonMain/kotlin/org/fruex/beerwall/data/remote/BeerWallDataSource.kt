package org.fruex.beerwall.data.remote

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
import org.fruex.beerwall.remote.common.ApiResponse
import org.fruex.beerwall.remote.dto.balance.*
import org.fruex.beerwall.remote.dto.cards.*
import org.fruex.beerwall.remote.dto.history.*
import org.fruex.beerwall.remote.dto.profile.*

class BeerWallDataSource {
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

    private suspend inline fun <reified T : ApiResponse<D>, D> safeCall(
        crossinline block: suspend HttpClient.() -> T
    ): Result<D> = try {
        val response = client.block()
        if (response.data != null) {
            Result.success(response.data!!)
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getBalance(): Result<List<BalanceItem>> = 
        safeCall<GetBalanceResponse, List<BalanceItem>> {
            get("$baseUrl/balance").body()
        }

    suspend fun topUp(amount: Double, venueName: String): Result<TopUpResponseData> = 
        safeCall<TopUpResponse, TopUpResponseData> {
            post("$baseUrl/balance") {
                contentType(ContentType.Application.Json)
                setBody(TopUpRequest(amount, "Blik"))
            }.body()
        }

    suspend fun getCards(): Result<List<CardItemDto>> = 
        safeCall<GetCardsResponse, List<CardItemDto>> {
            get("$baseUrl/cards").body()
        }

    suspend fun toggleCardStatus(cardId: String, activate: Boolean): Result<CardActivationData> = 
        safeCall<CardActivationResponse, CardActivationData> {
            post("$baseUrl/card-activation") {
                contentType(ContentType.Application.Json)
                setBody(CardActivationRequest(cardId, activate))
            }.body()
        }

    suspend fun getHistory(): Result<List<TransactionDto>> = 
        safeCall<GetHistoryResponse, List<TransactionDto>> {
            get("$baseUrl/history").body()
        }

    suspend fun getProfile(): Result<ProfileDto> = 
        safeCall<GetProfileResponse, ProfileDto> {
            get("$baseUrl/profile").body()
        }
}
