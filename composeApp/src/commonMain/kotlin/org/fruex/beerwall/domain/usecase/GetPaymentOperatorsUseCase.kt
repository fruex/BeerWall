package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.domain.repository.BalanceRepository
import org.fruex.beerwall.data.remote.dto.operators.PaymentOperatorResponse
// TODO: UseCase zwraca DTO (`PaymentOperatorResponse`) z warstwy remote. Należy to zmienić, aby zwracał model domenowy.

/**
 * Przypadek użycia do pobierania dostępnych operatorów płatności.
 *
 * @property balanceRepository Repozytorium sald (gdzie zdefiniowana jest ta operacja).
 */
class GetPaymentOperatorsUseCase(
    private val balanceRepository: BalanceRepository
) {
    /**
     * Pobiera operatorów płatności.
     *
     * @return [Result] zawierający listę [PaymentOperatorResponse] lub błąd.
     */
    suspend operator fun invoke(): Result<List<PaymentOperatorResponse>> {
        return balanceRepository.getPaymentOperators()
    }
}
