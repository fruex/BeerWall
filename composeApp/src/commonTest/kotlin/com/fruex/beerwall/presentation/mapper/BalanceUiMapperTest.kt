package com.fruex.beerwall.presentation.mapper

import com.fruex.beerwall.domain.model.Balance
import kotlin.test.Test
import kotlin.test.assertEquals

class BalanceUiMapperTest {

    @Test
    fun testToUiMapsCorrectly() {
        val balance = Balance(
            premisesId = 1,
            premisesName = "Test Pub",
            balance = 123.45,
            loyaltyPoints = 100
        )

        val uiModel = balance.toUi()

        assertEquals(1, uiModel.premisesId)
        assertEquals("Test Pub", uiModel.premisesName)
        assertEquals(123.45, uiModel.balance)
        assertEquals(100, uiModel.loyaltyPoints)
        assertEquals("123.45 zł", uiModel.formattedBalance)
        assertEquals("100 pkt", uiModel.formattedLoyaltyPoints)
    }

    @Test
    fun testListToUiMapsCorrectly() {
        val balances = listOf(
            Balance(1, "A", 10.0, 5),
            Balance(2, "B", 20.0, 10)
        )

        val uiModels = balances.toUi()

        assertEquals(2, uiModels.size)
        assertEquals("10.0 zł", uiModels[0].formattedBalance)
        assertEquals("20.0 zł", uiModels[1].formattedBalance)
    }
}
