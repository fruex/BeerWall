package com.fruex.beerwall.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.fruex.beerwall.domain.usecase.GetBalancesUseCase
import com.fruex.beerwall.domain.usecase.GetPaymentOperatorsUseCase
import com.fruex.beerwall.domain.usecase.TopUpBalanceUseCase
import com.fruex.beerwall.presentation.mapper.toUi
import com.fruex.beerwall.presentation.mapper.toUiMethods
import com.fruex.beerwall.ui.models.PaymentMethod
import com.fruex.beerwall.ui.models.PremisesBalance

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

    private var topUpJob: kotlinx.coroutines.Job? = null

    fun onAddFunds(premisesId: Int, paymentMethodId: Int, balance: Double, authorizationCode: String? = null) {
        topUpJob?.cancel()
        topUpJob = viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, isLoading = true) }
            try {
                topUpBalanceUseCase(premisesId, paymentMethodId, balance, authorizationCode)
                    .onSuccess {
                        // Odśwież saldo po udanym doładowaniu
                        refreshBalance()
                    }
                    .onFailure { error ->
                        val mappedError = mapTopUpError(error.message)
                        _uiState.update { it.copy(errorMessage = mappedError) }
                    }
            } catch (e: Exception) {
                // Ignore cancellation exceptions
                if (e is kotlinx.coroutines.CancellationException) throw e
                _uiState.update { it.copy(errorMessage = "Nie udało się doładować konta: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onCancelTopUp() {
        topUpJob?.cancel()
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun mapTopUpError(message: String?): String {
        return when (message) {
            "ABANDONED" -> "Płatność porzucona"
            "ERROR" -> "Błąd płatności"
            "EXPIRED" -> "Płatność wygasła"
            "REJECTED" -> "Płatność odrzucona"
            else -> message ?: "Nieznany błąd płatności"
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
    val balances: List<PremisesBalance> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
