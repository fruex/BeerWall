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
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.getPlatform
import org.fruex.beerwall.log
import org.fruex.beerwall.remote.common.ApiResponse
import org.fruex.beerwall.remote.dto.auth.*
import org.fruex.beerwall.remote.dto.balance.GetBalanceResponse
import org.fruex.beerwall.remote.dto.balance.TopUpRequest
import org.fruex.beerwall.remote.dto.balance.TopUpResponse
import org.fruex.beerwall.remote.dto.balance.TopUpResponseData
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
    private val platform = getPlatform()

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
            logger = object : Logger {
                override fun log(message: String) {
                    platform.log(message, "KtorClient", LogSeverity.DEBUG)
                }
            }
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
        platform.log("📤 API Request: ${T::class.simpleName}", this, LogSeverity.INFO)
        val response = client.block()
        platform.log("📥 API Response: ${response.data != null} - Error: ${response.error?.message}", this, LogSeverity.INFO)

        if (response.data != null) {
            Result.success(response.data!!)
        } else {
            val errorMsg = response.error?.message ?: "Unknown error"
            platform.log("❌ API Error: $errorMsg", this, LogSeverity.ERROR)
            Result.failure(Exception(errorMsg))
        }
    } catch (e: Exception) {
        platform.log("❌ API Exception: ${e.message}", this, LogSeverity.ERROR)
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
        platform.log("📤 Google SignIn Request to .NET Backend", this, LogSeverity.INFO)
        platform.log("  🔑 ID Token (first 50 chars): ${idToken.take(50)}...", this, LogSeverity.DEBUG)
        platform.log("  📏 ID Token length: ${idToken.length}", this, LogSeverity.DEBUG)
        platform.log("  🌐 Endpoint: ${ApiConfig.BASE_URL}/mobile/Auth/GoogleSignIn", this, LogSeverity.DEBUG)

        val httpResponse: HttpResponse = client.post("${ApiConfig.BASE_URL}/mobile/Auth/GoogleSignIn") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $idToken")
        }

        platform.log("📥 Google SignIn Response from .NET Backend", this, LogSeverity.INFO)
        platform.log("  📊 HTTP Status: ${httpResponse.status.value} ${httpResponse.status.description}", this, LogSeverity.DEBUG)
        platform.log("  📋 Content-Type: ${httpResponse.contentType()}", this, LogSeverity.DEBUG)

        when (httpResponse.status) {
            HttpStatusCode.OK -> {
                val response: GoogleSignInResponse = httpResponse.body()
                if (response.data != null) {
                    platform.log("✅ Google SignIn Success", this, LogSeverity.INFO)
                    platform.log("  👤 Backend returned .NET token", this, LogSeverity.DEBUG)
                    Result.success(response.data)
                } else {
                    val errorMsg = response.error?.message ?: "Unknown error"
                    platform.log("❌ Google SignIn Error from API response: $errorMsg", this, LogSeverity.ERROR)
                    Result.failure(Exception(errorMsg))
                }
            }
            HttpStatusCode.Unauthorized -> {
                val bodyText = httpResponse.bodyAsText()
                platform.log("❌ 401 Unauthorized from .NET Backend", this, LogSeverity.ERROR)
                platform.log("  📄 Full Response Body:", this, LogSeverity.DEBUG)
                platform.log("  $bodyText", this, LogSeverity.DEBUG)
                platform.log("", this, LogSeverity.DEBUG)
                platform.log("  💡 Możliwe przyczyny:", this, LogSeverity.WARN)
                platform.log("     1. Backend nie może zweryfikować tokenu Google", this, LogSeverity.WARN)
                platform.log("     2. Nieprawidłowy Google Client ID w konfiguracji backendu", this, LogSeverity.WARN)
                platform.log("     3. Token Google wygasł podczas transmisji", this, LogSeverity.WARN)
                platform.log("     4. Backend wymaga innych claims w tokenie", this, LogSeverity.WARN)

                // Spróbuj sparsować jako JSON
                try {
                    val jsonBody = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                        .decodeFromString<GoogleSignInResponse>(bodyText)
                    platform.log("  🔍 Parsed error message: ${jsonBody.error?.message}", this, LogSeverity.ERROR)
                } catch (e: Exception) {
                    platform.log("  ⚠️ Response is not JSON format", this, LogSeverity.WARN)
                }

                Result.failure(Exception("Backend .NET zwrócił 401: Token odrzucony. Sprawdź logi backendu."))
            }
            else -> {
                val bodyText = httpResponse.bodyAsText()
                platform.log("❌ HTTP ${httpResponse.status.value}: $bodyText", this, LogSeverity.ERROR)
                Result.failure(Exception("HTTP ${httpResponse.status.value}: ${httpResponse.status.description}"))
            }
        }
    } catch (e: Exception) {
        platform.log("❌ Google SignIn Exception: ${e.message}", this, LogSeverity.ERROR)
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun emailPasswordSignIn(email: String, password: String): Result<EmailPasswordSignInResponse> = try {
        platform.log("📤 Email SignIn Request", this, LogSeverity.INFO)
        val response = client.post("${ApiConfig.BASE_URL}/mobile/Auth/SignIn") {
            contentType(ContentType.Application.Json)
            setBody(EmailPasswordSignInRequest(email, password))
        }

        if (response.status == HttpStatusCode.OK) {
            // API zwraca bezpośrednio obiekt danych, a nie wrapper ApiResponse
            val responseData: EmailPasswordSignInResponse = response.body()
            platform.log("✅ Email SignIn Success", this, LogSeverity.INFO)
            Result.success(responseData)
        } else {
            val bodyText = response.bodyAsText()
            platform.log("❌ Email SignIn Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
            Result.failure(Exception("Błąd logowania: ${response.status}"))
        }
    } catch (e: Exception) {
        platform.log("❌ Email SignIn Exception: ${e.message}", this, LogSeverity.ERROR)
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun refreshToken(refreshToken: String): Result<RefreshTokenResponseData> =
        safeCall<RefreshTokenResponse, RefreshTokenResponseData> {
            post("${ApiConfig.BASE_URL}/mobile/Auth/RefreshToken") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken))
            }.body()
        }

    suspend fun getBalance(): Result<List<GetBalanceResponse>> = try {
        platform.log("📤 GetBalance Request", this, LogSeverity.INFO)
        val response = client.get("${ApiConfig.BASE_URL}/mobile/User/balance") {
            addAuthToken()
        }
        
        if (response.status == HttpStatusCode.OK) {
            // API zwraca bezpośrednio listę, a nie wrapper ApiResponse
            val responseData: List<GetBalanceResponse> = response.body()
            platform.log("✅ GetBalance Success", this, LogSeverity.INFO)
            Result.success(responseData)
        } else {
            val bodyText = response.bodyAsText()
            platform.log("❌ GetBalance Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
            Result.failure(Exception("Błąd pobierania salda: ${response.status}"))
        }
    } catch (e: Exception) {
        platform.log("❌ GetBalance Exception: ${e.message}", this, LogSeverity.ERROR)
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun topUp(premisesId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponseData> =
        safeCallWithAuth<TopUpResponse, TopUpResponseData> {
            post("${ApiConfig.BASE_URL}/mobile/Payment/top-up") {
                addAuthToken()
                contentType(ContentType.Application.Json)
                setBody(TopUpRequest(premisesId, paymentMethodId, balance))
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
