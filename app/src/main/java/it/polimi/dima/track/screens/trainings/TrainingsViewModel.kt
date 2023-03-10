package it.polimi.dima.track.screens.trainings

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.EDIT_TRAINING_SCREEN
import it.polimi.dima.track.SETTINGS_SCREEN
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.ConfigurationService
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.StorageService
import it.polimi.dima.track.screens.TrackViewModel
import javax.inject.Inject

@HiltViewModel
class TrainingsViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
  private val configurationService: ConfigurationService
) : TrackViewModel(logService) {
  val options = mutableStateOf<List<String>>(listOf())

  val trainings = storageService.trainings

  fun loadTaskOptions() {
    val hasEditOption = configurationService.isShowTrainingEditButtonConfig
    options.value = TrainingActionOption.getOptions(hasEditOption)
  }

  fun onTrainingCheckChange(training: Training) {
    launchCatching { storageService.update(training.copy(completed = !training.completed)) }
  }

  fun onAddClick(openScreen: (String) -> Unit) = openScreen(EDIT_TRAINING_SCREEN)

  fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

  fun onTrainingActionClick(openScreen: (String) -> Unit, training: Training, action: String) {
    when (TrainingActionOption.getByTitle(action)) {
      TrainingActionOption.EditTraining -> openScreen("$EDIT_TRAINING_SCREEN?$TRAINING_ID={${training.id}}")
      TrainingActionOption.ToggleFlag -> onFlagTrainingClick(training)
      TrainingActionOption.DeleteTask -> onDeleteTaskClick(training)
    }
  }

  private fun onFlagTrainingClick(training: Training) {
    launchCatching { storageService.update(training.copy(flag = !training.flag)) }
  }

  private fun onDeleteTaskClick(training: Training) {
    launchCatching { storageService.delete(training.id) }
  }
}
