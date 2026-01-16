package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.fakes.FakeCardRepository
import org.fruex.beerwall.test.BaseTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToggleCardStatusUseCaseTest : BaseTest() {

    private val fakeCardRepository = FakeCardRepository()
    private val useCase = ToggleCardStatusUseCase(fakeCardRepository)

    @Test
    fun `should toggle card status to active`() = kotlinx.coroutines.test.runTest {
        // Given
        val cardId = "card-1"
        // Upewnij się, że karta istnieje w fake'u
        assertTrue(fakeCardRepository.fakeCards.any { it.id == cardId })

        // When
        val result = useCase(cardId, true)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
        assertTrue(fakeCardRepository.fakeCards.first { it.id == cardId }.isActive)
    }

    @Test
    fun `should toggle card status to inactive`() = kotlinx.coroutines.test.runTest {
        // Given
        val cardId = "card-1"

        // When
        val result = useCase(cardId, false)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == false)
        assertTrue(fakeCardRepository.fakeCards.first { it.id == cardId }.isActive == false)
    }

    @Test
    fun `should return error when repository fails`() = kotlinx.coroutines.test.runTest {
        // Given
        fakeCardRepository.shouldFail = true
        fakeCardRepository.failureMessage = "Update failed"

        // When
        val result = useCase("card-1", true)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Update failed", result.exceptionOrNull()?.message)
    }
}
