package org.fruex.beerwall.ui

import org.fruex.beerwall.ui.models.DailyTransactions
import org.fruex.beerwall.ui.models.PaymentMethod
import org.fruex.beerwall.ui.models.UserCard
import org.fruex.beerwall.ui.models.UserProfile
import org.fruex.beerwall.ui.models.VenueBalance

/**
 * Stan interfejsu użytkownika (UI) dla całej aplikacji BeerWall.
 *
 * @property isCheckingSession Flaga wskazująca, czy trwa sprawdzanie sesji przy starcie.
 * @property isLoggedIn Flaga wskazująca, czy użytkownik jest zalogowany.
 * @property isRefreshing Flaga wskazująca, czy trwa odświeżanie danych (loading indicator).
 * @property balances Lista sald użytkownika.
 * @property cards Lista kart użytkownika.
 * @property userProfile Profil użytkownika (imię, email, inicjały).
 * @property transactionGroups Historia transakcji pogrupowana datami.
 * @property paymentMethods Dostępne metody płatności.
 * @property errorMessage Komunikat błędu do wyświetlenia (jeśli wystąpił).
 */
data class AppUiState(
    val isCheckingSession: Boolean = true,
    val isLoggedIn: Boolean = false,
    val isRefreshing: Boolean = false,
    val balances: List<VenueBalance> = emptyList(),
    val cards: List<UserCard> = emptyList(),
    val userProfile: UserProfile = UserProfile(name = "", email = "", initials = ""),
    val transactionGroups: List<DailyTransactions> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val errorMessage: String? = null
)
