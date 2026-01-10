package org.fruex.beerwall.di

import androidx.compose.runtime.Composable
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.auth.TokenManagerImpl

private class IosAppContainer : AppContainer() {
    override val tokenManager: TokenManager = TokenManagerImpl()
}

@Composable
actual fun createAppContainer(): AppContainer {
    return IosAppContainer()
}
