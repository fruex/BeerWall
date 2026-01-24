package com.fruex.beerwall.data.remote.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import com.fruex.beerwall.LogSeverity
import com.fruex.beerwall.Platform
import com.fruex.beerwall.data.local.TokenManager
import com.fruex.beerwall.data.remote.ApiRoutes
import com.fruex.beerwall.data.remote.BaseApiClient
import com.fruex.beerwall.log
import com.fruex.beerwall.data.remote.dto.balance.*
import com.fruex.beerwall.data.remote.dto.operators.GetPaymentOperatorsEnvelope
import com.fruex.beerwall.data.remote.dto.operators.PaymentOperatorResponse

import io.ktor.client.HttpClient

/**
 * Klient API do obsługi operacji finansowych (saldo, płatności).
 * Obsługuje pobieranie salda, doładowania konta oraz pobieranie metod płatności.
 */
class BalanceApiClient(
    tokenManager: TokenManager,
    onUnauthorized: (suspend () -> Unit)? = null,
    httpClient: HttpClient? = null,
    platform: Platform = com.fruex.beerwall.getPlatform()
) : BaseApiClient(tokenManager, onUnauthorized, httpClient, platform) {

    /**
     * Pobiera salda użytkownika we wszystkich lokalach.
     *
     * @return Result zawierający listę [BalanceResponse] lub błąd.
     */
    suspend fun getBalance(): Result<List<BalanceResponse>> =
        safeCallWithAuth<GetBalanceEnvelope, List<BalanceResponse>> {
            get("$baseUrl/${ApiRoutes.Users.BALANCE}") {
                addAuthToken()
            }.body()
        }

    /**
     * Doładowuje konto w wybranym lokalu przy użyciu określonej metody płatności.
     *
     * @param premisesId Identyfikator lokalu.
     * @param paymentMethodId Identyfikator metody płatności.
     * @param balance Kwota doładowania.
     * @return Result pusty w przypadku sukcesu lub błąd.
     */
    suspend fun topUp(
        premisesId: Int,
        paymentMethodId: Int,
        balance: Double,
        authorizationCode: String? = null
    ): Result<Unit> = try {
        platform.log("📤 TopUp Request", this, LogSeverity.INFO)
        val response = client.post("$baseUrl/${ApiRoutes.Payments.TOP_UP}") {
            addAuthToken()
            contentType(ContentType.Application.Json)
            setBody(TopUpRequest(premisesId, paymentMethodId, balance, authorizationCode))
        }

        if (response.status.isSuccess()) {
            platform.log("✅ TopUp Success", this, LogSeverity.INFO)
            Result.success(Unit)
        } else when (response.status) {
            HttpStatusCode.Unauthorized -> {
                platform.log("❌ TopUp Unauthorized", this, LogSeverity.ERROR)
                Result.failure(Exception("Unauthorized"))
            }
            else -> {
                val bodyText = response.bodyAsText()
                platform.log("❌ TopUp Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
                Result.failure(Exception(if (bodyText.isNotBlank()) bodyText else "Error topping up: ${response.status}"))
            }
        }
    } catch (e: Exception) {
        platform.log("❌ TopUp Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }

    /**
     * Pobiera dostępne metody/operatorów płatności.
     *
     * @return Result zawierający listę [PaymentOperatorResponse] lub błąd.
     */
    suspend fun getPaymentOperators(): Result<List<PaymentOperatorResponse>> =
        safeCallWithAuth<GetPaymentOperatorsEnvelope, List<PaymentOperatorResponse>> {
            get("$baseUrl/${ApiRoutes.Payments.OPERATORS}") {
                addAuthToken()
            }.body()
        }
}
