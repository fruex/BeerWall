package com.fruex.beerwall.domain.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmailValidatorTest {

    @Test
    fun `validate returns true for valid emails`() {
        assertTrue(EmailValidator.validate("test@example.com"))
        assertTrue(EmailValidator.validate("user.name@domain.co.uk"))
        assertTrue(EmailValidator.validate("user+tag@domain.com"))
        assertTrue(EmailValidator.validate("123@123.com"))
    }

    @Test
    fun `validate returns false for invalid emails`() {
        assertFalse(EmailValidator.validate(""))
        assertFalse(EmailValidator.validate("plainaddress"))
        assertFalse(EmailValidator.validate("@no-local-part.com"))
        assertFalse(EmailValidator.validate("user@.com.my"))
        assertFalse(EmailValidator.validate("user123@gmail"))
        assertFalse(EmailValidator.validate("user@domain"))
        assertFalse(EmailValidator.validate("user@domain."))
    }
}
