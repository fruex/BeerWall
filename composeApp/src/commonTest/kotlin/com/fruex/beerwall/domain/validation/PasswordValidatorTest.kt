package com.fruex.beerwall.domain.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PasswordValidatorTest {

    @Test
    fun `validate returns true for valid passwords`() {
        // Contains digit, lower, upper, special, length >= 6
        assertTrue(PasswordValidator.validate("Pass1!").isValid)
        assertTrue(PasswordValidator.validate("StrongP@ssw0rd").isValid)
        assertTrue(PasswordValidator.validate("123Abc$").isValid)
    }

    @Test
    fun `validate returns false for short passwords`() {
        // "Pas1!" is 5 chars
        assertFalse(PasswordValidator.validate("Pas1!").isValid)
    }

    @Test
    fun `validate returns false when missing digit`() {
        assertFalse(PasswordValidator.validate("Password!").isValid)
    }

    @Test
    fun `validate returns false when missing uppercase letter`() {
        assertFalse(PasswordValidator.validate("password123!").isValid)
    }

    @Test
    fun `validate returns false when missing lowercase letter`() {
        assertFalse(PasswordValidator.validate("PASSWORD123!").isValid)
    }

    @Test
    fun `validate returns false when missing special character`() {
        assertFalse(PasswordValidator.validate("Password123").isValid)
    }

    @Test
    fun `validate returns false for empty password`() {
        assertFalse(PasswordValidator.validate("").isValid)
    }

    @Test
    fun `validate checks individual criteria correctly`() {
        val result = PasswordValidator.validate("pass")
        assertFalse(result.isValid)
        assertFalse(result.isLengthValid)
        assertFalse(result.hasDigit)
        assertFalse(result.hasUpperCase)
        assertFalse(result.hasSpecialChar)
        assertTrue(result.hasLowerCase)
    }
}
