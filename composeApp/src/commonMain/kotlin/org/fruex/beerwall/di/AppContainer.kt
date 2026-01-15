package org.fruex.beerwall.di

import androidx.compose.runtime.Composable
import org.fruex.beerwall.presentation.BeerWallViewModel
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BeerWallDataSource
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

    // Data Layer
    // DataSource jest inicjalizowany bez callbacku, callback zostanie ustawiony w ViewModelu
    private val dataSource: BeerWallDataSource by lazy {
        BeerWallDataSource(tokenManager)
    }

    // Repository Layer
    private val balanceRepository: BalanceRepository by lazy {
        BalanceRepositoryImpl(dataSource)
    }

    private val cardRepository: CardRepository by lazy {
        CardRepositoryImpl(dataSource)
    }

    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(dataSource)
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(dataSource, tokenManager)
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

    // ViewModel Factory
    /**
     * Tworzy instancję [BeerWallViewModel].
     */
    fun createBeerWallViewModel(): BeerWallViewModel {
        val viewModel = BeerWallViewModel(
            getBalancesUseCase = getBalancesUseCase,
            topUpBalanceUseCase = topUpBalanceUseCase,
            getTransactionsUseCase = getTransactionsUseCase,
            toggleCardStatusUseCase = toggleCardStatusUseCase,
            assignCardUseCase = assignCardUseCase,
            deleteCardUseCase = deleteCardUseCase,
            getCardsUseCase = getCardsUseCase,
            getPaymentOperatorsUseCase = getPaymentOperatorsUseCase,
            googleSignInUseCase = googleSignInUseCase,
            emailPasswordSignInUseCase = emailPasswordSignInUseCase,
            registerUseCase = registerUseCase,
            forgotPasswordUseCase = forgotPasswordUseCase,
            resetPasswordUseCase = resetPasswordUseCase,
            checkSessionUseCase = checkSessionUseCase,
            authRepository = authRepository
        )

        // Skonfiguruj callback dla automatycznego wylogowania
        dataSource.onUnauthorized = {
            viewModel.handleSessionExpired()
        }

        return viewModel
    }
}
