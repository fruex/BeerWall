package com.fruex.beerwall.domain.repository

import com.fruex.beerwall.data.remote.dto.payments.BlikStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repozytorium do obsługi płatności BLIK.
 */
interface BlikRepository {
    /**
     * Łączy się z sesją BLIK i nasłuchuje statusów transakcji.
     *
     * @param transactionId ID transakcji (opcjonalne, może być użyte do filtrowania lub jako parametr połączenia).
     */
    fun connectToBlikSession(transactionId: String?): Flow<BlikStatus>
}
