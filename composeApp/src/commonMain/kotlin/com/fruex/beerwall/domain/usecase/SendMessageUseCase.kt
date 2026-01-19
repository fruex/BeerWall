package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.repository.SupportRepository

class SendMessageUseCase(
    private val repository: SupportRepository
) {
    suspend operator fun invoke(message: String): Result<Unit> =
        repository.sendMessage(message)
}
