package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.repository.BalanceRepository

class GetBalancesUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(): Result<List<Balance>> {
        return balanceRepository.getBalances()
    }
}
