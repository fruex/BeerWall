package com.fruex.beerwall.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.domain.model.SessionStatus
import com.fruex.beerwall.domain.auth.GoogleAuthProvider
import com.fruex.beerwall.domain.usecase.*
import com.fruex.beerwall.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val observeSessionStateUseCase: ObserveSessionStateUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val markFirstLaunchSeenUseCase: MarkFirstLaunchSeenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var isManualLogout = false

    init {
        // Obserwuj globalny stan sesji
        viewModelScope.launch {
            observeSessionStateUseCase().collect { isLoggedIn ->
                if (!isLoggedIn && _uiState.value.isLoggedIn) {
                    if (!isManualLogout) {
                        handleSessionExpired()
                    }
                    isManualLogout = false // Reset flag
                } else if (isLoggedIn && !_uiState.value.isLoggedIn) {
                    _uiState.update { it.copy(isLoggedIn = true) }
                    // Po zalogowaniu (lub przywróceniu sesji) pobierz dane użytkownika
                    loadUserProfile()
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
                    .onSuccess { status ->
                        when (status) {
                            SessionStatus.Authenticated -> {
                                markFirstLaunchSeenUseCase()
                                _uiState.update {
                                    it.copy(
                                        isLoggedIn = true,
                                        isCheckingSession = false
                                    )
                                }
                                loadUserProfile()
                            }
                            SessionStatus.Expired -> {
                                _uiState.update {
                                    it.copy(
                                        isLoggedIn = false,
                                        isCheckingSession = false,
                                        errorMessage = "Sesja wygasła. Zaloguj się ponownie."
                                    )
                                }
                            }
                            SessionStatus.FirstLaunch -> {
                                markFirstLaunchSeenUseCase()
                                _uiState.update {
                                    it.copy(
                                        isLoggedIn = false,
                                        isCheckingSession = false,
                                        errorMessage = "Witaj w aplikacji!"
                                    )
                                }
                            }
                            SessionStatus.Guest -> {
                                _uiState.update {
                                    it.copy(
                                        isLoggedIn = false,
                                        isCheckingSession = false
                                    )
                                }
                            }
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
                    userProfile = UserProfile("")
                )
            }
        }
    }

    fun handleGoogleSignIn(googleAuthProvider: GoogleAuthProvider) {
        performAuthAction(
            action = { googleSignInUseCase(googleAuthProvider) },
            errorMessage = "Błąd logowania Google"
        )
    }

    fun handleEmailPasswordSignIn(email: String, password: String) {
        performAuthAction(
            action = { emailPasswordSignInUseCase(email, password) },
            errorMessage = "Błąd logowania"
        )
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
                        _uiState.update { it.copy(isLoading = false) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd rejestracji", isLoading = false) }
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

    fun handleChangePassword(oldPassword: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                changePasswordUseCase(oldPassword, newPassword)
                    .onSuccess {
                        onSuccess()
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Błąd zmiany hasła") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd zmiany hasła") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun handleLogout(googleAuthProvider: GoogleAuthProvider) {
        viewModelScope.launch {
            isManualLogout = true
            googleAuthProvider.signOut()
            logoutUseCase()
            _uiState.update {
                it.copy(
                    isLoggedIn = false,
                    userProfile = UserProfile("")
                )
            }
        }
    }

    fun onClearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun performAuthAction(
        action: suspend () -> Result<AuthTokens>,
        errorMessage: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                action()
                    .onSuccess { tokens ->
                        onLoginSuccess(tokens)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: errorMessage) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: errorMessage) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun onLoginSuccess(tokens: AuthTokens) {
        // Po zalogowaniu od razu pobieramy profil z TokenManagera, który ma już zaktualizowane tokeny
        loadUserProfile()
        checkSession()
        _uiState.update { it.copy(isLoggedIn = true, isCheckingSession = false) }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val userProfile = getUserProfileUseCase()
            if (userProfile != null) {
                _uiState.update { currentState ->
                    currentState.copy(userProfile = userProfile)
                }
            }
        }
    }
}

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val isCheckingSession: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userProfile: UserProfile = UserProfile("")
)
