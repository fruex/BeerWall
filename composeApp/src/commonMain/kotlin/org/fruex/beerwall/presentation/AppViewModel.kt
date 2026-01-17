package org.fruex.beerwall.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.auth.GoogleAuthProvider
import org.fruex.beerwall.presentation.viewmodel.*
import org.fruex.beerwall.ui.AppUiState

/**
 * Główny ViewModel aplikacji - teraz deleguje do feature-specific ViewModeli.
 *
 * Ta klasa służy jako fasada utrzymująca kompatybilność wsteczną.
 * Nowy kod powinien używać bezpośrednio feature ViewModeli:
 * - AuthViewModel
 * - BalanceViewModel
 * - CardsViewModel
 * - HistoryViewModel
 * - ProfileViewModel
 *
 * // TODO: REFACTOR - This is a God Object. Break this down by creating separate ViewModels per screen
 * // and passing them via Navigation graph or DI, instead of passing one giant AppViewModel or aggregating state here.
 */
class AppViewModel(
    val authViewModel: AuthViewModel,
    val balanceViewModel: BalanceViewModel,
    val cardsViewModel: CardsViewModel,
    val historyViewModel: HistoryViewModel,
    val profileViewModel: ProfileViewModel
) : ViewModel() {

    // Łączenie stanów z różnych ViewModeli w jeden AppUiState (dla kompatybilności)
    val uiState: StateFlow<AppUiState> = combine(
        authViewModel.uiState,
        balanceViewModel.uiState,
        cardsViewModel.uiState,
        historyViewModel.uiState
    ) { authState, balanceState, cardsState, historyState ->
        AppUiState(
            isLoggedIn = authState.isLoggedIn,
            isCheckingSession = authState.isCheckingSession,
            isRefreshing = balanceState.isRefreshing || cardsState.isRefreshing || historyState.isRefreshing,
            errorMessage = authState.errorMessage
                ?: balanceState.errorMessage
                ?: cardsState.errorMessage
                ?: historyState.errorMessage,
            balances = balanceState.balances,
            cards = cardsState.cards,
            transactionGroups = historyState.transactionGroups,
            userProfile = authState.userProfile,
            paymentMethods = balanceState.paymentMethods
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppUiState()
    )

    init {
        // Obserwuj zmiany stanu logowania i odświeżaj dane
        viewModelScope.launch {
            authViewModel.uiState
                .map { it.isLoggedIn }
                .distinctUntilChanged()
                .collect { isLoggedIn ->
                    if (isLoggedIn) {
                        refreshAllData()
                    }
                }
        }
    }

    // Delegowanie metod autentykacji
    fun handleSessionExpired() = authViewModel.handleSessionExpired()
    fun checkSession() = authViewModel.checkSession()
    fun onLoginSuccess(tokens: AuthTokens) {
        refreshAllData()
    }
    fun handleGoogleSignIn(googleAuthProvider: GoogleAuthProvider) =
        authViewModel.handleGoogleSignIn(googleAuthProvider)
    fun handleEmailPasswordSignIn(email: String, password: String) =
        authViewModel.handleEmailPasswordSignIn(email, password)
    fun handleRegister(email: String, password: String) = authViewModel.handleRegister(email, password)
    fun handleForgotPassword(email: String) = authViewModel.handleForgotPassword(email)
    fun handleResetPassword(email: String, resetCode: String, newPassword: String) =
        authViewModel.handleResetPassword(email, resetCode, newPassword)
    fun handleLogout(googleAuthProvider: GoogleAuthProvider) = authViewModel.handleLogout(googleAuthProvider)

    // Delegowanie metod balansu
    fun refreshBalance() = balanceViewModel.refreshBalance()
    fun onAddFunds(premisesId: Int, paymentMethodId: Int, balance: Double) =
        balanceViewModel.onAddFunds(premisesId, paymentMethodId, balance)

    // Delegowanie metod kart
    fun refreshCards() = cardsViewModel.refreshCards()
    fun onToggleCardStatus(cardId: String) = cardsViewModel.onToggleCardStatus(cardId)
    fun onDeleteCard(cardId: String) = cardsViewModel.onDeleteCard(cardId)
    fun onSaveCard(name: String, cardId: String) = cardsViewModel.onSaveCard(name, cardId)

    // Delegowanie metod historii
    fun refreshHistory() = historyViewModel.refreshHistory()

    // Delegowanie metod profilu
    fun onSendMessage(message: String) = profileViewModel.onSendMessage(message)

    fun onClearError() {
        authViewModel.onClearError()
        balanceViewModel.onClearError()
        cardsViewModel.onClearError()
        historyViewModel.onClearError()
        profileViewModel.onClearMessages()
    }

    fun refreshAllData() {
        refreshBalance()
        refreshCards()
        refreshHistory()
    }
}
