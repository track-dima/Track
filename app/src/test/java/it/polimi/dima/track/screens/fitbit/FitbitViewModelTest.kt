package it.polimi.dima.track.screens.fitbit

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.fitbit.FitbitAuthManager
import it.polimi.dima.track.model.service.fitbit.FitbitOAuthToken
import it.polimi.dima.track.model.service.storage.UserStorageService
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
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FitbitViewModelTest {
  private lateinit var viewModel: FitbitViewModel
  private lateinit var mockFitbitAuthManager: FitbitAuthManager
  private lateinit var mockUserStorageService: UserStorageService
  private lateinit var mockLogService: LogService

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)

    mockFitbitAuthManager = mockk(relaxed = true)
    mockUserStorageService = mockk(relaxed = true)
    mockLogService = mockk(relaxed = true)

    viewModel = FitbitViewModel(mockFitbitAuthManager, mockUserStorageService, mockLogService)
  }

  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    mainThreadSurrogate.close()
  }

  @Test
  fun initialize() = runTest {
    // Given
    val authenticationCode = "12345678901234567890123456789012"

    // Mock behavior of exchangeCodeForToken
    coEvery { mockFitbitAuthManager.exchangeCodeForToken(any()) } returns FitbitOAuthToken()
    // Mock behavior of updateFitbitToken
    coEvery { mockUserStorageService.updateFitbitToken(any()) } returns Unit

    // When
    viewModel.initialize(authenticationCode)

    // Then
    // Authentication code exchange is requested
    coVerify { mockFitbitAuthManager.exchangeCodeForToken(authenticationCode) }

    // Storage is updated
    coVerify { mockUserStorageService.updateFitbitToken(FitbitOAuthToken()) }
  }

  @Test
  fun initialize_badAuthenticationCode() = runTest {
    // Given
    val authenticationCode = "an_invalid_authentication_code"

    // Mock behavior of exchangeCodeForToken
    coEvery { mockFitbitAuthManager.exchangeCodeForToken(any()) } throws IOException()

    // When
    viewModel.initialize(authenticationCode)

    // Then
    // Ensure the method has been called
    coVerify { mockFitbitAuthManager.exchangeCodeForToken(authenticationCode) }
    // And the exception has been handled correctly
  }
}