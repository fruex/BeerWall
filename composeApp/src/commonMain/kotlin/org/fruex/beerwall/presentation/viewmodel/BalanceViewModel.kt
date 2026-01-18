package org.fruex.beerwall.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fruex.beerwall.domain.usecase.GetBalancesUseCase
import org.fruex.beerwall.domain.usecase.GetPaymentOperatorsUseCase
import org.fruex.beerwall.domain.usecase.TopUpBalanceUseCase
import org.fruex.beerwall.presentation.mapper.toUi
import org.fruex.beerwall.presentation.mapper.toUiMethods
import org.fruex.beerwall.ui.models.PaymentMethod
import org.fruex.beerwall.ui.models.VenueBalance

/**
 * ViewModel odpowiedzialny za zarządzanie saldem użytkownika.
 *
 * Zarządza:
 * - Pobieraniem sald z różnych lokali
 * - Doładowywaniem środków
 * - Metodami płatności
 */
class BalanceViewModel(
    private val getBalancesUseCase: GetBalancesUseCase,
    private val topUpBalanceUseCase: TopUpBalanceUseCase,
    private val getPaymentOperatorsUseCase: GetPaymentOperatorsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BalanceUiState())
    val uiState: StateFlow<BalanceUiState> = _uiState.asStateFlow()

    init {
        refreshBalance()
        loadPaymentMethods()
    }

    fun refreshBalance() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            try {
                getBalancesUseCase()
                    .onSuccess { balances ->
                        _uiState.update { it.copy(balances = balances.toUi()) }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Błąd pobierania salda") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd pobierania salda") }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun onAddFunds(premisesId: Int, paymentMethodId: Int, balance: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            try {
                topUpBalanceUseCase(premisesId, paymentMethodId, balance)
                    .onSuccess {
                        // Odpowiedź przyjdzie przez webhook, nie czekamy na response
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = "Nie udało się doładować konta: ${error.message}") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Nie udało się doładować konta: ${e.message}") }
            }
        }
    }

    private fun loadPaymentMethods() {
        viewModelScope.launch {
            try {
                getPaymentOperatorsUseCase()
                    .onSuccess { operators ->
                        val methods = operators.flatMap { it.paymentMethods }
                        _uiState.update { it.copy(paymentMethods = methods.toUiMethods()) }
                    }
            } catch (_: Exception) {
                // Ignorujemy błędy ładowania metod płatności
            }
        }
    }

    fun onClearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class BalanceUiState(
    val balances: List<VenueBalance> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
