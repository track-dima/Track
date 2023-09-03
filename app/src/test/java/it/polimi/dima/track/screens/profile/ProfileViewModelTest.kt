package it.polimi.dima.track.screens.profile

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.polimi.dima.track.SETTINGS_SCREEN
import it.polimi.dima.track.model.service.storage.PersonalBestStorageService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
  private lateinit var profileViewModel: ProfileViewModel

  private lateinit var userStorageService: UserStorageService
  private lateinit var trainingStorageService: TrainingStorageService
  private lateinit var personalBestStorageService: PersonalBestStorageService

  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)

    userStorageService = mockk()
    trainingStorageService = mockk()
    personalBestStorageService = mockk()

    every { userStorageService.user } returns mockk()
    every { trainingStorageService.trainings } returns mockk()
    every { personalBestStorageService.personalBests } returns mockk()

    profileViewModel = ProfileViewModel(
      mockk(),
      userStorageService,
      trainingStorageService,
      personalBestStorageService,
    )
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    mainThreadSurrogate.close()
  }

  @Test
  fun onSettingsClick() {
    val openScreen: (String) -> Unit = mockk()
    every { openScreen(any()) } returns Unit
    profileViewModel.onSettingsClick(openScreen)
    verify { openScreen(SETTINGS_SCREEN) }
  }

  @Test
  fun onNameChange() = runTest {
    coEvery { userStorageService.updateUserName(any()) } returns Unit
    profileViewModel.onNameChange("newName")
    coVerify { userStorageService.updateUserName(any()) }
  }

  @Test
  fun onSpecialtyChange() {
    coEvery { userStorageService.updateUserSpecialty(any()) } returns Unit
    profileViewModel.onSpecialtyChange("newSpecialty")
    coVerify { userStorageService.updateUserSpecialty("newSpecialty") }
  }
}