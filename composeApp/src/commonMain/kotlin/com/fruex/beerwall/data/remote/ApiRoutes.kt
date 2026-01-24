package com.fruex.beerwall.data.remote

/**
 * Definicje ścieżek API.
 * Przechowuje stałe URL używane przez klientów API.
 */
object ApiRoutes {
    private const val MOBILE = "mobile"

    /**
     * Ścieżki uwierzytelniania.
     */
    object Auth {
        private const val BASE = "$MOBILE/auth"
        const val GOOGLE_SIGN_IN = "$BASE/googleSignIn"
        const val SIGN_IN = "$BASE/signIn"
        const val SIGN_UP = "$BASE/signUp"
        const val FORGOT_PASSWORD = "$BASE/forgotPassword"
        const val RESET_PASSWORD = "$BASE/resetPassword"
        const val REFRESH_TOKEN = "$BASE/refreshToken"
    }

    /**
     * Ścieżki użytkowników.
     */
    object Users {
        private const val BASE = "$MOBILE/users"
        const val BALANCE = "$BASE/balance"
        const val HISTORY = "$BASE/history"
        const val RESET_PASSWORD = "$BASE/resetPassword"
        const val FEEDBACK = "$BASE/feedback"
    }

    /**
     * Ścieżki płatności.
     */
    object Payments {
        private const val BASE = "$MOBILE/payments"
        const val TOP_UP = "$BASE/topUp"
        const val OPERATORS = "$BASE/operators"
        const val BLIK_WS = "$BASE/blik/ws"
    }

    /**
     * Ścieżki kart.
     */
    object Cards {
        const val CARDS = "$MOBILE/cards"
        const val ASSIGN = "$CARDS/assign"
    }
}
