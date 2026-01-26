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
    fun `should update card description and status`() = kotlinx.coroutines.test.runTest {
        // Given
        val cardId = "card-1"
        val newDescription = "New Name"
        val newStatus = false

        // When
        val result = useCase(cardId, newDescription, newStatus)

        // Then
        assertTrue(result.isSuccess)
        val updatedCard = fakeCardRepository.fakeCards.first { it.cardGuid == cardId }
        assertEquals(newDescription, updatedCard.description)
        assertEquals(newStatus, updatedCard.isActive)
    }

    @Test
    fun `should return error when repository fails`() = kotlinx.coroutines.test.runTest {
        // Given
        fakeCardRepository.shouldFail = true
        fakeCardRepository.failureMessage = "Update failed"

        // When
        val result = useCase("card-1", "Desc", true)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Update failed", result.exceptionOrNull()?.message)
    }
}
