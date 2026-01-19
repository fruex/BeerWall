package com.fruex.beerwall.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Testy dla TokenManager sprawdzające podstawowe operacje na tokenach
 */
class TokenManagerTest {

    @Test
    fun `AuthTokens should be created with all required fields`() {
        // Given & When
        val tokens = AuthTokens(
            token = "test-token",
            tokenExpires = 3600L,
            refreshToken = "refresh-token",
            refreshTokenExpires = 7200L,
            firstName = "Jan",
            lastName = "Kowalski"
        )

        // Then
        assertEquals("test-token", tokens.token)
        assertEquals(3600L, tokens.tokenExpires)
        assertEquals("refresh-token", tokens.refreshToken)
        assertEquals(7200L, tokens.refreshTokenExpires)
    }

    @Test
    fun `GoogleUser should be created with all fields`() {
        // Given & When
        val user = GoogleUser(
            idToken = "google-id-token",
            tokenExpires = 3600L,
            refreshToken = "refresh-token",
            refreshTokenExpires = 7200L,
            displayName = "Test User",
            email = "test@example.com",
        )

        // Then
        assertEquals("google-id-token", user.idToken)
        assertEquals(3600L, user.tokenExpires)
        assertEquals("refresh-token", user.refreshToken)
        assertEquals(7200L, user.refreshTokenExpires)
        assertEquals("Test User", user.displayName)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun `GoogleUser should allow null optional fields`() {
        // Given & When
        val user = GoogleUser(
            idToken = "google-id-token",
            tokenExpires = null,
            refreshToken = null,
            refreshTokenExpires = null,
            displayName = null,
            email = null
        )

        // Then
        assertNotNull(user.idToken)
        assertNull(user.tokenExpires)
        assertNull(user.refreshToken)
        assertNull(user.refreshTokenExpires)
        assertNull(user.displayName)
        assertNull(user.email)
    }

    @Test
    fun `GoogleUser copy should work correctly`() {
        // Given
        val originalUser = GoogleUser(
            idToken = "original-token",
            displayName = null,
            email = null
        )

        // When
        val updatedUser = originalUser.copy(
            displayName = "Updated Name",
            email = "updated@example.com"
        )

        // Then
        assertEquals("original-token", updatedUser.idToken)
        assertEquals("Updated Name", updatedUser.displayName)
        assertEquals("updated@example.com", updatedUser.email)
    }
}
