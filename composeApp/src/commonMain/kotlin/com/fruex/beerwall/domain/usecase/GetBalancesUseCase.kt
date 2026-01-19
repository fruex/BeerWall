package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.model.Balance
import com.fruex.beerwall.domain.repository.BalanceRepository

/**
 * Przypadek użycia do pobierania listy sald użytkownika.
 *
 * @property balanceRepository Repozytorium sald.
 */
class GetBalancesUseCase(
    private val balanceRepository: BalanceRepository
) {
    /**
     * Pobiera salda.
     *
     * @return [Result] zawierający listę obiektów [Balance] lub błąd.
     */
    suspend operator fun invoke(): Result<List<Balance>> {
        return balanceRepository.getBalances()
    }
}
