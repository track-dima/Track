package it.polimi.dima.track.screens.signup

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
class SignUpViewModelTest {

  private lateinit var viewModel: SignUpViewModel
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
    viewModel = SignUpViewModel(mockAccountService, mockLogService)
    mockSnackBarManager = mockk(relaxed = true)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    mainThreadSurrogate.close()
  }

  @Test
  fun onEmailChange() {
    val viewModel = SignUpViewModel(mockAccountService, mockLogService)

    viewModel.onEmailChange("test@example.com")

    assertEquals("test@example.com", viewModel.uiState.value.email)
  }

  @Test
  fun onPasswordChange() {
    val viewModel = SignUpViewModel(mockAccountService, mockLogService)

    viewModel.onPasswordChange("P@ssw0rd")

    assertEquals("P@ssw0rd", viewModel.uiState.value.password)
  }

  @Test
  fun onRepeatPasswordChange() {
    val viewModel = SignUpViewModel(mockAccountService, mockLogService)

    viewModel.onRepeatPasswordChange("P@ssw0rd")

    assertEquals("P@ssw0rd", viewModel.uiState.value.repeatPassword)
  }

  @Test
  fun onSignUpClick_validEmail_validPassword_matchingPassword() = runTest {
    // Given
    val viewModel = SignUpViewModel(mockAccountService, mockLogService)
    viewModel.onEmailChange("test@example.com")
    viewModel.onPasswordChange("P@ssw0rd")
    viewModel.onRepeatPasswordChange("P@ssw0rd")

    // Mock behavior of accountService.authenticate
    coEvery { mockAccountService.linkAccount(any(), any()) } returns Unit

    // When
    viewModel.onSignUpClick { _, _ -> }

    // Authenticate is called
    coVerify { mockAccountService.linkAccount("test@example.com", "P@ssw0rd") }
  }

  @Test
  fun onSignUpClick_invalidEmail() = runTest {
    // Given
    val viewModel = SignUpViewModel(mockAccountService, mockLogService)
    viewModel.onEmailChange("test")
    viewModel.onPasswordChange("P@ssw0rd")

    // When
    viewModel.onSignUpClick { _, _ -> }

    // Then
    coVerify(exactly = 0) { mockAccountService.linkAccount(any(), any()) }
  }

  @Test
  fun onSignInClick_invalidPassword() = runTest {
    // Given
    val viewModel = SignUpViewModel(mockAccountService, mockLogService)
    viewModel.onEmailChange("test@example.com")
    viewModel.onPasswordChange("password")

    // When
    viewModel.onSignUpClick { _, _ -> }

    // Then
    coVerify(exactly = 0) { mockAccountService.linkAccount(any(), any()) }
  }

  @Test
  fun onSignInClick_passwordsDoNotMatch() = runTest {
    // Given
    val viewModel = SignUpViewModel(mockAccountService, mockLogService)
    viewModel.onEmailChange("test@example.com")
    viewModel.onPasswordChange("P@ssw0rd")
    viewModel.onRepeatPasswordChange("P@ssw0rd1")

    // When
    viewModel.onSignUpClick { _, _ -> }

    // Then
    coVerify(exactly = 0) { mockAccountService.linkAccount(any(), any()) }
  }
}

