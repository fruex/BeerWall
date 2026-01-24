package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.BalanceRepository

/**
 * Przypadek użycia do doładowania salda użytkownika.
 *
 * @property balanceRepository Repozytorium sald.
 */
class TopUpBalanceUseCase(
    private val balanceRepository: BalanceRepository
) {
    /**
     * Wykonuje doładowanie salda.
     *
     * @param premisesId Identyfikator lokalu.
     * @param paymentMethodId Identyfikator metody płatności.
     * @param balance Kwota doładowania.
     * @param authorizationCode Kod autoryzacyjny (np. BLIK).
     * @return [Result] typu Unit w przypadku sukcesu lub błąd.
     */
    suspend operator fun invoke(
        premisesId: Int,
        paymentMethodId: Int,
        balance: Double,
        authorizationCode: String? = null
    ): Result<Unit> {
        return balanceRepository.topUp(premisesId, paymentMethodId, balance, authorizationCode)
    }
}
