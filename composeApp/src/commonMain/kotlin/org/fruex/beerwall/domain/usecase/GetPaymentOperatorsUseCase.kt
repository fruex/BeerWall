package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.operators.PaymentOperator

class GetPaymentOperatorsUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(): Result<List<PaymentOperator>> {
        return balanceRepository.getPaymentOperators()
    }
}
