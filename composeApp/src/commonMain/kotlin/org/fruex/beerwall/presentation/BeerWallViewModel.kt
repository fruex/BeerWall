package org.fruex.beerwall.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.domain.usecase.*
import org.fruex.beerwall.presentation.mapper.groupByDate
import org.fruex.beerwall.presentation.mapper.toUi
import org.fruex.beerwall.ui.BeerWallUiState

/**
 * ViewModel zarządzający stanem aplikacji BeerWall
 *
 * Odpowiedzialny za:
 * - Zarządzanie stanem UI (balanse, karty, transakcje, profil użytkownika)
 * - Komunikację z warstwą domenową poprzez Use Cases
 * - Obsługę akcji użytkownika (logowanie, dodawanie środków, zarządzanie kartami)
 * - Obsługę błędów i stanów ładowania
 *
 * @param getBalancesUseCase Use case do pobierania sald
 * @param topUpBalanceUseCase Use case do doładowania konta
 * @param getTransactionsUseCase Use case do pobierania historii transakcji
 * @param toggleCardStatusUseCase Use case do przełączania statusu karty
 */
class BeerWallViewModel(
    private val getBalancesUseCase: GetBalancesUseCase,
    private val topUpBalanceUseCase: TopUpBalanceUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val toggleCardStatusUseCase: ToggleCardStatusUseCase,
    private val assignCardUseCase: AssignCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val getPaymentOperatorsUseCase: GetPaymentOperatorsUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val emailPasswordSignInUseCase: EmailPasswordSignInUseCase,
    private val registerUseCase: RegisterUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val authRepository: org.fruex.beerwall.domain.repository.AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BeerWallUiState())
    val uiState: StateFlow<BeerWallUiState> = _uiState.asStateFlow()

    /**
     * Wywołane automatycznie gdy refresh token wygasł
     * Czyści stan użytkownika i przekierowuje do ekranu logowania
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

    private fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isRefreshing = isLoading) }
    }

    private fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    private fun setMessage(message: String) {
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

    /**
     * Sprawdza sesję przy starcie aplikacji
     * Używa zapisanego tokenu .NET zamiast logowania Google
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
            } catch (_: Exception) {
                _uiState.update { it.copy(isCheckingSession = false) }
            }
        }
    }

    fun onLoginSuccess(tokens: AuthTokens) {
        updateUserProfile(tokens)
        _uiState.update { it.copy(isLoggedIn = true, isCheckingSession = false) }
        refreshAllData()
    }

    fun handleGoogleSignIn(googleAuthProvider: org.fruex.beerwall.auth.GoogleAuthProvider) {
        viewModelScope.launch {
            setLoading(true)
            try {
                googleSignInUseCase(googleAuthProvider)
                    .onSuccess { tokens ->
                        onLoginSuccess(tokens)
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

    fun handleEmailPasswordSignIn(email: String, password: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                emailPasswordSignInUseCase(email, password)
                    .onSuccess { tokens ->
                        onLoginSuccess(tokens)
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

    fun handleRegister(email: String, password: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                registerUseCase(email, password)
                    .onSuccess {
                        // Po udanej rejestracji logujemy użytkownika
                        handleEmailPasswordSignIn(email, password)
                    }
                    .onFailure { error ->
                        setError(error.message ?: "Błąd rejestracji")
                    }
            } catch (e: Exception) {
                setError(e.message ?: "Błąd rejestracji")
            } finally {
                setLoading(false)
            }
        }
    }

    fun handleForgotPassword(email: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                forgotPasswordUseCase(email)
                    .onSuccess {
                        setMessage("Wysłano link do resetowania hasła na podany adres email.")
                    }
                    .onFailure { error ->
                        setError(error.message ?: "Błąd resetowania hasła")
                    }
            } catch (e: Exception) {
                setError(e.message ?: "Błąd resetowania hasła")
            } finally {
                setLoading(false)
            }
        }
    }

    fun handleResetPassword(email: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                resetPasswordUseCase(email)
                    .onSuccess {
                        setMessage("Hasło zostało pomyślnie zresetowane.")
                    }
                    .onFailure { error ->
                        setError(error.message ?: "Błąd resetowania hasła")
                    }
            } catch (e: Exception) {
                setError(e.message ?: "Błąd resetowania hasła")
            } finally {
                setLoading(false)
            }
        }
    }

    fun onClearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun handleLogout(googleAuthProvider: org.fruex.beerwall.auth.GoogleAuthProvider) {
        viewModelScope.launch {
            googleAuthProvider.signOut()
            authRepository.logout()
            _uiState.update { it.copy(isLoggedIn = false) }
        }
    }

    fun refreshAllData() {
        refreshBalance()
        refreshCards()
        refreshHistory()
        loadPaymentMethods()
    }

    private fun loadPaymentMethods() {
        viewModelScope.launch {
            getPaymentOperatorsUseCase().onSuccess { operators ->
                val methods = operators.flatMap { it.paymentMethods }
                _uiState.update { it.copy(paymentMethods = methods) }
            }
        }
    }

    fun onAddFunds(premisesId: Int, paymentMethodId: Int, balance: Double) {
        viewModelScope.launch {
            topUpBalanceUseCase(premisesId, paymentMethodId, balance)
                .onSuccess {
                    // Odpowiedź przyjdzie przez webhook, nie czekamy na response
                }
                .onFailure { setError("Nie udało się doładować konta: ${it.message}") }
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

    fun refreshCards() {
        launchWithLoading {
            getCardsUseCase().onSuccess { cards ->
                _uiState.update { it.copy(cards = cards.toUi()) }
            }
        }
    }

    fun onDeleteCard(cardId: String) {
        viewModelScope.launch {
            setLoading(true)
            deleteCardUseCase(cardId)
                .onSuccess {
                    refreshCards()
                }
                .onFailure {
                    setError("Nie udało się usunąć karty: ${it.message}")
                }
            setLoading(false)
        }
    }

    fun onSaveCard(name: String, cardId: String) {
        viewModelScope.launch {
            setLoading(true)
            assignCardUseCase(cardId, name)
                .onSuccess {
                    refreshCards()
                }
                .onFailure {
                    setError("Nie udało się zapisać karty: ${it.message}")
                }
            setLoading(false)
        }
    }

    private fun updateUserProfile(tokens: AuthTokens) {
        val displayName = if (tokens.firstName != null || tokens.lastName != null) 
            "${tokens.firstName ?: ""} ${tokens.lastName ?: ""}".trim() 
            else null
            
        _uiState.update { currentState ->
            currentState.copy(
                userProfile = currentState.userProfile.copy(
                    name = displayName ?: currentState.userProfile.name,
                    // Email nie jest dostępny w AuthTokens, więc zostawiamy stary lub pusty
                    // Jeśli potrzebujemy emaila, musielibyśmy go też wyciągnąć z tokenu (jeśli tam jest)
                    initials = getUserInitials(displayName, currentState.userProfile.initials)
                )
            )
        }
    }

    private fun getUserInitials(displayName: String?, fallback: String): String {
        return displayName?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: fallback
    }
}
