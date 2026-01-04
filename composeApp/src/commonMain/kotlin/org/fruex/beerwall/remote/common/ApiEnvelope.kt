package org.fruex.beerwall.remote.common

import kotlinx.serialization.Serializable

@Serializable
data class ApiEnvelope<T>(
    override val data: T? = null,
    override val error: ApiError? = null
) : ApiResponse<T>

interface ApiResponse<T> {
    val data: T?
    val error: ApiError?
}

@Serializable
data class ApiError(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null
)
