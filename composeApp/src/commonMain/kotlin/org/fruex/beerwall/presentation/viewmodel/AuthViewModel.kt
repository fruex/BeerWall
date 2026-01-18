package org.fruex.beerwall.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fruex.beerwall.auth.AuthTokens
import org.fruex.beerwall.auth.GoogleAuthProvider
import org.fruex.beerwall.domain.usecase.*
import org.fruex.beerwall.ui.models.UserProfile

/**
 * ViewModel odpowiedzialny za autentykację użytkownika.
 *
 * Zarządza:
 * - Logowaniem (Google, email/hasło)
 * - Rejestracją
 * - Wylogowaniem
 * - Resetowaniem hasła
 * - Sprawdzaniem sesji
 */
class AuthViewModel(
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val emailPasswordSignInUseCase: EmailPasswordSignInUseCase,
    private val registerUseCase: RegisterUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val observeSessionStateUseCase: ObserveSessionStateUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Obserwuj globalny stan sesji
        viewModelScope.launch {
            observeSessionStateUseCase().collect { isLoggedIn ->
                if (!isLoggedIn && _uiState.value.isLoggedIn) {
                    handleSessionExpired()
                } else if (isLoggedIn && !_uiState.value.isLoggedIn) {
                    _uiState.update { it.copy(isLoggedIn = true) }
                }
            }
        }
    }

    /**
     * Sprawdza sesję przy starcie aplikacji.
     * UWAGA: Nie wywoływać w init{}, tylko po skonfigurowaniu callbacków w DI.
     */
    fun checkSession() {
        viewModelScope.launch {
            try {
                checkSessionUseCase()
                    .onSuccess { isLoggedIn ->
                        // Ustawienie stanu sesji odbywa się automatycznie przez repozytorium/sessionManager
                        // tutaj tylko aktualizujemy UI jeśli to konieczne, ale observeSessionStateUseCase
                        // powinno załatwić sprawę. Jednak checkSessionUseCase może wymuszać odświeżenie.
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

    /**
     * Wywołane automatycznie gdy refresh token wygasł.
     */
    private fun handleSessionExpired() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update {
                it.copy(
                    isLoggedIn = false,
                    errorMessage = "Sesja wygasła. Zaloguj się ponownie.",
                    userProfile = UserProfile("", "", "?")
                )
            }
        }
    }

    fun handleGoogleSignIn(googleAuthProvider: GoogleAuthProvider) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                googleSignInUseCase(googleAuthProvider)
                    .onSuccess { tokens ->
                        onLoginSuccess(tokens)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Błąd logowania Google") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd logowania Google") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun handleEmailPasswordSignIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                emailPasswordSignInUseCase(email, password)
                    .onSuccess { tokens ->
                        onLoginSuccess(tokens)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Błąd logowania") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd logowania") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun handleRegister(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                registerUseCase(email, password)
                    .onSuccess {
                        // Po udanej rejestracji logujemy użytkownika
                        handleEmailPasswordSignIn(email, password)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Błąd rejestracji") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd rejestracji") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun handleForgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                forgotPasswordUseCase(email)
                    .onSuccess {
                        _uiState.update { it.copy(errorMessage = "Wysłano link do resetowania hasła na podany adres email.") }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Błąd resetowania hasła") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd resetowania hasła") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun handleResetPassword(email: String, resetCode: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                resetPasswordUseCase(email, resetCode, newPassword)
                    .onSuccess {
                        _uiState.update { it.copy(errorMessage = "Hasło zostało pomyślnie zresetowane.") }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Błąd resetowania hasła") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd resetowania hasła") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun handleLogout(googleAuthProvider: GoogleAuthProvider) {
        viewModelScope.launch {
            googleAuthProvider.signOut()
            logoutUseCase()
            _uiState.update {
                it.copy(
                    isLoggedIn = false,
                    userProfile = UserProfile("", "", "?")
                )
            }
        }
    }

    fun onClearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun onLoginSuccess(tokens: AuthTokens) {
        updateUserProfile(tokens)
        // SessionManager jest aktualizowany przez repozytorium (via AuthApiClient callback lub jawnie),
        // ale w przypadku sukcesu logowania musimy poinformować system.
        // Jednak tutaj mamy lukę: UseCase zwraca tokeny, ale czy ustawia stan zalogowania w SessionManager?
        // AuthRepositoryImpl tylko zapisuje tokeny.
        // W poprzedniej wersji AuthViewModel ustawiał sessionManager.setLoggedIn(true).
        // Teraz nie mamy dostępu do SessionManager.
        // AuthRepository powinno zarządzać stanem sesji.
        // Zrobimy to przez checkSession() lub założenie, że sukces logowania = zalogowany.
        // TODO: Refactor AuthRepository to handle session state internally fully.
        // Na razie zakładamy, że observeSessionStateUseCase wyłapie zmianę,
        // jeśli AuthRepository lub SessionManager zareaguje na zapisanie tokenów?
        // TokenManager nie ma callbacka.
        // Wymuśmy sprawdzenie sesji lub dodajmy metodę do repozytorium "setLoggedIn".
        // Ale "setLoggedIn" to detale implementacyjne SessionManagera.
        // Rozwiązanie tymczasowe: checkSessionUseCase po sukcesie?
        // Lub lepiej: AuthRepositoryImpl po sukcesie logowania powinno ustawić flagę w SessionManager.
        // (Zrobię to w AuthRepositoryImpl w kolejnym kroku, lub zaktualizuję teraz ViewModel by po prostu odświeżył stan).
        checkSession()
        _uiState.update { it.copy(isLoggedIn = true, isCheckingSession = false) }
    }

    private fun updateUserProfile(tokens: AuthTokens) {
        val displayName = if (tokens.firstName != null || tokens.lastName != null)
            "${tokens.firstName ?: ""} ${tokens.lastName ?: ""}".trim()
        else null

        _uiState.update { currentState ->
            currentState.copy(
                userProfile = currentState.userProfile.copy(
                    name = displayName ?: currentState.userProfile.name,
                    initials = getUserInitials(displayName, currentState.userProfile.initials)
                )
            )
        }
    }

    private fun getUserInitials(displayName: String?, fallback: String): String {
        return displayName?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: fallback
    }
}

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val isCheckingSession: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userProfile: UserProfile = UserProfile("", "", "?")
)
