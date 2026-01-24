package com.fruex.beerwall.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fruex.beerwall.domain.usecase.SendMessageUseCase
import com.fruex.beerwall.ui.sensor.DeviceOrientationSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel odpowiedzialny za profil użytkownika i support.
 *
 * Zarządza:
 * - Wysyłaniem wiadomości do supportu
 * - Obsługą czujników (tilt)
 */
class ProfileViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    deviceOrientationSensor: DeviceOrientationSensor
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Expose tilt angle from sensor
    val tiltAngle: StateFlow<Float> = deviceOrientationSensor.roll
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0f
        )

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
