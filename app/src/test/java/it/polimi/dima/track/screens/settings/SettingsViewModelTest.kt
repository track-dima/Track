package it.polimi.dima.track.screens.settings

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import it.polimi.dima.track.LOGIN_SCREEN
import it.polimi.dima.track.SIGN_UP_SCREEN
import it.polimi.dima.track.model.User
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.fitbit.FitbitAuthManager
import it.polimi.dima.track.model.service.storage.UserStorageService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
  private lateinit var settingsViewModel: SettingsViewModel

  private lateinit var accountService: AccountService
  private lateinit var fitbitAuthManager: FitbitAuthManager

  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)

    val userStorageService: UserStorageService = mockk()
    accountService = mockk()
    fitbitAuthManager = mockk()

    every { userStorageService.user } returns flowOf(User())

    settingsViewModel = SettingsViewModel(
      mockk(),
      userStorageService,
      accountService,
      fitbitAuthManager,
    )
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    mainThreadSurrogate.close()
  }

  @Test
  fun onLoginClick() {
    val openScreen: (String) -> Unit = mockk()
    every { openScreen(any()) } returns Unit
    settingsViewModel.onLoginClick(openScreen)
    verify { openScreen(LOGIN_SCREEN) }
  }

  @Test
  fun onSignUpClick() {
    val openScreen: (String) -> Unit = mockk()
    every { openScreen(any()) } returns Unit
    settingsViewModel.onSignUpClick(openScreen)
    verify { openScreen(SIGN_UP_SCREEN) }
  }

  @Test
  fun onSignOutClick() = runTest {
    coEvery { accountService.signOut() } just Runs
    settingsViewModel.onSignOutClick()
    coVerify { accountService.signOut() }
  }

  @Test
  fun onDeleteMyAccountClick() = runTest {
    val restartApp: (String) -> Unit = mockk()
    every { restartApp(any()) } returns Unit
    coEvery { accountService.deleteAccount() } returns Unit
    settingsViewModel.onDeleteMyAccountClick(restartApp)
    coVerify { accountService.deleteAccount() }
  }
}