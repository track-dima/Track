package it.polimi.dima.track.screens.training

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifyOrder
import it.polimi.dima.track.EDIT_MODE_DUPLICATE
import it.polimi.dima.track.TRAINING_SCREEN
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.fitbit.FitbitAuthManager
import it.polimi.dima.track.model.service.fitbit.FitbitService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.model.service.storage.UserStorageService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrainingViewModelTest {
  private lateinit var trainingViewModel: TrainingViewModel

  private lateinit var trainingStorageService: TrainingStorageService
  private lateinit var fitbitService: FitbitService
  private lateinit var userStorageService: UserStorageService
  private lateinit var fitbitAuthManager: FitbitAuthManager

  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)

    trainingStorageService = mockk()
    fitbitService = mockk()
    userStorageService = mockk()
    fitbitAuthManager = mockk()

    trainingViewModel = TrainingViewModel(
      mockk(),
      trainingStorageService,
      fitbitService,
      userStorageService,
      fitbitAuthManager,
    )
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    mainThreadSurrogate.close()
  }

  @Test
  fun initialize() {
    trainingViewModel.initialize("trainingId")
  }

  @Test
  fun loadTaskOptions() {
    trainingViewModel.loadTaskOptions()
    assertEquals(trainingViewModel.options.value, TrainingActionOption.getOptions(TRAINING_SCREEN))
  }

  @Test
  fun onFavoriteClick() {
    coEvery {  trainingStorageService.updateTraining(any()) } just Runs

    trainingViewModel.onFavoriteClick(true)
    assertTrue(trainingViewModel.training.value.favorite)

    coVerify { trainingStorageService.updateTraining(any()) }

    trainingViewModel.onFavoriteClick(false)
    assertFalse(trainingViewModel.training.value.favorite)

    coVerify { trainingStorageService.updateTraining(any()) }
  }

  @Test
  fun onDeleteTaskClick() {
    val training = Training(id = "trainingId")
    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } just Runs

    coEvery { trainingStorageService.deleteTraining(any()) } just Runs

    trainingViewModel.onDeleteTaskClick(training, popUpScreen)

    coVerifyOrder { trainingStorageService.deleteTraining(training.id); popUpScreen() }
  }

  @Test
  fun onDuplicateTrainingClick() {
    val trainingId = "trainingId"
    val popUpScreen: () -> Unit = mockk()
    val editTraining: (String, String) -> Unit = mockk()

    every { popUpScreen() } just Runs
    every { editTraining(any(), any()) } just Runs

    trainingViewModel.onDuplicateTrainingClick(trainingId, popUpScreen, editTraining)

    verifyOrder { popUpScreen(); editTraining(trainingId, EDIT_MODE_DUPLICATE) }
  }
}