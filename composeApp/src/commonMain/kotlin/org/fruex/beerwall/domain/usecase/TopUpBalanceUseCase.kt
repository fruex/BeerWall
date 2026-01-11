package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.remote.dto.balance.TopUpResponseData

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
     * @param venueId ID lokalu.
     * @param paymentMethodId ID metody płatności.
     * @param balance Kwota doładowania.
     * @return Result z danymi odpowiedzi [TopUpResponseData].
     */
    suspend operator fun invoke(venueId: Int, paymentMethodId: Int, balance: Double): Result<TopUpResponseData> {
        return balanceRepository.topUp(venueId, paymentMethodId, balance)
    }
}
