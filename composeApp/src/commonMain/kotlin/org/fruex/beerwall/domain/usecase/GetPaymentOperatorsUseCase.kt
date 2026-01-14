package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.operators.PaymentOperatorResponse

class GetPaymentOperatorsUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(): Result<List<PaymentOperatorResponse>> {
        return balanceRepository.getPaymentOperators()
    }
}
