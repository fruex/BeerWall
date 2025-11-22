package com.igibeer.beerwall.data.remote.common

// Note: @Serializable annotations can be added once the Kotlin serialization plugin is enabled in Amper.
data class ApiEnvelope<T>(
    val data: T? = null,
    val error: ApiError? = null
)

data class ApiError(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null
)
