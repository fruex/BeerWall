package com.fruex.beerwall.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.fruex.beerwall.domain.usecase.GetTransactionsUseCase
import com.fruex.beerwall.presentation.mapper.groupByDate
import com.fruex.beerwall.ui.models.DailyTransactions

/**
 * ViewModel odpowiedzialny za historię transakcji użytkownika.
 *
 * Zarządza:
 * - Pobieraniem historii transakcji
 * - Grupowaniem transakcji według dat
 */
class HistoryViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        refreshHistory()
    }

    fun refreshHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            try {
                getTransactionsUseCase()
                    .onSuccess { transactions ->
                        _uiState.update { it.copy(transactionGroups = transactions.groupByDate()) }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Błąd pobierania historii") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd pobierania historii") }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun onClearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class HistoryUiState(
    val transactionGroups: List<DailyTransactions> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
