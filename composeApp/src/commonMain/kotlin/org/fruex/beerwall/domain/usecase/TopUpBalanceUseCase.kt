package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository

class TopUpBalanceUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(premisesId: Int, paymentMethodId: Int, balance: Double): Result<Unit> {
        return balanceRepository.topUp(premisesId, paymentMethodId, balance)
    }
}
