package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.model.PaymentOperator
import org.fruex.beerwall.domain.repository.BalanceRepository

/**
 * Przypadek użycia do pobierania dostępnych operatorów płatności.
 *
 * @property balanceRepository Repozytorium sald (gdzie zdefiniowana jest ta operacja).
 */
class GetPaymentOperatorsUseCase(
    private val balanceRepository: BalanceRepository
) {
    /**
     * Pobiera operatorów płatności.
     *
     * @return [Result] zawierający listę [PaymentOperator] lub błąd.
     */
    suspend operator fun invoke(): Result<List<PaymentOperator>> {
        return balanceRepository.getPaymentOperators()
    }
}
