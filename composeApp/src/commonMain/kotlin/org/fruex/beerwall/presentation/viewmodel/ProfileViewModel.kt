package org.fruex.beerwall.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fruex.beerwall.domain.usecase.SendMessageUseCase

/**
 * ViewModel odpowiedzialny za profil użytkownika i support.
 *
 * Zarządza:
 * - Wysyłaniem wiadomości do supportu
 */
class ProfileViewModel(
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onSendMessage(message: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                sendMessageUseCase(message)
                    .onSuccess {
                        _uiState.update { it.copy(successMessage = "Wiadomość została wysłana. Dziękujemy!") }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message ?: "Błąd wysyłania wiadomości") }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Błąd wysyłania wiadomości") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onClearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
