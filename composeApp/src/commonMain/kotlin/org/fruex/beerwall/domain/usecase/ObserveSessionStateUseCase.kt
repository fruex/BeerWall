package org.fruex.beerwall.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.fruex.beerwall.domain.repository.AuthRepository

class ObserveSessionStateUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return authRepository.observeSessionState()
    }
}
