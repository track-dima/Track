package it.polimi.dima.track.screens.fill_repetitions

import androidx.lifecycle.SavedStateHandle
import io.mockk.Awaits
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import it.polimi.dima.track.model.PersonalBest
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.TrainingStep
import it.polimi.dima.track.model.service.storage.PersonalBestStorageService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FillRepetitionsViewModelTest {
  private lateinit var fillRepetitionsViewModel: FillRepetitionsViewModel

  private lateinit var trainingStorageService: TrainingStorageService
  private lateinit var personalBestStorageService: PersonalBestStorageService
  private lateinit var savedStateHandle: SavedStateHandle

  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)

    trainingStorageService = mockk()
    personalBestStorageService = mockk()
    savedStateHandle = mockk()

    every { savedStateHandle.get<String?>(any()) } returns null

    fillRepetitionsViewModel = FillRepetitionsViewModel(
      mockk(),
      trainingStorageService,
      personalBestStorageService,
      savedStateHandle,
    )
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    mainThreadSurrogate.close()
  }

  @Test
  fun onTimeFillClick_EmptyHierarchy() {
    fillRepetitionsViewModel.trainingSteps.value = listOf(
      TrainingStep(
        id = "step1",
      )
    )

    fillRepetitionsViewModel.onTimeFillClick(emptyList(), 0, "step1", "12:34")
    assertEquals("12:34", fillRepetitionsViewModel.trainingSteps.value[0].results.first())
  }

  @Test
  fun onTimeFillClick_NonEmptyHierarchy() {
    fillRepetitionsViewModel.trainingSteps.value = listOf(
      TrainingStep(
        id = "step1",
        stepsInRepetition = listOf(
          TrainingStep(
            id = "step2"
          )
        )
      )
    )

    fillRepetitionsViewModel.onTimeFillClick(
      listOf("step1"),
      0,
      "step2",
      "12:34",
    )

    assertEquals(
      "12:34",

      fillRepetitionsViewModel
        .trainingSteps
        .value[0]
        .stepsInRepetition
        .first()
        .results[0]
    )
  }

  @Test
  fun onDoneClick_NewTraining_NoPersonalBest() = runTest {
    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } just Runs

    // Given an already existing training
    fillRepetitionsViewModel.training.value = Training()
    coEvery { trainingStorageService.saveTraining(any()) } returns "newTrainingId"

    // And no global personal best
    coEvery { personalBestStorageService.existsGlobalPersonalBestWithTrainingId(any()) } returns false


    coEvery { trainingStorageService.updatePersonalBestFlag(any(), any()) } just Runs

    val step1 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:30", "1:40", "1:20")
    )
    val step2 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:25", "1:45", "1:15")
    )
    val step3 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 400,
      distanceUnit = "m",
      results = listOf("1:22", "1:42", "1:12")
    )
    val step4 = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 3,
      stepsInRepetition = listOf(step1, step2, step3)
    )
    fillRepetitionsViewModel.trainingSteps.value = listOf(step4)

    coEvery {
      personalBestStorageService.getPersonalBestFromDistanceAndTraining(
        any(),
        any()
      )
    } returns null
    coEvery {
      personalBestStorageService.getPersonalBestFromDurationAndTraining(
        any(),
        any(),
      )
    } returns null
    coEvery { personalBestStorageService.getGlobalPersonalBestFromDistance(any()) } returns null
    coEvery { personalBestStorageService.savePersonalBest(any()) } just Awaits

    fillRepetitionsViewModel.onDoneClick(popUpScreen)
    coVerify { trainingStorageService.saveTraining(any()) }
  }

  @Test
  fun onDoneClick_ExistingTraining_WithPersonalBest() = runTest {
    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } just Runs

    fillRepetitionsViewModel.training.value = Training(id = "trainingId")

    coEvery { trainingStorageService.updateTraining(any()) } just Runs
    coEvery { personalBestStorageService.existsGlobalPersonalBestWithTrainingId(any()) } returns true
    coEvery { trainingStorageService.updatePersonalBestFlag(any(), any()) } just Runs
    coEvery { personalBestStorageService.updatePersonalBest(any()) } just Runs

    val step1 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:30", "1:40", "1:20")
    )
    val step2 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:25", "1:45", "1:15")
    )
    val step3 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 400,
      distanceUnit = "m",
      results = listOf("1:22", "1:42", "1:12")
    )
    val step4 = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 3,
      stepsInRepetition = listOf(step1, step2, step3)
    )
    fillRepetitionsViewModel.trainingSteps.value = listOf(step4)

    // Personal best
    coEvery {
      personalBestStorageService.getPersonalBestFromDistanceAndTraining(
        any(),
        any()
      )
    } returns PersonalBest(
      result = "01:23:45",
      globalPersonalBest = true,
    )
    coEvery { personalBestStorageService.getGlobalPersonalBestFromDistance(any()) } returns null

    fillRepetitionsViewModel.onDoneClick(popUpScreen)
    coVerify { trainingStorageService.updateTraining(any()) }
  }

  @Test
  fun onCancelClick() {
    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } returns Unit
    fillRepetitionsViewModel.onCancelClick(popUpScreen)
    verify { popUpScreen() }
  }
}