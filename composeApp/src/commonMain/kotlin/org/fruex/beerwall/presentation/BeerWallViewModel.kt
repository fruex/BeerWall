package org.fruex.beerwall.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fruex.beerwall.auth.GoogleUser
import org.fruex.beerwall.domain.usecase.*
import org.fruex.beerwall.presentation.mapper.groupByDate
import org.fruex.beerwall.presentation.mapper.toUi
import org.fruex.beerwall.ui.BeerWallUiState
import org.fruex.beerwall.ui.models.UserCard

/**
 * ViewModel zarządzający stanem aplikacji BeerWall
 *
 * Odpowiedzialny za:
 * - Zarządzanie stanem UI (balanse, karty, transakcje, profil użytkownika)
 * - Komunikację z warstwą domenową poprzez Use Cases
 * - Obsługę akcji użytkownika (logowanie, dodawanie środków, zarządzanie kartami)
 * - Obsługę błędów i stanów ładowania
 *
 * @param refreshAllDataUseCase Use case do odświeżania wszystkich danych jednocześnie
 * @param getBalancesUseCase Use case do pobierania sald
 * @param topUpBalanceUseCase Use case do doładowania konta
 * @param getTransactionsUseCase Use case do pobierania historii transakcji
 * @param toggleCardStatusUseCase Use case do przełączania statusu karty
 */
class BeerWallViewModel(
    private val refreshAllDataUseCase: RefreshAllDataUseCase,
    private val getBalancesUseCase: GetBalancesUseCase,
    private val topUpBalanceUseCase: TopUpBalanceUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val toggleCardStatusUseCase: ToggleCardStatusUseCase,
    private val getPaymentOperatorsUseCase: GetPaymentOperatorsUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase
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
            // Jeśli mamy usera z lokalnej sesji Google, spróbujmy go od razu zweryfikować w backendzie
            viewModelScope.launch {
                // MOCK: Symulacja opóźnienia i sukcesu, jeśli backend nie odpowiada
                try {
                    googleSignInUseCase(user.idToken)
                        .onSuccess { backendUser ->
                            val mergedUser = backendUser.copy(
                                displayName = user.displayName ?: backendUser.displayName,
                                email = user.email ?: backendUser.email
                            )
                            onLoginSuccess(mergedUser)
                        }
                        .onFailure {
                            // W przypadku błędu (np. brak połączenia z backendem),
                            // na potrzeby testów/deweloperki logujemy lokalnie po opóźnieniu
                            delay(2000)
                            onLoginSuccess(user)
                        }
                } catch (e: Exception) {
                    delay(2000)
                    onLoginSuccess(user)
                }
            }
        } else {
            _uiState.update { it.copy(isCheckingSession = false) }
        }
    }

    fun onLoginSuccess(user: GoogleUser) {
        updateUserProfile(user)
        _uiState.update { it.copy(isLoggedIn = true, isCheckingSession = false) }
        refreshAllData()
    }

    fun handleGoogleSignIn(localUser: GoogleUser, onSuccess: () -> Unit) {
        viewModelScope.launch {
            setLoading(true)
            // Wysyłamy ID Token do backendu w celu weryfikacji i uzyskania tokenu sesyjnego (JWT)
            
            // MOCK: Symulacja opóźnienia i sukcesu, jeśli backend nie odpowiada
            try {
                googleSignInUseCase(localUser.idToken)
                    .onSuccess { backendUser ->
                        val finalUser = backendUser.copy(
                            displayName = localUser.displayName ?: backendUser.displayName,
                            email = localUser.email ?: backendUser.email
                        )

                        onLoginSuccess(finalUser)
                        onSuccess()
                    }
                    .onFailure {
                        // Fallback dla deweloperki: jeśli backend leży, zaloguj lokalnie po 3s
                        delay(3000)
                        onLoginSuccess(localUser)
                        onSuccess()
                    }
            } catch (e: Exception) {
                delay(3000)
                onLoginSuccess(localUser)
                onSuccess()
            }

            setLoading(false)
        }
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

            val allData = refreshAllDataUseCase()

            _uiState.update { currentState ->
                var newState = currentState

                allData.balances?.let { balances ->
                    newState = newState.copy(balances = balances.toUi())
                }
                allData.cards?.let { cards ->
                    newState = newState.copy(
                        cards = cards.toUi()
                    )
                }
                allData.transactions?.let { transactions ->
                    newState = newState.copy(transactionGroups = transactions.groupByDate())
                }

                newState
            }

            // Pobierz metody płatności
            loadPaymentMethods()

            setLoading(false)
        }
    }

    private fun loadPaymentMethods() {
        viewModelScope.launch {
            getPaymentOperatorsUseCase().onSuccess { operators ->
                val methods = operators.flatMap { it.paymentMethods }
                _uiState.update { it.copy(paymentMethods = methods) }
            }
        }
    }

    fun onAddFunds(venueId: Int, paymentMethodId: Int, balance: Double) {
        viewModelScope.launch {
            topUpBalanceUseCase(venueId, paymentMethodId, balance)
                .onSuccess {
                    // Odpowiedź przyjdzie przez webhook, nie czekamy na response
                }
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
            toggleCardStatusUseCase(cardId, !card.isActive)
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
                cards = updatedCards
            )
        }
    }

    fun refreshHistory() {
        launchWithLoading {
            getTransactionsUseCase().onSuccess { transactions ->
                _uiState.update { it.copy(transactionGroups = transactions.groupByDate()) }
            }
        }
    }

    fun refreshBalance() {
        launchWithLoading {
            getBalancesUseCase().onSuccess { balances ->
                _uiState.update { it.copy(balances = balances.toUi()) }
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
                cards = updatedCards
            )
        }
    }

    private fun updateUserProfile(user: GoogleUser) {
        _uiState.update { currentState ->
            currentState.copy(
                userProfile = currentState.userProfile.copy(
                    name = user.displayName ?: currentState.userProfile.name,
                    email = user.email ?: currentState.userProfile.email,
                    initials = getUserInitials(user.displayName, currentState.userProfile.initials)
                )
            )
        }
    }

    private fun getUserInitials(displayName: String?, fallback: String): String {
        return displayName?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: fallback
    }
}
