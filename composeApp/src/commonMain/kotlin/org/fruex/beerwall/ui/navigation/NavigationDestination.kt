package org.fruex.beerwall.ui.navigation

sealed class NavigationDestination(val route: String) {
    // Auth
    data object Registration : NavigationDestination("registration")
    data object Login : NavigationDestination("login")

    // Main screen with bottom navigation
    data object Main : NavigationDestination("main")

    // Main tabs (used internally by MainScreen)
    data object Balance : NavigationDestination("balance")
    data object Cards : NavigationDestination("cards")
    data object History : NavigationDestination("history")
    data object Profile : NavigationDestination("profile")

    // Other screens
    data object AddFunds : NavigationDestination("add_funds")
    data object AddCard : NavigationDestination("add_card")
    
    // Profile sub-screens
    data object ChangePassword : NavigationDestination("change_password")
    data object Support : NavigationDestination("support")
    data object About : NavigationDestination("about")
}
