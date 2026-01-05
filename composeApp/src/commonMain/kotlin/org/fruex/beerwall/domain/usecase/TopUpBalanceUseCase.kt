package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository

class TopUpBalanceUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(amount: Double, venueName: String): Result<Double> {
        return balanceRepository.topUp(amount, venueName)
    }
}
