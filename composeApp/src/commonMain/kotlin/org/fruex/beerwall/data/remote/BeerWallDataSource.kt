package org.fruex.beerwall.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.remote.common.ApiResponse
import org.fruex.beerwall.remote.dto.auth.*
import org.fruex.beerwall.remote.dto.balance.*
import org.fruex.beerwall.remote.dto.cards.*
import org.fruex.beerwall.remote.dto.history.GetHistoryResponse
import org.fruex.beerwall.remote.dto.history.TransactionDto
import org.fruex.beerwall.remote.dto.operators.GetPaymentOperatorsResponse
import org.fruex.beerwall.remote.dto.operators.PaymentOperator

/**
 * Data Source do komunikacji z API BeerWall
 *
 * Odpowiedzialny za:
 * - Wykonywanie requestów HTTP do API
 * - Obsługę serializacji/deserializacji JSON
 * - Obsługę błędów sieciowych
 * - Dodawanie tokenu autoryzacji do requestów
 * - Zwracanie wyników w postaci Result<T>
 *
 * Używa Ktor Client z Content Negotiation dla JSON
 */
class BeerWallDataSource(
    private val tokenManager: TokenManager
) {
    private val refreshMutex = Mutex()
    private var isRefreshing = false

    var onUnauthorized: (suspend () -> Unit)? = null

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
            filter { request ->
                request.url.host.contains("igibeer")
            }
        }
    }

    private suspend fun HttpRequestBuilder.addAuthToken() {
        tokenManager.getToken()?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    private suspend fun tryRefreshToken(): Boolean {
        // Sprawdź czy refresh token nie wygasł
        if (tokenManager.isRefreshTokenExpired()) {
            onUnauthorized?.invoke()
            return false
        }

        // Użyj mutex aby zapobiec wielokrotnym równoległym próbom odświeżenia
        return refreshMutex.withLock {
            if (isRefreshing) {
                // Inny request już odświeża token
                return@withLock true // Poczekaj na zakończenie
            }

            isRefreshing = true
            try {
                val refreshTokenValue = tokenManager.getRefreshToken()
                if (refreshTokenValue == null) {
                    onUnauthorized?.invoke()
                    return@withLock false
                }

                val result = refreshToken(refreshTokenValue)
                result.isSuccess
            } catch (e: Exception) {
                onUnauthorized?.invoke()
                false
            } finally {
                isRefreshing = false
            }
        }
    }

    private suspend inline fun <reified T : ApiResponse<D>, D> safeCall(
        crossinline block: suspend HttpClient.() -> T
    ): Result<D> = try {
        println("📤 API Request: ${T::class.simpleName}")
        val response = client.block()
        println("📥 API Response: ${response.data != null} - Error: ${response.error?.message}")

        if (response.data != null) {
            Result.success(response.data!!)
        } else {
            val errorMsg = response.error?.message ?: "Unknown error"
            println("❌ API Error: $errorMsg")
            Result.failure(Exception(errorMsg))
        }
    } catch (e: Exception) {
        println("❌ API Exception: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    private suspend inline fun <reified T : ApiResponse<D>, D> safeCallWithAuth(
        crossinline block: suspend HttpClient.() -> T
    ): Result<D> = try {
        var response = client.block()

        // Jeśli otrzymaliśmy 401, spróbuj odświeżyć token
        if (response.data == null && response.error?.message?.contains("401") == true) {
            if (tryRefreshToken()) {
                // Token odświeżony, spróbuj ponownie
                response = client.block()
            }
        }

        if (response.data != null) {
            Result.success(response.data!!)
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun googleSignIn(idToken: String): Result<GoogleSignInResponseData> = try {
        println("📤 Google SignIn Request to .NET Backend")
        println("  🔑 ID Token (first 50 chars): ${idToken.take(50)}...")
        println("  📏 ID Token length: ${idToken.length}")
        println("  🌐 Endpoint: ${ApiConfig.BASE_URL}/mobile/Auth/GoogleSignIn")

        val httpResponse: HttpResponse = client.post("${ApiConfig.BASE_URL}/mobile/Auth/GoogleSignIn") {
            contentType(ContentType.Application.Json)
            setBody(GoogleSignInRequest(idToken))
        }

        println("📥 Google SignIn Response from .NET Backend")
        println("  📊 HTTP Status: ${httpResponse.status.value} ${httpResponse.status.description}")
        println("  📋 Content-Type: ${httpResponse.contentType()}")

        when (httpResponse.status) {
            HttpStatusCode.OK -> {
                val response: GoogleSignInResponse = httpResponse.body()
                if (response.data != null) {
                    println("✅ Google SignIn Success")
                    println("  👤 Backend returned .NET token")
                    Result.success(response.data!!)
                } else {
                    val errorMsg = response.error?.message ?: "Unknown error"
                    println("❌ Google SignIn Error from API response: $errorMsg")
                    Result.failure(Exception(errorMsg))
                }
            }
            HttpStatusCode.Unauthorized -> {
                val bodyText = httpResponse.bodyAsText()
                println("❌ 401 Unauthorized from .NET Backend")
                println("  📄 Full Response Body:")
                println("  $bodyText")
                println("")
                println("  💡 Możliwe przyczyny:")
                println("     1. Backend nie może zweryfikować tokenu Google")
                println("     2. Nieprawidłowy Google Client ID w konfiguracji backendu")
                println("     3. Token Google wygasł podczas transmisji")
                println("     4. Backend wymaga innych claims w tokenie")

                // Spróbuj sparsować jako JSON
                try {
                    val jsonBody = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                        .decodeFromString<GoogleSignInResponse>(bodyText)
                    println("  🔍 Parsed error message: ${jsonBody.error?.message}")
                } catch (e: Exception) {
                    println("  ⚠️ Response is not JSON format")
                }

                Result.failure(Exception("Backend .NET zwrócił 401: Token odrzucony. Sprawdź logi backendu."))
            }
            else -> {
                val bodyText = httpResponse.bodyAsText()
                println("❌ HTTP ${httpResponse.status.value}: $bodyText")
                Result.failure(Exception("HTTP ${httpResponse.status.value}: ${httpResponse.status.description}"))
            }
        }
    } catch (e: Exception) {
        println("❌ Google SignIn Exception: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun emailPasswordSignIn(email: String, password: String): Result<EmailPasswordSignInResponseData> =
        safeCall<EmailPasswordSignInResponse, EmailPasswordSignInResponseData> {
            post("${ApiConfig.BASE_URL}/mobile/Auth/SignIn") {
                contentType(ContentType.Application.Json)
                setBody(EmailPasswordSignInRequest(email, password))
            }.body()
        }

    suspend fun refreshToken(refreshToken: String): Result<RefreshTokenResponseData> =
        safeCall<RefreshTokenResponse, RefreshTokenResponseData> {
            post("${ApiConfig.BASE_URL}/mobile/Auth/RefreshToken") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken))
            }.body()
        }

    suspend fun getBalance(): Result<List<BalanceItem>> =
        safeCallWithAuth<GetBalanceResponse, List<BalanceItem>> {
            get("${ApiConfig.BASE_URL}/mobile/User/balance") {
                addAuthToken()
            }.body()
        }

    suspend fun topUp(venueId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponseData> =
        safeCallWithAuth<TopUpResponse, TopUpResponseData> {
            post("${ApiConfig.BASE_URL}/mobile/Payment/top-up") {
                addAuthToken()
                contentType(ContentType.Application.Json)
                setBody(TopUpRequest(venueId, paymentMethodId, balance))
            }.body()
        }

    suspend fun getPaymentOperators(): Result<List<PaymentOperator>> =
        safeCallWithAuth<GetPaymentOperatorsResponse, List<PaymentOperator>> {
            get("${ApiConfig.BASE_URL}/mobile/Payment/operators") {
                addAuthToken()
            }.body()
        }

    suspend fun getCards(): Result<List<CardItemDto>> =
        safeCallWithAuth<GetCardsResponse, List<CardItemDto>> {
            get("${ApiConfig.BASE_URL}/mobile/Card") {
                addAuthToken()
            }.body()
        }

    suspend fun toggleCardStatus(cardId: String, activate: Boolean): Result<CardActivationData> =
        safeCallWithAuth<CardActivationResponse, CardActivationData> {
            post("${ApiConfig.BASE_URL}/mobile/Card/Activation") {
                addAuthToken()
                contentType(ContentType.Application.Json)
                setBody(CardActivationRequest(cardId, activate))
            }.body()
        }

    suspend fun getHistory(): Result<List<TransactionDto>> =
        safeCallWithAuth<GetHistoryResponse, List<TransactionDto>> {
            get("${ApiConfig.BASE_URL}/mobile/User/History") {
                addAuthToken()
            }.body()
        }

}
