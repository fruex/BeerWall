package com.fruex.beerwall.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.fruex.beerwall.ui.theme.BeerWallTheme
import com.fruex.beerwall.ui.theme.InputBackground
import com.fruex.beerwall.ui.theme.TextPrimary
import com.fruex.beerwall.ui.theme.TextSecondary
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Standardowe pole tekstowe aplikacji.
 *
 * @param value Aktualna wartość.
 * @param onValueChange Callback zmiany wartości.
 * @param placeholder Tekst zastępczy.
 * @param modifier Modyfikator.
 * @param visualTransformation Transformacja wizualna (np. maskowanie hasła).
 * @param keyboardOptions Opcje klawiatury.
 * @param keyboardActions Akcje klawiatury.
 * @param leadingIcon Ikona początkowa.
 * @param trailingIcon Ikona końcowa.
 * @param isError Flaga błędu.
 * @param errorMessage Wiadomość błędu.
 * @param enabled Czy pole jest aktywne.
 */
@Composable
fun BeerWallTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    inputModifier: Modifier = Modifier,
    isPassword: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    textAlign: TextAlign = TextAlign.Start
) {
    val effectiveVisualTransformation = if (isPassword) PasswordVisualTransformation() else visualTransformation
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextSecondary,
                    textAlign = textAlign,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .then(inputModifier),
            visualTransformation = effectiveVisualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            enabled = enabled,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                disabledContainerColor = InputBackground.copy(alpha = 0.5f),
                errorContainerColor = InputBackground,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextSecondary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            textStyle = TextStyle(
                textAlign = textAlign,
                fontWeight = FontWeight.SemiBold
            )
        )
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
fun BeerWallTextFieldPreview() {
    BeerWallTheme {
        BeerWallTextField(
            value = "",
            onValueChange = {},
            placeholder = "Wpisz tekst..."
        )
    }
}
