package com.fruex.beerwall.data.mapper

import com.fruex.beerwall.data.remote.dto.payments.GdprClauseResponse
import com.fruex.beerwall.domain.model.GdprClause

fun GdprClauseResponse.toDomain(): GdprClause {
    return GdprClause(
        title = title,
        content = content,
        locale = locale
    )
}
