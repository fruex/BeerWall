package com.fruex.beerwall.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import beerwall.composeapp.generated.resources.Res
import beerwall.composeapp.generated.resources.ic_apple
import beerwall.composeapp.generated.resources.ic_facebook
import beerwall.composeapp.generated.resources.ic_google
import com.fruex.beerwall.domain.validation.PasswordValidator
import com.fruex.beerwall.ui.components.*
import com.fruex.beerwall.ui.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class AuthMode {
    LOGIN,
    REGISTER
}

/**
 * Ujednolicony ekran autentykacji obsługujący zarówno logowanie, jak i rejestrację.
 *
 * @param mode Tryb autentykacji (LOGIN lub REGISTER)
 * @param onAuthClick Callback do autentykacji email/hasło
 * @param onGoogleSignInClick Callback do logowania przez Google
 * @param onToggleModeClick Callback do przełączania między trybami logowania/rejestracji
 * @param onForgotPasswordClick Opcjonalny callback do resetowania hasła (tylko logowanie)
 * @param isLoading Stan ładowania
 * @param errorMessage Opcjonalny komunikat błędu do wyświetlenia
 */
@Composable
fun AuthScreen(
    mode: AuthMode,
    onAuthClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onToggleModeClick: () -> Unit,
    onForgotPasswordClick: ((String) -> Unit)? = null,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showEmailAuth by rememberSaveable { mutableStateOf(false) }
    var showPasswordStep by rememberSaveable { mutableStateOf(false) }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val isLogin = mode == AuthMode.LOGIN
    val focusManager = LocalFocusManager.current
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    // Walidacja hasła tylko dla rejestracji
    val passwordValidation = remember(password) { PasswordValidator.validate(password) }
    val isPasswordValid = if (isLogin) password.isNotBlank() else passwordValidation.isValid

    LaunchedEffect(showEmailAuth) {
        if (showEmailAuth) {
            emailFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(showPasswordStep) {
        if (showPasswordStep) {
            passwordFocusRequester.requestFocus()
        }
    }

    LoadingDialog(
        isVisible = isLoading,
        title = if (isLogin) "Logowanie..." else "Rejestracja..."
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo()

            Spacer(modifier = Modifier.height(48.dp))

            AuthHeader(
                subtitle = if (isLogin) "Zaloguj do konta" else "Utwórz konto"
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Error message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Google Button
            SocialLoginButton(
                text = "Kontynuuj z Google",
                onClick = onGoogleSignInClick,
                iconRes = Res.drawable.ic_google,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Facebook Button (wyłączony)
            SocialLoginButton(
                text = "Kontynuuj z Facebook",
                onClick = { },
                iconRes = Res.drawable.ic_facebook,
                enabled = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Apple Button (wyłączony)
            SocialLoginButton(
                text = "Kontynuuj z Apple",
                onClick = { },
                iconRes = Res.drawable.ic_apple,
                enabled = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = TextSecondary.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Przełącznik autentykacji email
            if (!showEmailAuth) {
                SocialLoginButton(
                    text = "Kontynuuj z e-mailem",
                    onClick = { showEmailAuth = true },
                    icon = Icons.Default.Email,
                    enabled = !isLoading
                )
            } else {
                // Formularz autentykacji email
                BeerWallTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "E-mail",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            if (email.isNotBlank()) {
                                showPasswordStep = true
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    inputModifier = Modifier.focusRequester(emailFocusRequester),
                    enabled = !isLoading && !showPasswordStep
                )

                if (!showPasswordStep) {
                    Spacer(modifier = Modifier.height(24.dp))

                    BeerWallButton(
                        text = if (isLogin) "Zaloguj" else "Dalej",
                        onClick = { showPasswordStep = true },
                        enabled = email.isNotBlank() && !isLoading
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))

                    BeerWallTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Hasło",
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (isPasswordValid) {
                                    focusManager.clearFocus()
                                    onAuthClick(email, password)
                                }
                            }
                        ),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isPasswordVisible) "Ukryj hasło" else "Pokaż hasło",
                                    tint = TextSecondary
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        inputModifier = Modifier.focusRequester(passwordFocusRequester),
                        enabled = !isLoading
                    )

                    // Wyświetlanie wymagań hasła tylko przy rejestracji i gdy hasło nie jest puste
                    if (!isLogin && password.isNotEmpty() && !passwordValidation.isValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hasło musi mieć min. 6 znaków, zawierać małą i wielką literę, cyfrę oraz znak specjalny.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    BeerWallButton(
                        text = if (isLogin) "Zaloguj" else "Utwórz konto",
                        onClick = { onAuthClick(email, password) },
                        enabled = isPasswordValid && !isLoading
                    )

                    // Forgot Password Button (only for login)
                    if (isLogin && onForgotPasswordClick != null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = { onForgotPasswordClick(email) },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Zapomniałeś hasła?",
                                color = GoldPrimary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Wiersz przełączania trybu
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isLogin) "Nie masz konta? " else "Masz już konto? ",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                TextButton(
                    onClick = onToggleModeClick,
                    contentPadding = PaddingValues(0.dp),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isLogin) "Utwórz konto" else "Zaloguj się",
                        color = GoldPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AuthScreenLoginPreview() {
    BeerWallTheme {
        AuthScreen(
            mode = AuthMode.LOGIN,
            onAuthClick = { _, _ -> },
            onGoogleSignInClick = {},
            onToggleModeClick = {},
            onForgotPasswordClick = {}
        )
    }
}

@Preview
@Composable
fun AuthScreenRegisterPreview() {
    BeerWallTheme {
        AuthScreen(
            mode = AuthMode.REGISTER,
            onAuthClick = { _, _ -> },
            onGoogleSignInClick = {},
            onToggleModeClick = {}
        )
    }
}
