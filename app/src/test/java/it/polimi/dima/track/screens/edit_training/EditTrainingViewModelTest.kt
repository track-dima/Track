package it.polimi.dima.track.screens.edit_training

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.polimi.dima.track.EDIT_REPETITIONS_SCREEN
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.TrainingStep
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.burnoutcrew.reorderable.ItemPosition
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalCoroutinesApi::class)
class EditTrainingViewModelTest {
  private lateinit var editTrainingViewModel: EditTrainingViewModel
  private lateinit var trainingStorageService: TrainingStorageService
  private lateinit var savedStateHandle: SavedStateHandle

  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)

    trainingStorageService = mockk()
    coEvery { trainingStorageService.getTraining(any()) } returns Training()

    savedStateHandle = mockk()
    every { savedStateHandle.get<String?>(any()) } returns null

    editTrainingViewModel = EditTrainingViewModel(
      logService = mockk(),
      trainingStorageService = trainingStorageService,
      savedStateHandle = savedStateHandle,
    )
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    mainThreadSurrogate.close()
  }

  @Test
  fun onTitleChange() {
    val newTitle = "New title"
    editTrainingViewModel.onTitleChange(newTitle)
    assertEquals(newTitle, editTrainingViewModel.training.value.title)
  }

  @Test
  fun onDescriptionChange() {
    val newDescription = "New description"
    editTrainingViewModel.onDescriptionChange(newDescription)
    assertEquals(newDescription, editTrainingViewModel.training.value.description)
  }

  @Test
  fun onNotesChange() {
    val newNotes = "New notes"
    editTrainingViewModel.onNotesChange(newNotes)
    assertEquals(newNotes, editTrainingViewModel.training.value.notes)
  }

  @Test
  fun onDateChange() {
    val newDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    editTrainingViewModel.onDateChange(newDate.timeInMillis)
    assertEquals(newDate.time, editTrainingViewModel.training.value.dueDate)
  }

  @Test
  fun onTimeChange() {
    val hour = 12
    val minute = 34
    editTrainingViewModel.onTimeChange(hour, minute)
    assertEquals(
      mapOf("hour" to hour, "minute" to minute),
      editTrainingViewModel.training.value.dueTime
    )
  }

  @Test
  fun onFavoriteToggle_Yes() {
    editTrainingViewModel.onFavoriteToggle("Yes")
    assertEquals(true, editTrainingViewModel.training.value.favorite)
  }

  @Test
  fun onFavoriteToggle_No() {
    editTrainingViewModel.onFavoriteToggle("No")
    assertEquals(false, editTrainingViewModel.training.value.favorite)
  }

  @Test
  fun onFavoriteToggle_Invalid() {
    editTrainingViewModel.onFavoriteToggle("Invalid")
    assertEquals(false, editTrainingViewModel.training.value.favorite)
  }

  @Test
  fun onTypeChange() {
    val newType = "New type"
    editTrainingViewModel.onTypeChange(newType)
    assertEquals(newType, editTrainingViewModel.training.value.type)
  }

  @Test
  fun onEditSteps() {
    val openScreen: (String) -> Unit = mockk()
    every { openScreen(any()) } returns Unit

    editTrainingViewModel.onEditSteps(openScreen)
    verify { openScreen("$EDIT_REPETITIONS_SCREEN?$TRAINING_ID=${editTrainingViewModel.training.value.id}") }
  }

  @Test
  fun onDoneClick_NewTraining() = runTest {
    val training = Training()
    editTrainingViewModel.training.value = training

    coEvery { trainingStorageService.saveTraining(any()) } returns ""

    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } returns Unit

    editTrainingViewModel.onDoneClick(popUpScreen)
    coVerifySequence { trainingStorageService.saveTraining(training); popUpScreen() }
  }

  @Test
  fun onDoneClick_ExistingTraining() = runTest {
    val training = Training(id = "trainingId")
    editTrainingViewModel.training.value = training

    coEvery { trainingStorageService.updateTraining(any()) } returns Unit

    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } returns Unit

    editTrainingViewModel.onDoneClick(popUpScreen)
    coVerifySequence { trainingStorageService.updateTraining(any()); popUpScreen() }
  }

  @Test
  fun onCancelClick() {
    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } returns Unit
    editTrainingViewModel.onCancelClick(popUpScreen)
    verify { popUpScreen() }
  }

  @Test
  fun onAddStepClick_EmptyHierarchy() {
    val hierarchy = emptyList<String>()
    editTrainingViewModel.onAddStepClick(hierarchy)
  }

  @Test
  fun onAddStepClick_NonEmptyHierarchy() {
    val hierarchy = listOf("step")
    editTrainingViewModel.onAddStepClick(hierarchy)
  }

  @Test
  fun onAddBlockClick_EmptyHierarchyOneRepetition() {
    editTrainingViewModel.onAddBlockClick(emptyList(), 1)
  }

  @Test
  fun onAddBlockClick_NonEmptyHierarchyOneRepetition() {
    editTrainingViewModel.onAddBlockClick(listOf("step"), 1)
  }

  @Test
  fun onAddBlockClick_EmptyHierarchyZeroRepetitions() {
    editTrainingViewModel.onAddBlockClick(emptyList(), 0)
  }

  @Test
  fun onAddBlockClick_NonEmptyHierarchyZeroRepetitions() {
    editTrainingViewModel.onAddBlockClick(listOf("step"), 0)
  }

  @Test
  fun deleteStep() {
    val hierarchy = listOf("step1", "step2")

    editTrainingViewModel.deleteStep(hierarchy, "step2")
  }

  @Test
  fun editStep() {
    editTrainingViewModel.editStep(listOf("step1", "step2"), TrainingStep())
  }

  @Test
  fun moveStep() {
    editTrainingViewModel.moveStep(listOf("step1", "step2"), ItemPosition(1, 1), ItemPosition(2, 2))
  }

  @Test
  fun onEditRepetitions() {
    editTrainingViewModel.onEditRepetitions(listOf("step1", "step2"), 2)
  }

  @Test
  fun onEditRecover() {
    editTrainingViewModel.onEditRecover(
      listOf("step1", "step2"),
      "recoverType",
      123,
      123,
      "unit",
      true
    )
  }

  @Test
  fun onSaveStepsClick() {
    val training = Training()
    val trainingSteps = listOf(TrainingStep())

    editTrainingViewModel.training.value = training
    editTrainingViewModel.trainingSteps.value = trainingSteps

    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } returns Unit
    editTrainingViewModel.onSaveStepsClick(popUpScreen)

    assertEquals(training.copy(trainingSteps = trainingSteps), editTrainingViewModel.training.value)
    verify { popUpScreen() }
  }

  @Test
  fun onDiscardStepsClick() {
    val popUpScreen: () -> Unit = mockk()
    every { popUpScreen() } returns Unit

    editTrainingViewModel.onDiscardStepsClick(popUpScreen)
    verify { popUpScreen() }
  }
}