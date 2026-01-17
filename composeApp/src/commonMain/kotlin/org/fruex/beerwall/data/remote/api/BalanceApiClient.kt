package org.fruex.beerwall.data.remote.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BaseApiClient
import org.fruex.beerwall.log
import org.fruex.beerwall.data.remote.dto.balance.*
import org.fruex.beerwall.data.remote.dto.operators.GetPaymentOperatorsEnvelope
import org.fruex.beerwall.data.remote.dto.operators.PaymentOperatorResponse

/**
 * API client for balance and payment operations.
 * Handles balance retrieval, top-ups, and payment method queries.
 */
class BalanceApiClient(tokenManager: TokenManager) : BaseApiClient(tokenManager) {

    /**
     * Retrieves all balances for current user.
     */
    suspend fun getBalance(): Result<List<BalanceResponse>> =
        safeCallWithAuth<GetBalanceEnvelope, List<BalanceResponse>> {
            get("$baseUrl/mobile/users/balance") {
                addAuthToken()
            }.body()
        }

    /**
     * Tops up balance for specified premises using payment method.
     */
    suspend fun topUp(
        premisesId: Int,
        paymentMethodId: Int,
        balance: Double
    ): Result<Unit> = try {
        platform.log("📤 TopUp Request", this, LogSeverity.INFO)
        val response = client.post("$baseUrl/mobile/payments/topUp") {
            addAuthToken()
            contentType(ContentType.Application.Json)
            setBody(TopUpRequest(premisesId, paymentMethodId, balance))
        }

        when (response.status) {
            HttpStatusCode.NoContent -> {
                platform.log("✅ TopUp Success", this, LogSeverity.INFO)
                Result.success(Unit)
            }
            HttpStatusCode.Unauthorized -> {
                platform.log("❌ TopUp Unauthorized", this, LogSeverity.ERROR)
                Result.failure(Exception("Unauthorized"))
            }
            else -> {
                val bodyText = response.bodyAsText()
                platform.log("❌ TopUp Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
                Result.failure(Exception("Error topping up: ${response.status}"))
            }
        }
    } catch (e: Exception) {
        platform.log("❌ TopUp Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }

    /**
     * Retrieves available payment operators/methods.
     */
    suspend fun getPaymentOperators(): Result<List<PaymentOperatorResponse>> =
        safeCallWithAuth<GetPaymentOperatorsEnvelope, List<PaymentOperatorResponse>> {
            get("$baseUrl/mobile/users/paymentOperators") {
                addAuthToken()
            }.body()
        }
}
