package org.fruex.beerwall.domain.model

/**
 * Model domeny reprezentujący saldo użytkownika w konkretnym lokalu.
 *
 * @property premisesId Unikalny identyfikator lokalu.
 * @property premisesName Nazwa lokalu.
 * @property balance Aktualne saldo środków dostępnych w lokalu.
 * @property loyaltyPoints Ilość zgromadzonych punktów lojalnościowych.
 */
data class Balance(
    val premisesId: Int,
    val premisesName: String,
    val balance: Double,
    val loyaltyPoints: Int
)
