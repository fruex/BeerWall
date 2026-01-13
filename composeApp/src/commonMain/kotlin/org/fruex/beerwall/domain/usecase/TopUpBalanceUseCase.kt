package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.balance.TopUpResponseData

class TopUpBalanceUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(premisesId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponseData> {
        return balanceRepository.topUp(premisesId, paymentMethodId, balance)
    }
}
