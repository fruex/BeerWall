package com.fruex.beerwall.fakes

import com.fruex.beerwall.domain.repository.SupportRepository

class FakeSupportRepository : SupportRepository {
    var shouldFail = false
    var failureMessage = "Błąd wysyłania wiadomości"

    override suspend fun sendMessage(message: String): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(Unit)
    }
}
