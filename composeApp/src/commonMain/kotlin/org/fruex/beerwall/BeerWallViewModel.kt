package org.fruex.beerwall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fruex.beerwall.auth.GoogleUser
import org.fruex.beerwall.remote.BeerWallApiClient
import org.fruex.beerwall.ui.BeerWallUiState
import org.fruex.beerwall.ui.models.DailyTransactions
import org.fruex.beerwall.ui.models.UserCard
import org.fruex.beerwall.ui.models.VenueBalance

class BeerWallViewModel : ViewModel() {
    private val apiClient = BeerWallApiClient()

    private val _uiState = MutableStateFlow(BeerWallUiState())
    val uiState: StateFlow<BeerWallUiState> = _uiState.asStateFlow()

    fun onSessionCheckComplete(user: GoogleUser?) {
        if (user != null) {
            updateUserProfile(user)
            _uiState.update { it.copy(isLoggedIn = true) }
        }
        _uiState.update { it.copy(isCheckingSession = false) }
    }

    fun onLoginSuccess(user: GoogleUser) {
        updateUserProfile(user)
        _uiState.update { it.copy(isLoggedIn = true) }
        refreshAllData()
    }

    fun setGuestSession() {
        _uiState.update { it.copy(isLoggedIn = true) }
        refreshAllData()
    }

    fun onLogout() {
        _uiState.update { it.copy(isLoggedIn = false) }
    }

    fun refreshAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            val balanceDeferred = async { apiClient.getBalance() }
            val cardsDeferred = async { apiClient.getCards() }
            val historyDeferred = async { apiClient.getHistory() }
            val profileDeferred = async { apiClient.getProfile() }

            val balanceResult = balanceDeferred.await()
            val cardsResult = cardsDeferred.await()
            val historyResult = historyDeferred.await()
            val profileResult = profileDeferred.await()

            _uiState.update { currentState ->
                var newState = currentState

                balanceResult.onSuccess { balances ->
                    newState = newState.copy(balances = balances)
                }
                cardsResult.onSuccess { cards ->
                    newState = newState.copy(
                        cards = cards,
                        userProfile = newState.userProfile.copy(activeCards = cards.count { card -> card.isActive })
                    )
                }
                historyResult.onSuccess { transactions ->
                    val transactionGroups = transactions
                        .groupBy { it.date }
                        .map { (date, items) -> DailyTransactions(date.uppercase(), items) }
                    newState = newState.copy(transactionGroups = transactionGroups)
                }
                profileResult.onSuccess { points ->
                    newState = newState.copy(
                        userProfile = newState.userProfile.copy(loyaltyPoints = points)
                    )
                }

                newState.copy(isRefreshing = false)
            }
        }
    }

    fun onAddFunds(venueName: String, amount: Double, blikCode: String) {
        viewModelScope.launch {
            apiClient.topUp(amount, venueName).onSuccess { newBalance ->
                _uiState.update { currentState ->
                    val updatedBalances = currentState.balances.map {
                        if (it.venueName == venueName) {
                            it.copy(balance = newBalance)
                        } else it
                    }
                    currentState.copy(balances = updatedBalances)
                }
            }.onFailure {
                // Handle error
                println("Failed to top up: ${it.message}")
            }
        }
    }

    fun onToggleCardStatus(cardId: String) {
        val card = _uiState.value.cards.find { it.id == cardId } ?: return
        viewModelScope.launch {
            apiClient.toggleCardStatus(cardId, !card.isActive).onSuccess { isActive ->
                _uiState.update { currentState ->
                    val updatedCards = currentState.cards.map {
                        if (it.id == cardId) {
                            it.copy(isActive = isActive)
                        } else it
                    }
                    currentState.copy(
                        cards = updatedCards,
                        userProfile = currentState.userProfile.copy(activeCards = updatedCards.count { it.isActive })
                    )
                }
            }
        }
    }

    fun refreshHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            apiClient.getHistory().onSuccess { transactions ->
                val transactionGroups = transactions
                    .groupBy { it.date }
                    .map { (date, items) -> DailyTransactions(date.uppercase(), items) }
                _uiState.update {
                    it.copy(
                        transactionGroups = transactionGroups,
                        isRefreshing = false
                    )
                }
            }
            // Ensure isRefreshing is set to false even on failure
            if (_uiState.value.isRefreshing) {
                 _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun refreshBalance() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            apiClient.getBalance().onSuccess { balances ->
                _uiState.update {
                    it.copy(
                        balances = balances,
                        isRefreshing = false
                    )
                }
            }
             if (_uiState.value.isRefreshing) {
                 _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun onDeleteCard(cardId: String) {
        _uiState.update { currentState ->
            val updatedCards = currentState.cards.filter { it.id != cardId || !it.isPhysical }
            currentState.copy(
                cards = updatedCards,
                userProfile = currentState.userProfile.copy(activeCards = updatedCards.count { it.isActive })
            )
        }
    }

    fun onSaveCard(name: String, cardId: String) {
         val newCard = UserCard(
            id = cardId,
            name = name,
            isActive = true,
            isPhysical = true
        )
        _uiState.update { currentState ->
            val updatedCards = currentState.cards + newCard
            currentState.copy(
                cards = updatedCards,
                userProfile = currentState.userProfile.copy(activeCards = updatedCards.count { it.isActive })
            )
        }
    }

    private fun updateUserProfile(user: GoogleUser) {
        _uiState.update { currentState ->
            currentState.copy(
                userProfile = currentState.userProfile.copy(
                    name = user.displayName ?: currentState.userProfile.name,
                    email = user.email ?: currentState.userProfile.email,
                    initials = user.displayName?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: currentState.userProfile.initials,
                    photoUrl = user.photoUrl
                )
            )
        }
    }
}
