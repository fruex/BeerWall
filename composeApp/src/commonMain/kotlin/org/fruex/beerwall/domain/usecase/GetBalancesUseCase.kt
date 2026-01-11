package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.model.Balance
import org.fruex.beerwall.domain.repository.BalanceRepository

/**
 * Przypadek użycia do pobierania sald użytkownika.
 *
 * @property balanceRepository Repozytorium salda.
 */
class GetBalancesUseCase(
    private val balanceRepository: BalanceRepository
) {
    /**
     * Pobiera listę sald.
     * @return Result z listą obiektów [Balance].
     */
    suspend operator fun invoke(): Result<List<Balance>> {
        return balanceRepository.getBalances()
    }
}
