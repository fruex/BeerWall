package org.fruex.beerwall.remote.dto.operators

import kotlinx.serialization.Serializable
import org.fruex.beerwall.remote.common.ApiError
import org.fruex.beerwall.remote.common.ApiResponse

@Serializable
data class GetPaymentOperatorsResponse(
    override val data: List<String>? = null,
    override val error: ApiError? = null
) : ApiResponse<List<String>>
