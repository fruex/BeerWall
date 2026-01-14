package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.balance.TopUpResponse

class TopUpBalanceUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(premisesId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponse> {
        return balanceRepository.topUp(premisesId, paymentMethodId, balance)
    }
}
