package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.operators.PaymentOperatorResponse

/**
 * Przypadek użycia do pobierania operatorów płatności.
 *
 * @property balanceRepository Repozytorium salda (zawiera metody płatności).
 */
class GetPaymentOperatorsUseCase(
    private val balanceRepository: BalanceRepository
) {
    /**
     * Pobiera listę operatorów płatności.
     * @return Result z listą operatorów.
     * // TODO: Dodać mapowanie na model domenowy zamiast zwracać DTO (PaymentOperator).
     */
    suspend operator fun invoke(): Result<List<PaymentOperatorResponse>> {
        return balanceRepository.getPaymentOperators()
    }
}
