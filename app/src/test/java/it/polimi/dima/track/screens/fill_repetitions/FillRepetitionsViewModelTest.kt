package it.polimi.dima.track.screens.fill_repetitions

import androidx.lifecycle.SavedStateHandle
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
  fun onDoneClick() = runTest {
    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } returns Unit
    fillRepetitionsViewModel.onDoneClick(popUpScreen)
    coVerify { popUpScreen() }
  }

  @Test
  fun onCancelClick() {
    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } returns Unit
    fillRepetitionsViewModel.onCancelClick(popUpScreen)
    verify { popUpScreen() }
  }
}