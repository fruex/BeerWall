package org.fruex.beerwall.data.remote.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.fruex.beerwall.LogSeverity
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BaseApiClient
import org.fruex.beerwall.log
import org.fruex.beerwall.remote.dto.auth.*

/**
 * API client for authentication operations.
 * Handles user sign-in, registration, password management, and token refresh.
 */
class AuthApiClient(tokenManager: TokenManager) : BaseApiClient(tokenManager) {

    /**
     * Authenticates user with Google ID token.
     */
    suspend fun googleSignIn(idToken: String): Result<GoogleSignInResponse> = try {
        platform.log("📤 Google SignIn Request", this, LogSeverity.INFO)

        val httpResponse: HttpResponse = client.post("$baseUrl/mobile/auth/googleSignIn") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $idToken")
        }

        when (httpResponse.status) {
            HttpStatusCode.OK -> {
                val response: GoogleSignInEnvelope = httpResponse.body()
                if (response.data != null) {
                    platform.log("✅ Google SignIn Success", this, LogSeverity.INFO)
                    Result.success(response.data)
                } else {
                    val errorMsg = response.error?.message ?: "Unknown error"
                    platform.log("❌ Google SignIn Error: $errorMsg", this, LogSeverity.ERROR)
                    Result.failure(Exception(errorMsg))
                }
            }
            HttpStatusCode.Unauthorized -> {
                platform.log("❌ 401 Unauthorized from Backend", this, LogSeverity.ERROR)
                Result.failure(Exception("Backend rejected token"))
            }
            else -> {
                val bodyText = httpResponse.bodyAsText()
                platform.log("❌ HTTP ${httpResponse.status.value}: $bodyText", this, LogSeverity.ERROR)
                Result.failure(Exception("HTTP ${httpResponse.status.value}"))
            }
        }
    } catch (e: Exception) {
        platform.log("❌ Google SignIn Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }

    /**
     * Authenticates user with email and password.
     */
    suspend fun emailPasswordSignIn(email: String, password: String): Result<EmailPasswordSignInResponse> = try {
        platform.log("📤 Email SignIn Request", this, LogSeverity.INFO)
        val response = client.post("$baseUrl/mobile/auth/signIn") {
            contentType(ContentType.Application.Json)
            setBody(EmailPasswordSignInRequest(email, password))
        }

        if (response.status == HttpStatusCode.OK) {
            val envelope: EmailPasswordSignInEnvelope = response.body()
            if (envelope.data != null) {
                platform.log("✅ Email SignIn Success", this, LogSeverity.INFO)
                Result.success(envelope.data)
            } else {
                Result.failure(Exception(envelope.error?.message ?: "Unknown error"))
            }
        } else {
            Result.failure(Exception("Login failed: ${response.status}"))
        }
    } catch (e: Exception) {
        platform.log("❌ Email SignIn Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }

    /**
     * Registers new user account.
     */
    suspend fun register(email: String, password: String): Result<Unit> = try {
        platform.log("📤 Register Request", this, LogSeverity.INFO)
        val response = client.post("$baseUrl/mobile/auth/signUp") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(email, password))
        }

        if (response.status == HttpStatusCode.OK) {
            platform.log("✅ Registration Success", this, LogSeverity.INFO)
            Result.success(Unit)
        } else {
            val bodyText = response.bodyAsText()
            platform.log("❌ Registration Error: ${response.status} - $bodyText", this, LogSeverity.ERROR)
            Result.failure(Exception("Registration failed"))
        }
    } catch (e: Exception) {
        platform.log("❌ Registration Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }

    /**
     * Initiates password reset flow.
     */
    suspend fun forgotPassword(email: String): Result<Unit> = try {
        platform.log("📤 Forgot Password Request", this, LogSeverity.INFO)
        val response = client.post("$baseUrl/mobile/auth/forgotPassword") {
            contentType(ContentType.Application.Json)
            setBody(ForgotPasswordRequest(email))
        }

        if (response.status == HttpStatusCode.OK) {
            platform.log("✅ Forgot Password Success", this, LogSeverity.INFO)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Forgot password failed"))
        }
    } catch (e: Exception) {
        platform.log("❌ Forgot Password Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }

    /**
     * Resets password with reset code.
     */
    suspend fun resetPassword(email: String, resetCode: String, newPassword: String): Result<Unit> = try {
        platform.log("📤 Reset Password Request", this, LogSeverity.INFO)
        val response = client.post("$baseUrl/mobile/auth/resetPassword") {
            contentType(ContentType.Application.Json)
            setBody(ResetPasswordRequest(email, resetCode, newPassword))
        }

        if (response.status == HttpStatusCode.OK) {
            platform.log("✅ Reset Password Success", this, LogSeverity.INFO)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Reset password failed"))
        }
    } catch (e: Exception) {
        platform.log("❌ Reset Password Exception: ${e.message}", this, LogSeverity.ERROR)
        Result.failure(e)
    }

    /**
     * Refreshes access token using refresh token.
     */
    suspend fun refreshToken(refreshToken: String): Result<RefreshTokenResponse> =
        safeCall<RefreshTokenEnvelope, RefreshTokenResponse> {
            post("$baseUrl/mobile/auth/refreshToken") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken))
            }.body()
        }
}
