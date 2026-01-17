package org.fruex.beerwall.presentation

import kotlinx.coroutines.test.runTest
import org.fruex.beerwall.domain.usecase.*
import org.fruex.beerwall.fakes.*
import org.fruex.beerwall.presentation.viewmodel.*
import org.fruex.beerwall.test.BaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppViewModelTest : BaseTest() {

    private lateinit var viewModel: AppViewModel
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var balanceRepository: FakeBalanceRepository
    private lateinit var cardRepository: FakeCardRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var supportRepository: FakeSupportRepository

    @BeforeTest
    fun setupViewModel() {
        authRepository = FakeAuthRepository()
        balanceRepository = FakeBalanceRepository()
        cardRepository = FakeCardRepository()
        transactionRepository = FakeTransactionRepository()
        supportRepository = FakeSupportRepository()

        // Tworzenie feature ViewModeli
        val authViewModel = AuthViewModel(
            googleSignInUseCase = GoogleSignInUseCase(authRepository),
            emailPasswordSignInUseCase = EmailPasswordSignInUseCase(authRepository),
            registerUseCase = RegisterUseCase(authRepository),
            forgotPasswordUseCase = ForgotPasswordUseCase(authRepository),
            resetPasswordUseCase = ResetPasswordUseCase(authRepository),
            checkSessionUseCase = CheckSessionUseCase(authRepository),
            authRepository = authRepository
        )

        val balanceViewModel = BalanceViewModel(
            getBalancesUseCase = GetBalancesUseCase(balanceRepository),
            topUpBalanceUseCase = TopUpBalanceUseCase(balanceRepository),
            getPaymentOperatorsUseCase = GetPaymentOperatorsUseCase(balanceRepository)
        )

        val cardsViewModel = CardsViewModel(
            getCardsUseCase = GetCardsUseCase(cardRepository),
            toggleCardStatusUseCase = ToggleCardStatusUseCase(cardRepository),
            assignCardUseCase = AssignCardUseCase(cardRepository),
            deleteCardUseCase = DeleteCardUseCase(cardRepository)
        )

        val historyViewModel = HistoryViewModel(
            getTransactionsUseCase = GetTransactionsUseCase(transactionRepository)
        )

        val profileViewModel = ProfileViewModel(
            sendMessageUseCase = SendMessageUseCase(supportRepository)
        )

        // Tworzenie głównego AppViewModel
        viewModel = AppViewModel(
            authViewModel = authViewModel,
            balanceViewModel = balanceViewModel,
            cardsViewModel = cardsViewModel,
            historyViewModel = historyViewModel,
            profileViewModel = profileViewModel
        )
    }

    @Test
    fun `initial state should be empty`() = runTest {
        val state = viewModel.uiState.value
        assertFalse(state.isLoggedIn)
        assertTrue(state.balances.isEmpty())
        assertTrue(state.cards.isEmpty())
    }

    @Test
    fun `login should update state and fetch data`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password"

        // When
        viewModel.handleEmailPasswordSignIn(email, password)
        // Coroutines in tests need to process pending tasks.
        // With StandardTestDispatcher/runTest, we might need to advance time or rely on runTest waiting.
        // viewModel uses viewModelScope which defaults to Main Dispatcher,
        // which we mocked in BaseTest.

        // Then
        // Allow coroutines to complete
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isLoggedIn)
        assertFalse(state.isRefreshing)

        // Check if data was refreshed (cards and balances populated)
        assertEquals(1, state.balances.size)
        assertEquals(2, state.cards.size)
        assertEquals("Pub Testowy", state.balances[0].premisesName)
    }

    @Test
    fun `failed login should set error message`() = runTest {
        // Given
        authRepository.shouldFail = true
        authRepository.failureMessage = "Wrong password"

        // When
        viewModel.handleEmailPasswordSignIn("a", "b")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoggedIn)
        assertEquals("Wrong password", state.errorMessage)
    }

    @Test
    fun `toggle card status should update card in list`() = runTest {
        // Given - Logged in state with cards
        viewModel.handleEmailPasswordSignIn("test", "pass")
        testDispatcher.scheduler.advanceUntilIdle()

        val initialCard = viewModel.uiState.value.cards.first { it.id == "card-1" }
        assertTrue(initialCard.isActive) // Default in Fake

        // When
        viewModel.onToggleCardStatus("card-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val updatedCard = viewModel.uiState.value.cards.first { it.id == "card-1" }
        assertFalse(updatedCard.isActive)
    }
}
