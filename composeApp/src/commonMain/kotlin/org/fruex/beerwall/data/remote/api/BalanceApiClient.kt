package org.fruex.beerwall.data.remote.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.ApiRoutes
import org.fruex.beerwall.data.remote.BaseApiClient
import org.fruex.beerwall.log
import org.fruex.beerwall.remote.dto.balance.*
import org.fruex.beerwall.remote.dto.operators.GetPaymentOperatorsEnvelope
import org.fruex.beerwall.remote.dto.operators.PaymentOperatorResponse

/**
 * Klient API do obsługi operacji finansowych (saldo, płatności).
 * Obsługuje pobieranie salda, doładowania konta oraz pobieranie metod płatności.
 */
class BalanceApiClient(tokenManager: TokenManager) : BaseApiClient(tokenManager) {

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
        balance: Double
    ): Result<Unit> = try {
        platform.log("📤 TopUp Request", this, LogSeverity.INFO)
        val response = client.post("$baseUrl/${ApiRoutes.Payments.TOP_UP}") {
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
