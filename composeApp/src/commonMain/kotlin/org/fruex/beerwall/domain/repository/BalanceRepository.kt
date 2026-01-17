package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.data.remote.dto.operators.PaymentOperatorResponse
// TODO: Repozytorium domeny zwraca DTO (`PaymentOperatorResponse`) z warstwy remote. Należy stworzyć odpowiedni model domeny i mapować dane, aby uniezależnić domenę od warstwy danych/API.

/**
 * Interfejs repozytorium do zarządzania saldami i płatnościami.
 */
interface BalanceRepository {
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
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend fun topUp(premisesId: Int, paymentMethodId: Int, balance: Double): Result<Unit>

    /**
     * Pobiera listę dostępnych operatorów płatności.
     *
     * @return [Result] zawierający listę operatorów [PaymentOperatorResponse] lub błąd.
     */
    suspend fun getPaymentOperators(): Result<List<PaymentOperatorResponse>>
}
