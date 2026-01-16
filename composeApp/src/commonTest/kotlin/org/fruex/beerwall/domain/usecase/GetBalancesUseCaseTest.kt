package org.fruex.beerwall.domain.usecase

import org.fruex.beerwall.fakes.FakeBalanceRepository
import org.fruex.beerwall.test.BaseTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetBalancesUseCaseTest : BaseTest() {

    private val fakeBalanceRepository = FakeBalanceRepository()
    private val useCase = GetBalancesUseCase(fakeBalanceRepository)

    @Test
    fun `should return balances successfully`() = kotlinx.coroutines.test.runTest {
        // Given
        val expectedBalance = 50.0
        fakeBalanceRepository.fakeBalances[0] = fakeBalanceRepository.fakeBalances[0].copy(balance = expectedBalance)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        val balances = result.getOrNull()
        assertEquals(1, balances?.size)
        assertEquals(expectedBalance, balances?.first()?.balance)
    }

    @Test
    fun `should return error when repository fails`() = kotlinx.coroutines.test.runTest {
        // Given
        fakeBalanceRepository.shouldFail = true
        fakeBalanceRepository.failureMessage = "Network error"

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
