package com.fruex.beerwall.data.remote

import kotlin.test.Test
import kotlin.test.assertEquals

class LogSanitizerTest {

    @Test
    fun testSanitizePassword() {
        val input = """{"email": "test@example.com", "password": "secretPassword123"}"""
        val expected = """{"email": "test@example.com", "password": "***"}"""
        assertEquals(expected, LogSanitizer.sanitize(input))
    }

    @Test
    fun testSanitizeNestedPassword() {
        val input = """
            {
                "user": {
                    "email": "test@example.com",
                    "password": "secretPassword123"
                }
            }
        """.trimIndent()

        val expected = """
            {
                "user": {
                    "email": "test@example.com",
                    "password": "***"
                }
            }
        """.trimIndent()

        assertEquals(expected, LogSanitizer.sanitize(input))
    }

    @Test
    fun testSanitizeMultipleSensitiveFields() {
        val input = """{"oldPassword": "old", "newPassword": "new", "confirmPassword": "new"}"""
        val expected = """{"oldPassword": "***", "newPassword": "***", "confirmPassword": "***"}"""
        assertEquals(expected, LogSanitizer.sanitize(input))
    }

    @Test
    fun testSanitizeWithEscapedQuotes() {
        val input = """{"password": "pass\"word"}"""
        val expected = """{"password": "***"}"""
        assertEquals(expected, LogSanitizer.sanitize(input))
    }

    @Test
    fun testSanitizeCaseInsensitive() {
        val input = """{"Password": "Secret"}"""
        val expected = """{"Password": "***"}"""
        assertEquals(expected, LogSanitizer.sanitize(input))
    }

    @Test
    fun testIgnoreSafeFields() {
        val input = """{"description": "This is a password field in text", "other": "value"}"""
        val expected = """{"description": "This is a password field in text", "other": "value"}"""
        assertEquals(expected, LogSanitizer.sanitize(input))
    }
}
