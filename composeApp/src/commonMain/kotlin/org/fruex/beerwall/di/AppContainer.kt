package org.fruex.beerwall.di

import androidx.compose.runtime.Composable
import org.fruex.beerwall.presentation.BeerWallViewModel
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.data.repository.*
import org.fruex.beerwall.domain.repository.*
import org.fruex.beerwall.domain.usecase.*

/**
 * Funkcja fabrykująca dla kontenera aplikacji (implementacja platformowa).
 */
@Composable
expect fun createAppContainer(): AppContainer

/**
 * Kontener zależności aplikacji - Implementacja wzorca Service Locator.
 * Zarządza cyklem życia, tworzeniem i dostarczaniem zależności dla całej aplikacji.
 *
 * TODO: Rozważyć użycie biblioteki do wstrzykiwania zależności (np. Koin) dla lepszej skalowalności i testowalności.
 */
abstract class AppContainer {

    // Warstwa Auth - dostarczana przez implementację platformową
    abstract val tokenManager: TokenManager

    // Warstwa Danych
    // DataSource jest inicjalizowany leniwie.
    // Callback onUnauthorized zostanie ustawiony w ViewModelu, co tworzy cykliczną zależność logiczną.
    // TODO: Rozważyć lepszy sposób obsługi 401 Unauthorized, np. przez globalny event bus lub obserwację stanu w repozytorium.
    private val dataSource: BeerWallDataSource by lazy {
        BeerWallDataSource(tokenManager)
    }

    // Warstwa Repozytoriów
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
    
    // Przypadki Użycia (Use Cases)
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

    private val checkSessionUseCase: CheckSessionUseCase by lazy {
        CheckSessionUseCase(authRepository)
    }

    private val refreshAllDataUseCase: RefreshAllDataUseCase by lazy {
        RefreshAllDataUseCase(
            getBalancesUseCase,
            getCardsUseCase,
            getTransactionsUseCase
        )
    }

    // Fabryka ViewModeli
    fun createBeerWallViewModel(): BeerWallViewModel {
        val viewModel = BeerWallViewModel(
            refreshAllDataUseCase = refreshAllDataUseCase,
            getBalancesUseCase = getBalancesUseCase,
            topUpBalanceUseCase = topUpBalanceUseCase,
            getTransactionsUseCase = getTransactionsUseCase,
            toggleCardStatusUseCase = toggleCardStatusUseCase,
            getPaymentOperatorsUseCase = getPaymentOperatorsUseCase,
            googleSignInUseCase = googleSignInUseCase,
            emailPasswordSignInUseCase = emailPasswordSignInUseCase,
            checkSessionUseCase = checkSessionUseCase,
            authRepository = authRepository
        )

        // Konfiguracja callbacku dla automatycznego wylogowania przy błędzie 401.
        // To jest workaround dla braku pełnego DI - ViewModel "nasłuchuje" na DataSource.
        dataSource.onUnauthorized = {
            viewModel.handleSessionExpired()
        }

        return viewModel
    }
}
