package org.fruex.beerwall.domain.repository

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.remote.dto.balance.TopUpResponse
import org.fruex.beerwall.remote.dto.operators.PaymentOperatorResponse

/**
 * Interfejs repozytorium do zarządzania saldem i płatnościami.
 */
interface BalanceRepository {
    /**
     * Pobiera listę sald użytkownika we wszystkich dostępnych lokalach.
     * @return Result z listą obiektów [Balance].
     */
    suspend fun getBalances(): Result<List<Balance>>

    /**
     * Inicjuje doładowanie konta w wybranym lokalu.
     *
     * @param premisesId ID lokalu, w którym ma nastąpić doładowanie.
     * @param paymentMethodId ID wybranej metody płatności.
     * @param balance Kwota doładowania.
     * @return Result z danymi odpowiedzi (np. link do bramki płatności).
     * // TODO: Zmienić nazwę parametru 'balance' na 'amount' dla spójności i jasności.
     */
    suspend fun topUp(premisesId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponse>

    /**
     * Pobiera listę dostępnych operatorów płatności.
     * // TODO: Rozważyć przeniesienie tego do osobnego PaymentRepository, jeśli logika płatności się rozrośnie.
     * @return Result z listą operatorów płatności.
     */
    suspend fun getPaymentOperators(): Result<List<PaymentOperatorResponse>>
}
