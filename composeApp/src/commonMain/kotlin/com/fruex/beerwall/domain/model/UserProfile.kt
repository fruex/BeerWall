package com.fruex.beerwall.domain.model

data class UserProfile(
    val name: String
) {
    val initials: String
        get() = name.split(" ")
            .mapNotNull { it.firstOrNull() }
            .take(2)
            .joinToString("")
            .uppercase()
}
