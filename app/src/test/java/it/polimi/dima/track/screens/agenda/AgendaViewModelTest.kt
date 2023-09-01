package it.polimi.dima.track.screens.agenda

import android.content.ClipboardManager
import android.content.Context
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.polimi.dima.track.AGENDA_SCREEN
import it.polimi.dima.track.EDIT_MODE
import it.polimi.dima.track.EDIT_MODE_DUPLICATE
import it.polimi.dima.track.EDIT_TRAINING_SCREEN
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.common.ext.parseTraining
import it.polimi.dima.track.common.utils.copyToClipboard
import it.polimi.dima.track.common.utils.sendIntent
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.screens.training.TrainingActionOption
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaViewModelTest {
  private lateinit var agendaViewModel: AgendaViewModel
  private lateinit var trainingStorageService: TrainingStorageService

  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)

    trainingStorageService = mockk()
    every { trainingStorageService.trainings } returns flowOf(listOf(Training()))

    agendaViewModel = AgendaViewModel(mockk(), trainingStorageService)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    mainThreadSurrogate.close()
  }

  @Test
  fun loadTrainingOptions() {
    agendaViewModel.loadTrainingOptions()
    assertEquals(TrainingActionOption.getOptions(AGENDA_SCREEN), agendaViewModel.actions.value)
  }

  @Test
  fun onFavoriteToggle_True() {
    agendaViewModel.onFavoriteToggle(true)
    assertEquals(true, agendaViewModel.isFavoriteFilterActive)
  }

  @Test
  fun onFavoriteToggle_False() {
    agendaViewModel.onFavoriteToggle(false)
    assertEquals(false, agendaViewModel.isFavoriteFilterActive)
  }

  @Test
  fun onAddClick() {
    val openScreen: (String) -> Unit = mockk()
    every { openScreen(eq(EDIT_TRAINING_SCREEN)) } returns Unit
    every { openScreen(neq(EDIT_TRAINING_SCREEN)) } throws Exception()
    agendaViewModel.onAddClick(openScreen)
    verify { openScreen(EDIT_TRAINING_SCREEN) }
  }

  @Test
  fun onTrainingActionClick_EditTraining() {
    val openScreen: (String) -> Unit = mockk()
    val training = Training(id = "trainingId")
    every { openScreen(any()) } returns Unit
    agendaViewModel.onTrainingActionClick(
      openScreen,
      training,
      "Edit training",
      mockk(),
    )
    verify { openScreen("$EDIT_TRAINING_SCREEN?$TRAINING_ID=${training.id}") }
  }

  @Test
  fun onTrainingActionClick_ShareLink() {
    val openScreen: (String) -> Unit = mockk()
    val training = Training(id = "trainingId")
    val context: Context = mockk()

    every { openScreen(any()) } returns Unit
    every { context.startActivity(null) } returns Unit

    agendaViewModel.onTrainingActionClick(
      openScreen,
      training,
      "Share link",
      context,
    )
    verify { sendIntent(context, "https://track.com/training/${training.id}") }
  }

  @Test
  fun onTrainingActionClick_CopyTraining() {
    val openScreen: (String) -> Unit = mockk()
    val training = Training(id = "trainingId")
    val context: Context = mockk()
    val clipboardManager: ClipboardManager = mockk()

    every { openScreen(any()) } returns Unit
    every { context.getSystemService(Context.CLIPBOARD_SERVICE) } returns clipboardManager
    every { clipboardManager.setPrimaryClip(any()) } returns Unit
    every { copyToClipboard(context, any(), any()) } returns Unit

    agendaViewModel.onTrainingActionClick(
      openScreen,
      training,
      "Copy training",
      context,
    )
    verify { copyToClipboard(context, training.parseTraining(), "Training") }
  }

  @Test
  fun onTrainingActionClick_DuplicateTraining() {
    val openScreen: (String) -> Unit = mockk()
    val training = Training(id = "trainingId")
    every { openScreen(any()) } returns Unit
    agendaViewModel.onTrainingActionClick(
      openScreen,
      training,
      "Duplicate training",
      mockk(),
    )
    verify { openScreen("$EDIT_TRAINING_SCREEN?$TRAINING_ID=${training.id}&$EDIT_MODE=$EDIT_MODE_DUPLICATE") }
  }

  @Test
  fun onTrainingActionClick_ToggleFavourite() {
    agendaViewModel.onTrainingActionClick(
      { _ -> },
      Training(),
      "Toggle favourite",
      mockk(),
    )
    coVerify { trainingStorageService.updateTraining(any()) }
  }

  @Test
  fun onDeleteTaskClick() {
    val trainingId = "trainingId"
    agendaViewModel.onDeleteTaskClick(trainingId)
    coVerify { trainingStorageService.deleteTraining(trainingId) }
  }
}