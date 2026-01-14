package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.balance.TopUpResponse

/**
 * Przypadek użycia do doładowania konta.
 *
 * @property balanceRepository Repozytorium salda.
 */
class TopUpBalanceUseCase(
    private val balanceRepository: BalanceRepository
) {
    /**
     * Inicjuje doładowanie.
     * @param premisesId ID lokalu.
     * @param paymentMethodId ID metody płatności.
     * @param balance Kwota doładowania.
     * @return Result z danymi odpowiedzi [TopUpResponseData].
     */
    suspend operator fun invoke(premisesId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponse> {
        return balanceRepository.topUp(premisesId, paymentMethodId, balance)
    }
}
