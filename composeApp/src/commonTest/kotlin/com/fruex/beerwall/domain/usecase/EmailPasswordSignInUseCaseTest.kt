package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.fakes.FakeAuthRepository
import com.fruex.beerwall.test.BaseTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EmailPasswordSignInUseCaseTest : BaseTest() {

    private val fakeAuthRepository = FakeAuthRepository()
    private val useCase = EmailPasswordSignInUseCase(fakeAuthRepository)

    @Test
    fun `should sign in successfully`() = kotlinx.coroutines.test.runTest {
        // Given
        val email = "test@example.com"
        val password = "password"

        // When
        val result = useCase(email, password)

        // Then
        assertTrue(result.isSuccess)
        val tokens = result.getOrNull()
        assertEquals("fake-token", tokens?.token)
        assertTrue(fakeAuthRepository.isUserLoggedIn())
    }

    @Test
    fun `should return error when credentials are invalid`() = kotlinx.coroutines.test.runTest {
        // Given
        fakeAuthRepository.shouldFail = true
        fakeAuthRepository.failureMessage = "Invalid credentials"

        // When
        val result = useCase("wrong@email.com", "wrong")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }
}
