package it.polimi.dima.track.screens.login.trainings

import android.content.Context
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.polimi.dima.track.EDIT_MODE
import it.polimi.dima.track.EDIT_MODE_NEW
import it.polimi.dima.track.EDIT_TRAINING_SCREEN
import it.polimi.dima.track.SETTINGS_SCREEN
import it.polimi.dima.track.TRAININGS_SCREEN
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.common.ext.parseTraining
import it.polimi.dima.track.common.utils.copyToClipboard
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.ConfigurationService
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.screens.training.TrainingActionOption
import it.polimi.dima.track.screens.trainings.TrainingsViewModel
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
class TrainingsViewModelTest {

  private lateinit var viewModel: TrainingsViewModel
  private lateinit var mockLogService: LogService
  private lateinit var mockTrainingStorageService: TrainingStorageService
  private lateinit var mockConfigurationService: ConfigurationService

  @OptIn(DelicateCoroutinesApi::class)
  private val mainThreadSurrogate = newSingleThreadContext("UI thread")

  @Before
  fun setUp() {
    Dispatchers.setMain(mainThreadSurrogate)

    mockTrainingStorageService = mockk(relaxed = true)
    mockConfigurationService = mockk(relaxed = true)
    mockLogService = mockk(relaxed = true)
    viewModel = TrainingsViewModel(mockLogService, mockTrainingStorageService, mockConfigurationService)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    mainThreadSurrogate.close()
  }

  @Test
  fun loadTaskOptions() {
    // Given
    val expectedOptions = TrainingActionOption.getOptions(TRAININGS_SCREEN)

    // When
    viewModel.loadTaskOptions()

    // Then
    assert(viewModel.actions.value == expectedOptions)
  }

  @Test
  fun onSettingsClick() {
    // Given
    val mockOpenScreen: (String) -> Unit = mockk(relaxed = true)

    // When
    viewModel.onSettingsClick(mockOpenScreen)

    // Then
    verify { mockOpenScreen.invoke(SETTINGS_SCREEN) }
  }

  @Test
  fun onAddClick() {
    // Given
    val mockOpenScreen: (String) -> Unit = mockk(relaxed = true)
    val expectedScreen = "$EDIT_TRAINING_SCREEN?$EDIT_MODE=${EDIT_MODE_NEW}"

    // When
    viewModel.onAddClick(mockOpenScreen)

    // Then
    verify { mockOpenScreen.invoke(expectedScreen) }
  }

  @Test
  fun onTrainingActionClick_EditTraining() {
    // Given
    val mockOpenScreen: (String) -> Unit = mockk(relaxed = true)
    val mockContext: Context = mockk(relaxed = true)
    val training = Training(id = "1")
    val action = TrainingActionOption.EditTraining.title
    val expectedScreen = "$EDIT_TRAINING_SCREEN?$TRAINING_ID=${training.id}"

    // When
    viewModel.onTrainingActionClick(mockOpenScreen, training, action, mockContext)

    // Then
    verify { mockOpenScreen.invoke(expectedScreen) }
  }

  @Test
  fun onTrainingActionClick_CopyTraining() {
    // Given
    val mockOpenScreen: (String) -> Unit = mockk(relaxed = true)
    val mockContext: Context = mockk(relaxed = true)
    val training = Training(id = "1")
    val action = TrainingActionOption.CopyTraining.title

    // Mock the copyToClipboard function
    every {
      copyToClipboard(
        context = mockContext,
        text = training.parseTraining(),
        label = "Training"
      )
    } returns Unit

    // When
    viewModel.onTrainingActionClick(mockOpenScreen, training, action, mockContext)

    // Then
    verify {
      copyToClipboard(
        context = mockContext,
        text = training.parseTraining(),
        label = "Training"
      )
    }
  }

  @Test
  fun onTrainingActionClick_ToggleFavourite() = runTest {
    // Given
    val mockOpenScreen: (String) -> Unit = mockk(relaxed = true)
    val mockContext: Context = mockk(relaxed = true)
    val training = Training(id = "1")
    val action = TrainingActionOption.ToggleFavourite.title

    coEvery {
      mockTrainingStorageService.updateTraining(any())
    } returns Unit

    // When
    viewModel.onTrainingActionClick(mockOpenScreen, training, action, mockContext)

    // Then
    coVerify {
      mockTrainingStorageService.updateTraining(match { it.favorite != training.favorite })
    }
  }
}
