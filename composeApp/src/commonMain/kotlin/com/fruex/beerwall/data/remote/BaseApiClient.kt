package com.fruex.beerwall.data.remote

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import com.fruex.beerwall.BuildKonfig
import com.fruex.beerwall.auth.TokenManager
import com.fruex.beerwall.getPlatform
import com.fruex.beerwall.data.remote.common.ApiResponse

/**
 * Base class for all API clients.
 * Provides common functionality for HTTP requests, token management, and error handling.
 */
abstract class BaseApiClient(
    protected val tokenManager: TokenManager,
    private val onUnauthorized: (suspend () -> Unit)?
) {
    private var _client: HttpClient? = null
    protected val client: HttpClient
        get() {
            if (_client == null) {
                _client = HttpClientFactory.create(tokenManager, onUnauthorized)
            }
            return _client!!
        }

    protected val json = HttpClientFactory.json
    protected val platform = getPlatform()
    protected val baseUrl: String = BuildKonfig.BASE_URL

    /**
     * Adds authorization token to request headers.
     * NOTE: AuthPlugin automatically adds token, but this is kept for manual requests.
     */
    protected suspend fun HttpRequestBuilder.addAuthToken() {
        tokenManager.getToken()?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    /**
     * Executes HTTP request without authentication.
     * Handles envelope-based responses and errors.
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
        val response = client.block()

        if (response.data != null) {
            Result.success(response.data!!)
        } else {
            Result.failure(Exception(response.error?.message ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
