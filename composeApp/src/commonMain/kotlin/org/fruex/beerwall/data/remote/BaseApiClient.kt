package org.fruex.beerwall.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.fruex.beerwall.BuildKonfig
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.getPlatform
import org.fruex.beerwall.log
import org.fruex.beerwall.remote.common.ApiResponse
import org.fruex.beerwall.remote.dto.auth.RefreshTokenRequest
import org.fruex.beerwall.remote.dto.auth.RefreshTokenResponse

/**
 * Klasa bazowa dla wszystkich klientów API.
 * Dostarcza wspólną funkcjonalność dla żądań HTTP, zarządzania tokenami i obsługi błędów.
 *
 * @property tokenManager Menedżer tokenów uwierzytelniających.
 */
abstract class BaseApiClient(
    protected val tokenManager: TokenManager
) {
    protected val client: HttpClient = HttpClientFactory.create()
    protected val json = HttpClientFactory.json
    protected val platform = getPlatform()
    protected val baseUrl: String = BuildKonfig.BASE_URL

    private val refreshMutex = Mutex()
    private var isRefreshing = false

    var onUnauthorized: (suspend () -> Unit)? = null

    /**
     * Dodaje token autoryzacyjny (Bearer) do nagłówków żądania.
     */
    protected suspend fun HttpRequestBuilder.addAuthToken() {
        tokenManager.getToken()?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    /**
     * Próbuje odświeżyć token dostępu przy użyciu tokenu odświeżania.
     *
     * @return true jeśli token został pomyślnie odświeżony, false w przeciwnym razie.
     */
    protected suspend fun tryRefreshToken(): Boolean {
        if (tokenManager.isRefreshTokenExpired()) {
            onUnauthorized?.invoke()
            return false
        }

        return refreshMutex.withLock {
            if (isRefreshing) {
                return@withLock true
            }

            isRefreshing = true
            try {
                val refreshTokenValue = tokenManager.getRefreshToken()
                if (refreshTokenValue == null) {
                    onUnauthorized?.invoke()
                    return@withLock false
                }

                val response: HttpResponse = client.post("$baseUrl/mobile/auth/refreshToken") {
                    contentType(ContentType.Application.Json)
                    setBody(RefreshTokenRequest(refreshToken = refreshTokenValue))
                }

                if (response.status == HttpStatusCode.OK) {
                    val refreshResponse: RefreshTokenResponse = response.body()
                    tokenManager.saveTokens(
                        tokens = org.fruex.beerwall.auth.AuthTokens(
                            token = refreshResponse.token,
                            tokenExpires = refreshResponse.tokenExpires,
                            refreshToken = refreshResponse.refreshToken,
                            refreshTokenExpires = refreshResponse.refreshTokenExpires,
                            firstName = null,
                            lastName = null
                        )
                    )
                    true
                } else {
                    onUnauthorized?.invoke()
                    false
                }
            } catch (e: Exception) {
                platform.log("Token refresh failed: ${e.message}", "BaseApiClient", LogSeverity.ERROR)
                onUnauthorized?.invoke()
                false
            } finally {
                isRefreshing = false
            }
        }
    }

    /**
     * Wykonuje żądanie HTTP bez autoryzacji.
     * Obsługuje odpowiedzi oparte na kopercie (ApiEnvelope) i błędy.
     *
     * @param block Blok kodu wykonujący żądanie HTTP.
     * @return Result zawierający dane typu [D] lub błąd.
     */
    protected suspend inline fun <reified T : ApiResponse<D>, D> safeCall(
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

    /**
     * Wykonuje żądanie HTTP z autoryzacją i automatycznym odświeżaniem tokenu.
     * Obsługuje błąd 401 poprzez próbę odświeżenia tokenu i ponowienie żądania.
     *
     * @param block Blok kodu wykonujący żądanie HTTP.
     * @return Result zawierający dane typu [D] lub błąd.
     */
    protected suspend inline fun <reified T : ApiResponse<D>, D> safeCallWithAuth(
        crossinline block: suspend HttpClient.() -> T
    ): Result<D> = try {
        var response = client.block()

        // Jeśli 401, spróbuj odświeżyć token i ponów
        if (response.data == null && response.error?.message?.contains("401") == true) {
            if (tryRefreshToken()) {
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
}
