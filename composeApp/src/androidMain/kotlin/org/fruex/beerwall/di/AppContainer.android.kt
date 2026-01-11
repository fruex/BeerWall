package org.fruex.beerwall.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.auth.TokenManagerImpl

private class AndroidAppContainer(context: Context) : AppContainer() {
    override val tokenManager: TokenManager = TokenManagerImpl(context)
}

@Composable
actual fun createAppContainer(): AppContainer {
    val context = LocalContext.current
    return remember(context) { 
        AndroidAppContainer(context.applicationContext) 
    }
}
