package com.fruex.beerwall.data.remote.dto.balance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.fruex.beerwall.data.remote.common.ApiEnvelope

/**
 * DTO salda użytkownika w danym lokalu.
 *
 * @property premisesId Identyfikator lokalu.
 * @property premisesName Nazwa lokalu.
 * @property balance Dostępne środki.
 * @property loyaltyPoints Punkty lojalnościowe.
 */
@Serializable
data class BalanceResponse(
    val premisesId: Int,
    val premisesName: String,
    val balance: Double,
    @SerialName("loyalityPoints") // API has typo, mapping to correct property name
    val loyaltyPoints: Int
)

/**
 * Alias dla koperty odpowiedzi pobierania salda.
 */
typealias GetBalanceEnvelope = ApiEnvelope<List<BalanceResponse>>

/**
 * DTO żądania doładowania konta.
 *
 * @property premisesId Identyfikator lokalu.
 * @property paymentMethodId Identyfikator metody płatności.
 * @property amount Kwota doładowania.
 */
@Serializable
data class TopUpRequest(
    val premisesId: Int,
    val paymentMethodId: Int,
    val amount: Double
)
