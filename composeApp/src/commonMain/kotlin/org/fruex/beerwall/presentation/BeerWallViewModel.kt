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
 * ViewModel zarządzający stanem aplikacji BeerWall.
 *
 * Odpowiedzialny za:
 * - Zarządzanie stanem UI (balanse, karty, transakcje, profil użytkownika).
 * - Komunikację z warstwą domenową poprzez Use Cases.
 * - Obsługę akcji użytkownika (logowanie, dodawanie środków, zarządzanie kartami).
 * - Obsługę błędów i stanów ładowania.
 *
 * @property refreshAllDataUseCase Use case do odświeżania wszystkich danych jednocześnie.
 * @property getBalancesUseCase Use case do pobierania sald.
 * @property topUpBalanceUseCase Use case do doładowania konta.
 * @property getTransactionsUseCase Use case do pobierania historii transakcji.
 * @property toggleCardStatusUseCase Use case do przełączania statusu karty.
 * @property getPaymentOperatorsUseCase Use case do pobierania operatorów płatności.
 * @property googleSignInUseCase Use case do logowania przez Google.
 * @property emailPasswordSignInUseCase Use case do logowania emailem i hasłem.
 * @property checkSessionUseCase Use case do sprawdzania ważności sesji.
 * @property authRepository Repozytorium autoryzacji (używane bezpośrednio do wylogowania - TODO: przenieść do UseCase).
 */
class BeerWallViewModel(
    private val refreshAllDataUseCase: RefreshAllDataUseCase,
    private val getBalancesUseCase: GetBalancesUseCase,
    private val topUpBalanceUseCase: TopUpBalanceUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val toggleCardStatusUseCase: ToggleCardStatusUseCase,
    private val getPaymentOperatorsUseCase: GetPaymentOperatorsUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val emailPasswordSignInUseCase: EmailPasswordSignInUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    // TODO: Bezpośrednie użycie repozytorium w ViewModelu łamie zasadę czystej architektury (powinien być UseCase 'LogoutUseCase').
    private val authRepository: org.fruex.beerwall.domain.repository.AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BeerWallUiState())
    val uiState: StateFlow<BeerWallUiState> = _uiState.asStateFlow()

    /**
     * Wywołane automatycznie gdy refresh token wygasł.
     * Czyści stan użytkownika i przekierowuje do ekranu logowania.
     */
    fun handleSessionExpired() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update {
                it.copy(
                    isLoggedIn = false,
                    errorMessage = "Sesja wygasła. Zaloguj się ponownie.",
                    userProfile = it.userProfile.copy(
                        name = "",
                        email = "",
                        initials = "?"
                    ),
                    balances = emptyList(),
                    cards = emptyList(),
                    transactionGroups = emptyList()
                )
            }
        }
    }

    /**
     * Ustawia flagę ładowania (isRefreshing) w stanie UI.
     * @param isLoading true jeśli trwa ładowanie, false w przeciwnym razie.
     */
    private fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isRefreshing = isLoading) }
    }

    /**
     * Ustawia komunikat błędu w stanie UI.
     * @param message Treść błędu.
     */
    private fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    /**
     * Wykonuje blok kodu z automatyczną obsługą stanu ładowania i błędów.
     * @param block Zawieszająca funkcja (suspend) do wykonania.
     */
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

    /**
     * Sprawdza sesję przy starcie aplikacji.
     * Weryfikuje czy użytkownik ma zapisane ważne tokeny.
     */
    fun checkSession() {
        viewModelScope.launch {
            try {
                // Sprawdź czy użytkownik ma zapisany token w TokenManager
                checkSessionUseCase()
                    .onSuccess { isLoggedIn ->
                        _uiState.update {
                            it.copy(
                                isLoggedIn = isLoggedIn,
                                isCheckingSession = false
                            )
                        }
                    }
                    .onFailure {
                        _uiState.update { it.copy(isCheckingSession = false) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isCheckingSession = false) }
            }
        }
    }

    /**
     * Aktualizuje stan po udanym logowaniu.
     * @param user Obiekt użytkownika Google.
     */
    fun onLoginSuccess(user: GoogleUser) {
        updateUserProfile(user)
        _uiState.update { it.copy(isLoggedIn = true, isCheckingSession = false) }
        refreshAllData()
    }

    /**
     * Obsługuje proces logowania przez Google.
     * @param googleAuthProvider Dostawca autoryzacji Google (platform-specific).
     */
    fun handleGoogleSignIn(googleAuthProvider: org.fruex.beerwall.auth.GoogleAuthProvider) {
        viewModelScope.launch {
            setLoading(true)
            try {
                googleSignInUseCase(googleAuthProvider)
                    .onSuccess { user ->
                        onLoginSuccess(user)
                    }
                    .onFailure { error ->
                        setError(error.message ?: "Błąd logowania Google")
                    }
            } catch (e: Exception) {
                setError(e.message ?: "Błąd logowania Google")
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Obsługuje proces logowania emailem i hasłem.
     * @param email Adres email.
     * @param password Hasło.
     */
    fun handleEmailPasswordSignIn(email: String, password: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                emailPasswordSignInUseCase(email, password)
                    .onSuccess {
                        // Tokeny zostały zapisane w TokenManager przez repository
                        _uiState.update { it.copy(isLoggedIn = true, isCheckingSession = false) }
                        refreshAllData()
                    }
                    .onFailure { error ->
                        setError(error.message ?: "Błąd logowania")
                    }
            } catch (e: Exception) {
                setError(e.message ?: "Błąd logowania")
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Ustawia tryb gościa (zalogowany bez danych).
     * // TODO: Zweryfikować czy tryb gościa jest nadal potrzebny i jak powinien działać.
     */
    fun setGuestSession() {
        _uiState.update { it.copy(isLoggedIn = true) }
        refreshAllData()
    }

    /**
     * Czyści aktualny komunikat błędu.
     */
    fun onClearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Wylogowuje użytkownika.
     */
    fun onLogout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(isLoggedIn = false) }
        }
    }

    /**
     * Odświeża wszystkie dane użytkownika (balanse, karty, historia).
     */
    fun refreshAllData() {
        viewModelScope.launch {
            setLoading(true)

            // TODO: Obsługa błędów wewnątrz refreshAllDataUseCase może być ulepszona, aby zwracać częściowe wyniki lub konkretne błędy.
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

    /**
     * Ładuje dostępne metody płatności.
     */
    private fun loadPaymentMethods() {
        viewModelScope.launch {
            getPaymentOperatorsUseCase().onSuccess { operators ->
                val methods = operators.flatMap { it.paymentMethods }
                _uiState.update { it.copy(paymentMethods = methods) }
            }
        }
    }

    /**
     * Inicjuje doładowanie konta.
     * @param venueId ID lokalu.
     * @param paymentMethodId ID metody płatności.
     * @param balance Kwota doładowania.
     */
    fun onAddFunds(venueId: Int, paymentMethodId: Int, balance: Double) {
        viewModelScope.launch {
            topUpBalanceUseCase(venueId, paymentMethodId, balance)
                .onSuccess {
                    // Odpowiedź przyjdzie przez webhook, nie czekamy na response
                    // TODO: Dodać obsługę przekierowania do bramki płatności jeśli usecase zwraca URL.
                }
                .onFailure { setError("Nie udało się doładować konta: ${it.message}") }
        }
    }

    /**
     * Aktualizuje saldo lokalu w stanie UI (lokalnie).
     */
    private fun updateVenueBalance(venueName: String, newBalance: Double) {
        _uiState.update { currentState ->
            val updatedBalances = currentState.balances.map {
                if (it.venueName == venueName) it.copy(balance = newBalance) else it
            }
            currentState.copy(balances = updatedBalances)
        }
    }

    /**
     * Przełącza status aktywności karty.
     * @param cardId ID karty.
     */
    fun onToggleCardStatus(cardId: String) {
        val card = _uiState.value.cards.find { it.id == cardId } ?: return
        viewModelScope.launch {
            toggleCardStatusUseCase(cardId, !card.isActive)
                .onSuccess { isActive -> updateCardStatus(cardId, isActive) }
                .onFailure { setError("Nie udało się zmienić statusu karty: ${it.message}") }
        }
    }

    /**
     * Aktualizuje status karty w stanie UI.
     */
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

    /**
     * Odświeża historię transakcji.
     */
    fun refreshHistory() {
        launchWithLoading {
            getTransactionsUseCase().onSuccess { transactions ->
                _uiState.update { it.copy(transactionGroups = transactions.groupByDate()) }
            }
        }
    }

    /**
     * Odświeża salda.
     */
    fun refreshBalance() {
        launchWithLoading {
            getBalancesUseCase().onSuccess { balances ->
                _uiState.update { it.copy(balances = balances.toUi()) }
            }
        }
    }

    /**
     * Usuwa kartę (symulacja lokalna).
     * // TODO: Dodać UseCase do usuwania karty w API.
     */
    fun onDeleteCard(cardId: String) {
        updateCards { cards -> cards.filter { it.id != cardId || !it.isPhysical } }
    }

    /**
     * Zapisuje nową kartę (symulacja lokalna).
     * // TODO: Dodać UseCase do dodawania karty w API.
     */
    fun onSaveCard(name: String, cardId: String) {
        val newCard = UserCard(
            id = cardId,
            name = name,
            isActive = true,
            isPhysical = true
        )
        updateCards { cards -> cards + newCard }
    }

    /**
     * Pomocnicza funkcja do aktualizacji listy kart.
     */
    private fun updateCards(transform: (List<UserCard>) -> List<UserCard>) {
        _uiState.update { currentState ->
            val updatedCards = transform(currentState.cards)
            currentState.copy(
                cards = updatedCards
            )
        }
    }

    /**
     * Aktualizuje dane profilowe użytkownika w stanie UI.
     */
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

    /**
     * Generuje inicjały użytkownika na podstawie nazwy wyświetlanej.
     */
    private fun getUserInitials(displayName: String?, fallback: String): String {
        return displayName?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: fallback
    }
}
