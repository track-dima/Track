package it.polimi.dima.track.screens.login

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import it.polimi.dima.track.common.snackbar.SnackBarManager
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.LogService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

  private lateinit var viewModel: LoginViewModel
  private lateinit var mockAccountService: AccountService
  private lateinit var mockLogService: LogService
  private lateinit var mockSnackBarManager: SnackBarManager


  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)

    mockAccountService = mockk()
    mockLogService = mockk()
    viewModel = LoginViewModel(mockAccountService, mockLogService)
    mockSnackBarManager = mockk(relaxed = true)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    mainThreadSurrogate.close()
  }

  @Test
  fun onEmailChange() {
    val viewModel = LoginViewModel(mockAccountService, mockLogService)

    viewModel.onEmailChange("test@example.com")

    assertEquals("test@example.com", viewModel.uiState.value.email)
  }

  @Test
  fun onPasswordChange() {
    val viewModel = LoginViewModel(mockAccountService, mockLogService)

    viewModel.onPasswordChange("P@ssw0rd")

    assertEquals("P@ssw0rd", viewModel.uiState.value.password)
  }

  @Test
  fun onSignInClick_validEmail_notBlankPassword() = runTest {
    // Given
    val viewModel = LoginViewModel(mockAccountService, mockLogService)
    viewModel.onEmailChange("test@example.com")
    viewModel.onPasswordChange("password123")

    // Mock behavior of accountService.authenticate
    coEvery { mockAccountService.authenticate(any(), any()) } returns Unit

    // When
    viewModel.onSignInClick { _, _ -> }

    // Authenticate is called
    coVerify { mockAccountService.authenticate("test@example.com", "password123") }
  }

  @Test
  fun onSignInClick_invalidEmail() = runTest {
    // Given
    val viewModel = LoginViewModel(mockAccountService, mockLogService)
    viewModel.onEmailChange("test")
    viewModel.onPasswordChange("password123")

    // When
    viewModel.onSignInClick { _, _ -> }

    // Then
    coVerify(exactly = 0) { mockAccountService.authenticate(any(), any()) }
  }

  @Test
  fun onSignInClick_blankPassword() = runTest {
    // Given
    val viewModel = LoginViewModel(mockAccountService, mockLogService)
    viewModel.onEmailChange("test@example.com")
    viewModel.onPasswordChange("")

    // When
    viewModel.onSignInClick { _, _ -> }

    // Then
    coVerify(exactly = 0) { mockAccountService.authenticate(any(), any()) }
  }

  @Test
  fun onForgotPasswordClick_validEmail() = runTest {
    // Given
    val viewModel = LoginViewModel(mockAccountService, mockLogService)
    viewModel.onEmailChange("test@example.com")

    // Mock behavior of accountService.sendRecoveryEmail
    coEvery { mockAccountService.sendRecoveryEmail(any()) } returns Unit

    // When
    viewModel.onForgotPasswordClick()

    // Then
    coVerify { mockAccountService.sendRecoveryEmail("test@example.com") }
  }

  @Test
  fun onForgotPasswordClick_invalidEmail() = runTest {
    // Given
    val viewModel = LoginViewModel(mockAccountService, mockLogService)
    viewModel.onEmailChange("test")

    // When
    viewModel.onForgotPasswordClick()

    // Then
    coVerify(exactly = 0) { mockAccountService.sendRecoveryEmail(any()) }
  }
}

