package org.fruex.beerwall.di

import androidx.compose.runtime.Composable
import org.fruex.beerwall.presentation.viewmodel.*
import org.fruex.beerwall.auth.SessionManager
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.api.*
import org.fruex.beerwall.data.repository.*
import org.fruex.beerwall.domain.repository.*
import org.fruex.beerwall.domain.usecase.*

/**
 * Funkcja fabryczna do tworzenia kontenera aplikacji.
 */
@Composable
expect fun createAppContainer(): AppContainer

/**
 * Kontener zależności aplikacji - Implementacja wzorca Service Locator.
 * Zarządza tworzeniem i dostarczaniem zależności dla całej aplikacji.
 */
abstract class AppContainer {

    // Auth Layer
    abstract val tokenManager: TokenManager
    val sessionManager: SessionManager by lazy { SessionManager() }

    // Data Layer - API Clients
    private val authApiClient: AuthApiClient by lazy {
        AuthApiClient(tokenManager).apply {
            onUnauthorized = { sessionManager.onSessionExpired() }
        }
    }

    private val cardsApiClient: CardsApiClient by lazy {
        CardsApiClient(tokenManager).apply {
            onUnauthorized = { sessionManager.onSessionExpired() }
        }
    }

    private val balanceApiClient: BalanceApiClient by lazy {
        BalanceApiClient(tokenManager).apply {
            onUnauthorized = { sessionManager.onSessionExpired() }
        }
    }

    private val historyApiClient: HistoryApiClient by lazy {
        HistoryApiClient(tokenManager).apply {
            onUnauthorized = { sessionManager.onSessionExpired() }
        }
    }

    private val supportApiClient: SupportApiClient by lazy {
        SupportApiClient(tokenManager).apply {
            onUnauthorized = { sessionManager.onSessionExpired() }
        }
    }

    // Repository Layer
    private val balanceRepository: BalanceRepository by lazy {
        BalanceRepositoryImpl(balanceApiClient)
    }

    private val cardRepository: CardRepository by lazy {
        CardRepositoryImpl(cardsApiClient)
    }

    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(historyApiClient)
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApiClient, tokenManager)
    }

    private val supportRepository: SupportRepository by lazy {
        SupportRepositoryImpl(supportApiClient)
    }
    
    // Use Cases
    private val getBalancesUseCase: GetBalancesUseCase by lazy { 
        GetBalancesUseCase(balanceRepository) 
    }
    
    private val topUpBalanceUseCase: TopUpBalanceUseCase by lazy { 
        TopUpBalanceUseCase(balanceRepository) 
    }
    
    private val getCardsUseCase: GetCardsUseCase by lazy { 
        GetCardsUseCase(cardRepository) 
    }
    
    private val toggleCardStatusUseCase: ToggleCardStatusUseCase by lazy { 
        ToggleCardStatusUseCase(cardRepository) 
    }

    private val assignCardUseCase: AssignCardUseCase by lazy {
        AssignCardUseCase(cardRepository)
    }

    private val deleteCardUseCase: DeleteCardUseCase by lazy {
        DeleteCardUseCase(cardRepository)
    }
    
    private val getTransactionsUseCase: GetTransactionsUseCase by lazy { 
        GetTransactionsUseCase(transactionRepository) 
    }
    
    private val getPaymentOperatorsUseCase: GetPaymentOperatorsUseCase by lazy {
        GetPaymentOperatorsUseCase(balanceRepository)
    }

    private val googleSignInUseCase: GoogleSignInUseCase by lazy {
        GoogleSignInUseCase(authRepository)
    }

    private val emailPasswordSignInUseCase: EmailPasswordSignInUseCase by lazy {
        EmailPasswordSignInUseCase(authRepository)
    }

    private val registerUseCase: RegisterUseCase by lazy {
        RegisterUseCase(authRepository)
    }

    private val forgotPasswordUseCase: ForgotPasswordUseCase by lazy {
        ForgotPasswordUseCase(authRepository)
    }

    private val resetPasswordUseCase: ResetPasswordUseCase by lazy {
        ResetPasswordUseCase(authRepository)
    }

    private val checkSessionUseCase: CheckSessionUseCase by lazy {
        CheckSessionUseCase(authRepository)
    }

    private val sendMessageUseCase: SendMessageUseCase by lazy {
        SendMessageUseCase(supportRepository)
    }

    // ViewModel Factories

    fun createAuthViewModel(): AuthViewModel {
        return AuthViewModel(
            googleSignInUseCase = googleSignInUseCase,
            emailPasswordSignInUseCase = emailPasswordSignInUseCase,
            registerUseCase = registerUseCase,
            forgotPasswordUseCase = forgotPasswordUseCase,
            resetPasswordUseCase = resetPasswordUseCase,
            checkSessionUseCase = checkSessionUseCase,
            authRepository = authRepository,
            sessionManager = sessionManager
        )
    }

    fun createBalanceViewModel(): BalanceViewModel {
        return BalanceViewModel(
            getBalancesUseCase = getBalancesUseCase,
            topUpBalanceUseCase = topUpBalanceUseCase,
            getPaymentOperatorsUseCase = getPaymentOperatorsUseCase
        )
    }

    fun createCardsViewModel(): CardsViewModel {
        return CardsViewModel(
            getCardsUseCase = getCardsUseCase,
            toggleCardStatusUseCase = toggleCardStatusUseCase,
            assignCardUseCase = assignCardUseCase,
            deleteCardUseCase = deleteCardUseCase
        )
    }

    fun createHistoryViewModel(): HistoryViewModel {
        return HistoryViewModel(
            getTransactionsUseCase = getTransactionsUseCase
        )
    }

    fun createProfileViewModel(): ProfileViewModel {
        return ProfileViewModel(
            sendMessageUseCase = sendMessageUseCase
        )
    }
}
