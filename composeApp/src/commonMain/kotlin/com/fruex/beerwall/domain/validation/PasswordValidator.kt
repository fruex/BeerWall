package com.fruex.beerwall.domain.validation

object PasswordValidator {
    private const val MIN_LENGTH = 6

    fun validate(password: String): PasswordValidationResult {
        val hasDigit = password.any { it.isDigit() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val isLengthValid = password.length >= MIN_LENGTH

        val isValid = isLengthValid && hasDigit && hasLowerCase && hasUpperCase && hasSpecialChar

        return PasswordValidationResult(
            isValid = isValid,
            hasDigit = hasDigit,
            hasLowerCase = hasLowerCase,
            hasUpperCase = hasUpperCase,
            hasSpecialChar = hasSpecialChar,
            isLengthValid = isLengthValid
        )
    }
}

data class PasswordValidationResult(
    val isValid: Boolean,
    val hasDigit: Boolean,
    val hasLowerCase: Boolean,
    val hasUpperCase: Boolean,
    val hasSpecialChar: Boolean,
    val isLengthValid: Boolean
)
