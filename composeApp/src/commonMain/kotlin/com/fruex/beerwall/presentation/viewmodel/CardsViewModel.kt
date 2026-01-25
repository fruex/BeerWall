package com.fruex.beerwall.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fruex.beerwall.domain.repository.NfcRepository
import com.fruex.beerwall.domain.usecase.AssignCardUseCase
import com.fruex.beerwall.domain.usecase.GetCardsUseCase
import com.fruex.beerwall.domain.usecase.UpdateCardUseCase
import com.fruex.beerwall.presentation.mapper.toUi
import com.fruex.beerwall.ui.models.UserCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel odpowiedzialny za zarządzanie kartami NFC użytkownika.
 *
 * Zarządza:
 * - Pobieraniem listy kart
 * - Przełączaniem statusu karty (aktywna/nieaktywna)
 * - Dodawaniem nowych kart
 */
class CardsViewModel(
    private val getCardsUseCase: GetCardsUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val assignCardUseCase: AssignCardUseCase,
    private val nfcRepository: NfcRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardsUiState())
    val uiState: StateFlow<CardsUiState> = _uiState.asStateFlow()

    init {
        refreshCards()
        observeNfcState()
    }

    private fun observeNfcState() {
        viewModelScope.launch {
            nfcRepository.scannedCardId.collect { id ->
                _uiState.update { it.copy(scannedCardId = id) }
            }
        }
        viewModelScope.launch {
            nfcRepository.isNfcEnabled.collect { isEnabled ->
                _uiState.update { it.copy(isNfcEnabled = isEnabled) }
            }
        }
    }

    fun startNfcListening() {
        viewModelScope.launch {
            nfcRepository.clearScannedCard()
            nfcRepository.setScanning(true)
        }
    }

    fun stopNfcListening() {
        viewModelScope.launch {
            nfcRepository.setScanning(false)
        }
    }

    fun refreshCards() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            try {
                getCardsUseCase()
                    .onSuccess { cards ->
                        _uiState.update { it.copy(cards = cards.toUi()) }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Błąd pobierania kart") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd pobierania kart") }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun onToggleCardStatus(cardId: String) {
        val card = _uiState.value.cards.find { it.cardGuid == cardId } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            try {
                val newStatus = !card.isActive
                updateCardUseCase(cardId, card.description, newStatus)
                    .onSuccess {
                        updateCardStatus(cardId, newStatus)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = "Nie udało się zmienić statusu karty: ${error.message}") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Nie udało się zmienić statusu karty: ${e.message}") }
            }
        }
    }

    fun onSaveCard(cardId: String, description: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            try {
                assignCardUseCase(cardId, description)
                    .onSuccess {
                        refreshCards()
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = "Nie udało się zapisać karty: ${error.message}") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Nie udało się zapisać karty: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun updateCardStatus(cardId: String, isActive: Boolean) {
        _uiState.update { currentState ->
            val updatedCards = currentState.cards.map {
                if (it.cardGuid == cardId) it.copy(isActive = isActive) else it
            }
            currentState.copy(cards = updatedCards)
        }
    }
}

data class CardsUiState(
    val cards: List<UserCard> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val scannedCardId: String? = null,
    val isNfcEnabled: Boolean = false
)
