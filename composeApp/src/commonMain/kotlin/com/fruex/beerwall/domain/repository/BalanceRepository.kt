package com.fruex.beerwall.domain.repository

import com.fruex.beerwall.domain.model.Balance
import com.fruex.beerwall.domain.model.GdprClause
import com.fruex.beerwall.domain.model.PaymentOperator

/**
 * Interfejs repozytorium do zarządzania saldami i płatnościami.
 */
interface BalanceRepository {
    /**
     * Pobiera treść klauzuli RODO (wymagane dla Paynow White Label).
     *
     * @return [Result] zawierający [GdprClause] lub błąd.
     */
    suspend fun getGdprClause(): Result<GdprClause>

    /**
     * Pobiera listę sald użytkownika w różnych lokalach.
     *
     * @return [Result] zawierający listę obiektów [Balance] lub błąd.
     */
    suspend fun getBalances(): Result<List<Balance>>

    /**
     * Doładowuje konto użytkownika w określonym lokalu.
     *
     * @param premisesId Identyfikator lokalu.
     * @param paymentMethodId Identyfikator metody płatności.
     * @param balance Kwota doładowania.
     * @param authorizationCode Kod autoryzacyjny (np. BLIK).
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend fun topUp(premisesId: Int, paymentMethodId: Int, balance: Double, authorizationCode: String? = null): Result<Unit>

    /**
     * Pobiera listę dostępnych operatorów płatności.
     *
     * @return [Result] zawierający listę operatorów [PaymentOperator] lub błąd.
     */
    suspend fun getPaymentOperators(): Result<List<PaymentOperator>>
}
