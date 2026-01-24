package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.model.SessionStatus
import com.fruex.beerwall.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckSessionUseCaseTest {

    private lateinit var authRepository: FakeAuthRepository
    private lateinit var useCase: CheckSessionUseCase

    @BeforeTest
    fun setUp() {
        authRepository = FakeAuthRepository()
        useCase = CheckSessionUseCase(authRepository)
    }

    @Test
    fun `should return Authenticated status when repository returns Authenticated`() = runTest {
        authRepository.forcedSessionStatus = SessionStatus.Authenticated

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(SessionStatus.Authenticated, result.getOrNull())
    }

    @Test
    fun `should return Expired status when repository returns Expired`() = runTest {
        authRepository.forcedSessionStatus = SessionStatus.Expired

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(SessionStatus.Expired, result.getOrNull())
    }

    @Test
    fun `should return FirstLaunch status when repository returns FirstLaunch`() = runTest {
        authRepository.forcedSessionStatus = SessionStatus.FirstLaunch

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(SessionStatus.FirstLaunch, result.getOrNull())
    }

    @Test
    fun `should return Guest status when repository returns Guest`() = runTest {
        authRepository.forcedSessionStatus = SessionStatus.Guest

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(SessionStatus.Guest, result.getOrNull())
    }

    @Test
    fun `should return failure when repository throws exception`() = runTest {
        authRepository.shouldFail = true

        val result = useCase()

        assertTrue(result.isFailure)
    }
}
