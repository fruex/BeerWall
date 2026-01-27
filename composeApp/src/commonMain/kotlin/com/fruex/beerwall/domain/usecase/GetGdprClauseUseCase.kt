package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.BalanceRepository
import com.fruex.beerwall.domain.model.GdprClause

/**
 * Przypadek użycia do pobierania klauzuli RODO (dla płatności White Label).
 *
 * @property balanceRepository Repozytorium sald.
 */
class GetGdprClauseUseCase(
    private val balanceRepository: BalanceRepository
) {
    /**
     * Pobiera klauzulę RODO.
     *
     * @return [Result] zawierający [GdprClause] lub błąd.
     */
    suspend operator fun invoke(): Result<GdprClause> {
        return balanceRepository.getGdprClause()
    }
}
