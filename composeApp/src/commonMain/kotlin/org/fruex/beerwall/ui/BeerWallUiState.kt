package org.fruex.beerwall.ui

import org.fruex.beerwall.remote.dto.operators.PaymentMethod
import org.fruex.beerwall.ui.models.DailyTransactions
import org.fruex.beerwall.ui.models.UserCard
import org.fruex.beerwall.ui.models.UserProfile
import org.fruex.beerwall.ui.models.VenueBalance

data class BeerWallUiState(
    val isCheckingSession: Boolean = true,
    val isLoggedIn: Boolean = false,
    val isRefreshing: Boolean = false,
    val balances: List<VenueBalance> = emptyList(),
    val cards: List<UserCard> = emptyList(),
    val userProfile: UserProfile = UserProfile(name = "", email = "", initials = "", activeCards = 0),
    val transactionGroups: List<DailyTransactions> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val errorMessage: String? = null
)
