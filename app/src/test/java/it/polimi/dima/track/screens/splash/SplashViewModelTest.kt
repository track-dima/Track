package it.polimi.dima.track.screens.splash

import com.google.firebase.auth.FirebaseAuthException
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.polimi.dima.track.SPLASH_SCREEN
import it.polimi.dima.track.TRAININGS_SCREEN
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.ConfigurationService
import it.polimi.dima.track.model.service.LogService
import junit.framework.TestCase.assertTrue
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

@ExperimentalCoroutinesApi
class SplashViewModelTest {

  private lateinit var viewModel: SplashViewModel
  private lateinit var mockConfigurationService: ConfigurationService
  private lateinit var mockAccountService: AccountService
  private lateinit var mockLogService: LogService

  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)

    mockConfigurationService = mockk(relaxed = true)
    mockAccountService = mockk(relaxed = true)
    mockLogService = mockk(relaxed = true)
    viewModel = SplashViewModel(mockConfigurationService, mockAccountService, mockLogService)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    mainThreadSurrogate.close()
  }

  @Test
  fun onAppStart_hasUser() {
    // Given
    val mockOpenAndPopUp: (String, String) -> Unit = mockk(relaxed = true)
    every { mockAccountService.hasUser } returns true

    // When
    viewModel.onAppStart(mockOpenAndPopUp)

    // Then
    verify { mockOpenAndPopUp.invoke(TRAININGS_SCREEN, SPLASH_SCREEN) }
  }

  @Test
  fun createAnonymousAccount_success() = runTest {
    // Given
    val mockOpenAndPopUp: (String, String) -> Unit = mockk(relaxed = true)
    coEvery { mockAccountService.createAnonymousAccount() } returns Unit

    // When
    viewModel.createAnonymousAccount(mockOpenAndPopUp)

    // Then
    coVerify {
      mockOpenAndPopUp.invoke(TRAININGS_SCREEN, SPLASH_SCREEN)
    }
  }

  @Test
  fun createAnonymousAccount_failure() = runTest {
    // Given
    val mockOpenAndPopUp: (String, String) -> Unit = mockk(relaxed = true)
    val mockException: FirebaseAuthException = mockk(relaxed = true)
    coEvery { mockAccountService.createAnonymousAccount() } throws mockException

    // When
    viewModel.createAnonymousAccount(mockOpenAndPopUp)

    verify { mockOpenAndPopUp wasNot Called }
    assertTrue(viewModel.showError.value)
  }
}
