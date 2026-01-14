package org.fruex.beerwall.domain.model

/**
 * Model reprezentujący saldo użytkownika w konkretnym lokalu.
 *
 * @property venueId Unikalny identyfikator lokalu.
 * @property venueName Nazwa lokalu.
 * @property amount Aktualny stan środków (saldo).
 * @property loyaltyPoints Liczba punktów lojalnościowych zgromadzonych w lokalu.
 */
data class Balance(
    val premisesId: Int,
    val premisesName: String,
    val balance: Double,
    val loyalityPoints: Int
)
