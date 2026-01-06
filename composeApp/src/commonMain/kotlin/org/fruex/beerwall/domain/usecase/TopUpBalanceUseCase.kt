package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository

class TopUpBalanceUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(paymentMethodId: Int, balance: Double): Result<Double> {
        return balanceRepository.topUp(paymentMethodId, balance)
    }
}
