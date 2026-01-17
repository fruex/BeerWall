package org.fruex.beerwall.data.remote.dto

import kotlinx.serialization.json.Json
import org.fruex.beerwall.data.remote.dto.auth.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testy serializacji/deserializacji DTO dla logowania
 */
@OptIn(kotlin.experimental.ExperimentalNativeApi::class)
class GoogleSignInDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `RefreshTokenRequest should serialize correctly`() {
        // Given
        val request = RefreshTokenRequest("test-refresh-token")

        // When
        val jsonString = json.encodeToString(RefreshTokenRequest.serializer(), request)

        // Then
        assert(jsonString.contains("\"refreshToken\":\"test-refresh-token\""))
    }

    @Test
    fun `EmailPasswordSignInRequest should serialize correctly`() {
        // Given
        val request = EmailPasswordSignInRequest(
            email = "test@example.com",
            password = "password123"
        )

        // When
        val jsonString = json.encodeToString(EmailPasswordSignInRequest.serializer(), request)

        // Then
        assert(jsonString.contains("\"email\":\"test@example.com\""))
        assert(jsonString.contains("\"password\":\"password123\""))
    }

    @Test
    fun `GoogleSignInResponse should deserialize correctly`() {
        // Given
        val jsonString = """
            {
                "token": "test-token",
                "tokenExpires": 3600,
                "refreshToken": "refresh-token",
                "refreshTokenExpires": 7200
            }
        """.trimIndent()

        // When
        val response = json.decodeFromString<GoogleSignInResponse>(jsonString)

        // Then
        assertEquals("test-token", response.token)
        assertEquals(3600L, response.tokenExpires)
        assertEquals("refresh-token", response.refreshToken)
        assertEquals(7200L, response.refreshTokenExpires)
    }

    @Test
    fun `RefreshTokenResponse should deserialize correctly`() {
        // Given
        val jsonString = """
            {
                "token": "new-token",
                "tokenExpires": 3600,
                "refreshToken": "new-refresh-token",
                "refreshTokenExpires": 7200
            }
        """.trimIndent()

        // When
        val response = json.decodeFromString<RefreshTokenResponse>(jsonString)

        // Then
        assertEquals("new-token", response.token)
        assertEquals(3600L, response.tokenExpires)
        assertEquals("new-refresh-token", response.refreshToken)
        assertEquals(7200L, response.refreshTokenExpires)
    }

    @Test
    fun `EmailPasswordSignInResponse should deserialize correctly`() {
        // Given
        val jsonString = """
            {
                "token": "test-token",
                "tokenExpires": 3600,
                "refreshToken": "refresh-token",
                "refreshTokenExpires": 7200
            }
        """.trimIndent()

        // When
        val response = json.decodeFromString<EmailPasswordSignInResponse>(jsonString)

        // Then
        assertNotNull(response)
        assertEquals("test-token", response.token)
        assertEquals(3600L, response.tokenExpires)
        assertEquals("refresh-token", response.refreshToken)
        assertEquals(7200L, response.refreshTokenExpires)
    }
}
