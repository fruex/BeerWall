package com.fruex.beerwall.domain.validation

object EmailValidator {
    // Standard email regex pattern
    private val EMAIL_REGEX = Regex(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    )

    fun validate(email: String): Boolean {
        return email.matches(EMAIL_REGEX)
    }
}
