package org.fruex.beerwall.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.fruex.beerwall.getPlatform
import org.fruex.beerwall.remote.common.ApiResponse
import org.fruex.beerwall.remote.dto.auth.GoogleSignInRequest
import org.fruex.beerwall.remote.dto.auth.GoogleSignInResponse
import org.fruex.beerwall.remote.dto.auth.GoogleSignInResponseData
import org.fruex.beerwall.remote.dto.balance.*
import org.fruex.beerwall.remote.dto.cards.*
import org.fruex.beerwall.remote.dto.history.GetHistoryResponse
import org.fruex.beerwall.remote.dto.history.TransactionDto
import org.fruex.beerwall.remote.dto.operators.GetPaymentOperatorsResponse
import org.fruex.beerwall.remote.dto.operators.PaymentOperator
import org.fruex.beerwall.remote.dto.profile.GetProfileResponse
import org.fruex.beerwall.remote.dto.profile.ProfileDto

/**
 * Data Source do komunikacji z API BeerWall
 *
 * Odpowiedzialny za:
 * - Wykonywanie requestów HTTP do API
 * - Obsługę serializacji/deserializacji JSON
 * - Obsługę błędów sieciowych
 * - Zwracanie wyników w postaci Result<T>
 *
 * Używa Ktor Client z Content Negotiation dla JSON
 */
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

    suspend fun googleSignIn(idToken: String): Result<GoogleSignInResponseData> =
        safeCall<GoogleSignInResponse, GoogleSignInResponseData> {
            post("$baseUrl/mobile/Auth/GoogleSignIn") {
                contentType(ContentType.Application.Json)
                setBody(GoogleSignInRequest(idToken))
            }.body()
        }

    suspend fun getBalance(): Result<List<BalanceItem>> = 
        safeCall<GetBalanceResponse, List<BalanceItem>> {
            get("$baseUrl/balance").body()
        }

    suspend fun topUp(venueId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponseData> = 
        safeCall<TopUpResponse, TopUpResponseData> {
            post("$baseUrl/balance") {
                contentType(ContentType.Application.Json)
                setBody(TopUpRequest(venueId, paymentMethodId, balance))
            }.body()
        }

    suspend fun getPaymentOperators(): Result<List<PaymentOperator>> =
        safeCall<GetPaymentOperatorsResponse, List<PaymentOperator>> {
            get("$baseUrl/payment-operators").body()
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
