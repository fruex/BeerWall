package com.fruex.beerwall.domain.usecase

import com.fruex.beerwall.domain.model.AuthTokens
import com.fruex.beerwall.domain.model.SessionStatus
import com.fruex.beerwall.domain.repository.AuthRepository
import com.fruex.beerwall.test.BaseTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterUseCaseTest : BaseTest() {

    private val authRepository = FakeAuthRepository()
    private val useCase = RegisterUseCase(authRepository)

    @Test
    fun `invoke should call register on repository with correct params`() = runTest {
        val email = "test@example.com"
        val password = "Password1!"
        authRepository.registerResult = Result.success(Unit)

        val result = useCase(email, password)

        assertTrue(result.isSuccess)
        assertEquals(email, authRepository.capturedEmail)
        assertEquals(password, authRepository.capturedPassword)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val error = Exception("Registration failed")
        authRepository.registerResult = Result.failure(error)

        val result = useCase("email", "pass")

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    private class FakeAuthRepository : AuthRepository {
        var registerResult: Result<Unit> = Result.success(Unit)
        var capturedEmail = ""
        var capturedPassword = ""

        override suspend fun register(email: String, password: String): Result<Unit> {
            capturedEmail = email
            capturedPassword = password
            return registerResult
        }

        override suspend fun checkSessionStatus(): SessionStatus = TODO("Not yet implemented")
        override fun observeSessionState(): Flow<Boolean> = emptyFlow()
        override suspend fun googleSignIn(idToken: String): Result<AuthTokens> = TODO("Not yet implemented")
        override suspend fun emailPasswordSignIn(email: String, password: String): Result<AuthTokens> = TODO("Not yet implemented")
        override suspend fun forgotPassword(email: String): Result<Unit> = TODO("Not yet implemented")
        override suspend fun resetPassword(email: String, resetCode: String, newPassword: String): Result<Unit> = TODO("Not yet implemented")
        override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> = TODO("Not yet implemented")
        override suspend fun refreshToken(): Result<AuthTokens> = TODO("Not yet implemented")
        override suspend fun isUserLoggedIn(): Boolean = false
        override suspend fun logout() {}
    }
}
