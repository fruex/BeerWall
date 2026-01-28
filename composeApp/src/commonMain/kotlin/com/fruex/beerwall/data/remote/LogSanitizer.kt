package com.fruex.beerwall.data.remote

/**
 * Utility for sanitizing sensitive information from logs.
 */
object LogSanitizer {
    private val SENSITIVE_KEYS_REGEX = Regex(
        pattern = "(?i)(\"(?:password|oldPassword|newPassword|confirmPassword|currentPassword|resetCode)\"\\s*:\\s*)\"(?:\\\\.|[^\"\\\\])*\"",
        options = setOf(RegexOption.IGNORE_CASE)
    )

    /**
     * Masks sensitive fields in a JSON string (or any string containing "key": "value" patterns).
     * Replaces the value with "***".
     */
    fun sanitize(message: String): String {
        return SENSITIVE_KEYS_REGEX.replace(message, "$1\"***\"")
    }
}
