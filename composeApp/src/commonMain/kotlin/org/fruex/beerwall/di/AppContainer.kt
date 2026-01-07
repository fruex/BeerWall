package org.fruex.beerwall.di

import org.fruex.beerwall.presentation.BeerWallViewModel
import org.fruex.beerwall.data.remote.BeerWallDataSource
import org.fruex.beerwall.data.repository.*
import org.fruex.beerwall.domain.repository.*
import org.fruex.beerwall.domain.usecase.*

/**
 * Application dependency container - Simple Service Locator pattern
 * Zarządza tworzeniem i dostarczaniem zależności dla całej aplikacji
 */
class AppContainer {
    
    // Data Layer
    private val dataSource: BeerWallDataSource by lazy { 
        BeerWallDataSource() 
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
    
    private val getTransactionsUseCase: GetTransactionsUseCase by lazy { 
        GetTransactionsUseCase(transactionRepository) 
    }
    
    private val getPaymentOperatorsUseCase: GetPaymentOperatorsUseCase by lazy {
        GetPaymentOperatorsUseCase(balanceRepository)
    }

    private val refreshAllDataUseCase: RefreshAllDataUseCase by lazy {
        RefreshAllDataUseCase(
            getBalancesUseCase,
            getCardsUseCase,
            getTransactionsUseCase
        )
    }

    // ViewModel Factory
    fun createBeerWallViewModel(): BeerWallViewModel {
        return BeerWallViewModel(
            refreshAllDataUseCase = refreshAllDataUseCase,
            getBalancesUseCase = getBalancesUseCase,
            topUpBalanceUseCase = topUpBalanceUseCase,
            getTransactionsUseCase = getTransactionsUseCase,
            toggleCardStatusUseCase = toggleCardStatusUseCase,
            getPaymentOperatorsUseCase = getPaymentOperatorsUseCase
        )
    }
}
