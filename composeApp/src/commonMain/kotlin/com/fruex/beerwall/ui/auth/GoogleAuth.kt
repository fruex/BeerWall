package com.fruex.beerwall.ui.auth

import androidx.compose.runtime.Composable
import com.fruex.beerwall.domain.auth.GoogleAuthProvider

@Composable
expect fun rememberGoogleAuthProvider(): GoogleAuthProvider
