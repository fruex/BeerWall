package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.fakes.FakeCardRepository
import com.fruex.beerwall.test.BaseTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateCardUseCaseTest : BaseTest() {

    private val fakeCardRepository = FakeCardRepository()
    private val useCase = UpdateCardUseCase(fakeCardRepository)

    @Test
    fun `should update card`() = kotlinx.coroutines.test.runTest {
        // Given
        val cardId = "card-1"
        val newName = "New Name"

        // When
        val result = useCase(cardId, newName, true)

        // Then
        assertTrue(result.isSuccess)
        val updatedCard = fakeCardRepository.fakeCards.first { it.cardGuid == cardId }
        assertTrue(updatedCard.isActive)
        assertEquals(newName, updatedCard.description)
    }

    @Test
    fun `should return error when repository fails`() = kotlinx.coroutines.test.runTest {
        // Given
        fakeCardRepository.shouldFail = true
        fakeCardRepository.failureMessage = "Update failed"

        // When
        val result = useCase("card-1", "Name", true)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Update failed", result.exceptionOrNull()?.message)
    }
}
