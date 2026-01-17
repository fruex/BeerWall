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
import org.fruex.beerwall.domain.repository.AuthRepository
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
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Sprawdza sesję przy starcie aplikacji.
     * UWAGA: Nie wywoływać w init{}, tylko po skonfigurowaniu callbacków w DI.
     */
    fun checkSession() {
        viewModelScope.launch {
            try {
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

    /**
     * Wywołane automatycznie gdy refresh token wygasł.
     */
    fun handleSessionExpired() {
        viewModelScope.launch {
            authRepository.logout()
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
            authRepository.logout()
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
