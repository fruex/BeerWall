package org.fruex.beerwall.di

import org.fruex.beerwall.auth.SessionManager
import org.fruex.beerwall.auth.TokenManager
import org.fruex.beerwall.auth.TokenManagerImpl
import org.fruex.beerwall.data.remote.api.*
import org.fruex.beerwall.data.repository.*
import org.fruex.beerwall.domain.repository.*
import org.fruex.beerwall.domain.usecase.*
import org.fruex.beerwall.presentation.viewmodel.*
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<TokenManager> { TokenManagerImpl() }
    single { SessionManager(get()) }
}

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
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<BalanceRepository> { BalanceRepositoryImpl(get()) }
    single<CardRepository> { CardRepositoryImpl(get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<SupportRepository> { SupportRepositoryImpl(get()) }
}

val useCaseModule = module {
    singleOf(::GetBalancesUseCase)
    singleOf(::TopUpBalanceUseCase)
    singleOf(::GetCardsUseCase)
    singleOf(::ToggleCardStatusUseCase)
    singleOf(::AssignCardUseCase)
    singleOf(::DeleteCardUseCase)
    singleOf(::GetTransactionsUseCase)
    singleOf(::GetPaymentOperatorsUseCase)
    singleOf(::GoogleSignInUseCase)
    singleOf(::EmailPasswordSignInUseCase)
    singleOf(::RegisterUseCase)
    singleOf(::ForgotPasswordUseCase)
    singleOf(::ResetPasswordUseCase)
    singleOf(::CheckSessionUseCase)
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
    appModule,
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
