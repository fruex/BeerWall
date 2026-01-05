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

class BeerWallViewModel(
    private val apiClient: BeerWallApiClient = BeerWallApiClient()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BeerWallUiState())
    val uiState: StateFlow<BeerWallUiState> = _uiState.asStateFlow()

    private fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isRefreshing = isLoading) }
    }

    private fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    private fun launchWithLoading(block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            try {
                block()
            } catch (e: Exception) {
                setError(e.message ?: "Wystąpił nieoczekiwany błąd")
            } finally {
                setLoading(false)
            }
        }
    }

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

    fun onClearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onLogout() {
        _uiState.update { it.copy(isLoggedIn = false) }
    }

    fun refreshAllData() {
        viewModelScope.launch {
            setLoading(true)

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
                        userProfile = newState.userProfile.copy(activeCards = cards.count { it.isActive })
                    )
                }
                historyResult.onSuccess { transactions ->
                    newState = newState.copy(transactionGroups = groupTransactionsByDate(transactions))
                }
                profileResult.onSuccess { points ->
                    newState = newState.copy(
                        userProfile = newState.userProfile.copy(loyaltyPoints = points)
                    )
                }

                newState
            }
            setLoading(false)
        }
    }

    fun onAddFunds(venueName: String, amount: Double, blikCode: String) {
        viewModelScope.launch {
            apiClient.topUp(amount, venueName)
                .onSuccess { newBalance -> updateVenueBalance(venueName, newBalance) }
                .onFailure { setError("Nie udało się doładować konta: ${it.message}") }
        }
    }

    private fun updateVenueBalance(venueName: String, newBalance: Double) {
        _uiState.update { currentState ->
            val updatedBalances = currentState.balances.map {
                if (it.venueName == venueName) it.copy(balance = newBalance) else it
            }
            currentState.copy(balances = updatedBalances)
        }
    }

    fun onToggleCardStatus(cardId: String) {
        val card = _uiState.value.cards.find { it.id == cardId } ?: return
        viewModelScope.launch {
            apiClient.toggleCardStatus(cardId, !card.isActive)
                .onSuccess { isActive -> updateCardStatus(cardId, isActive) }
                .onFailure { setError("Nie udało się zmienić statusu karty: ${it.message}") }
        }
    }

    private fun updateCardStatus(cardId: String, isActive: Boolean) {
        _uiState.update { currentState ->
            val updatedCards = currentState.cards.map {
                if (it.id == cardId) it.copy(isActive = isActive) else it
            }
            currentState.copy(
                cards = updatedCards,
                userProfile = currentState.userProfile.copy(activeCards = updatedCards.count { it.isActive })
            )
        }
    }

    fun refreshHistory() {
        launchWithLoading {
            apiClient.getHistory().onSuccess { transactions ->
                _uiState.update { it.copy(transactionGroups = groupTransactionsByDate(transactions)) }
            }
        }
    }

    fun refreshBalance() {
        launchWithLoading {
            apiClient.getBalance().onSuccess { balances ->
                _uiState.update { it.copy(balances = balances) }
            }
        }
    }

    fun onDeleteCard(cardId: String) {
        updateCards { cards -> cards.filter { it.id != cardId || !it.isPhysical } }
    }

    fun onSaveCard(name: String, cardId: String) {
        val newCard = UserCard(
            id = cardId,
            name = name,
            isActive = true,
            isPhysical = true
        )
        updateCards { cards -> cards + newCard }
    }

    private fun updateCards(transform: (List<UserCard>) -> List<UserCard>) {
        _uiState.update { currentState ->
            val updatedCards = transform(currentState.cards)
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
                    initials = getUserInitials(user.displayName, currentState.userProfile.initials),
                    photoUrl = user.photoUrl
                )
            )
        }
    }

    private fun getUserInitials(displayName: String?, fallback: String): String {
        return displayName?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: fallback
    }

    private fun groupTransactionsByDate(transactions: List<org.fruex.beerwall.ui.models.Transaction>): List<DailyTransactions> {
        return transactions
            .groupBy { it.date }
            .map { (date, items) -> DailyTransactions(date.uppercase(), items) }
    }
}
