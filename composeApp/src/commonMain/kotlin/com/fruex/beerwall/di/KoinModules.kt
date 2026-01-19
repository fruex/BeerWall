package com.fruex.beerwall.di

import com.fruex.beerwall.auth.SessionManager
import com.fruex.beerwall.auth.TokenManager
import com.fruex.beerwall.auth.TokenManagerImpl
import com.fruex.beerwall.data.remote.api.*
import com.fruex.beerwall.data.repository.*
import com.fruex.beerwall.domain.repository.*
import com.fruex.beerwall.domain.usecase.*
import com.fruex.beerwall.presentation.viewmodel.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val networkModule = module {
    single {
        AuthApiClient(get()).apply {
            onUnauthorized = { get<SessionManager>().onSessionExpired() }
        }
    }
    single {
        CardsApiClient(get()).apply {
            onUnauthorized = { get<SessionManager>().onSessionExpired() }
        }
    }
    single {
        BalanceApiClient(get()).apply {
            onUnauthorized = { get<SessionManager>().onSessionExpired() }
        }
    }
    single {
        HistoryApiClient(get()).apply {
            onUnauthorized = { get<SessionManager>().onSessionExpired() }
        }
    }
    single {
        SupportApiClient(get()).apply {
            onUnauthorized = { get<SessionManager>().onSessionExpired() }
        }
    }
}

val repositoryModule = module {
    single { SessionManager() }
    single { AuthRepositoryImpl(get(), get(), get()) } bind AuthRepository::class
    single { BalanceRepositoryImpl(get()) } bind BalanceRepository::class
    single { CardRepositoryImpl(get()) } bind CardRepository::class
    single { TransactionRepositoryImpl(get()) } bind TransactionRepository::class
    single { SupportRepositoryImpl(get()) } bind SupportRepository::class
}

val useCaseModule = module {
    // Auth
    singleOf(::GoogleSignInUseCase)
    singleOf(::EmailPasswordSignInUseCase)
    singleOf(::RegisterUseCase)
    singleOf(::ForgotPasswordUseCase)
    singleOf(::ResetPasswordUseCase)
    singleOf(::CheckSessionUseCase)
    singleOf(::LogoutUseCase)
    singleOf(::ObserveSessionStateUseCase)

    // Balance
    singleOf(::GetBalancesUseCase)
    singleOf(::TopUpBalanceUseCase)
    singleOf(::GetPaymentOperatorsUseCase)

    // Cards
    singleOf(::GetCardsUseCase)
    singleOf(::ToggleCardStatusUseCase)
    singleOf(::AssignCardUseCase)
    singleOf(::DeleteCardUseCase)

    // Transactions
    singleOf(::GetTransactionsUseCase)

    // Support
    singleOf(::SendMessageUseCase)
}

val viewModelModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::BalanceViewModel)
    viewModelOf(::CardsViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::ProfileViewModel)
}

fun appModules() = listOf(
    platformModule,
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
