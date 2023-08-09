package it.polimi.dima.track.screens.agenda

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
import it.polimi.dima.track.screens.training.TrainingActionOption
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
  private val configurationService: ConfigurationService
) : TrackViewModel(logService) {
  val options = mutableStateOf<List<String>>(listOf())

  val trainings = storageService.trainings

  fun loadTaskOptions() {
    // TODO just as example of configuration service val hasEditOption = configurationService.isShowTrainingEditButtonConfig
    options.value = TrainingActionOption.getOptions(false)
  }

  fun onTrainingCheckChange(training: Training) {
    launchCatching { storageService.update(training.copy(completed = !training.completed)) }
  }

  fun onAddClick(openScreen: (String) -> Unit) = openScreen(EDIT_TRAINING_SCREEN)

  fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

  fun onTrainingActionClick(openScreen: (String) -> Unit, training: Training, action: String) {
    when (TrainingActionOption.getByTitle(action)) {
      TrainingActionOption.EditTraining -> openScreen("$EDIT_TRAINING_SCREEN?$TRAINING_ID={${training.id}}")
      TrainingActionOption.CopyTraining -> Unit
      TrainingActionOption.DuplicateTraining -> onDuplicateTrainingClick(training, openScreen)
      TrainingActionOption.ToggleFavourite -> onFavouriteTrainingClick(training)
      else -> {}
    }
  }

  private fun onFavouriteTrainingClick(training: Training) {
    launchCatching { storageService.update(training.copy(favorite = !training.favorite)) }
  }

  fun onDeleteTaskClick(trainingId: String) {
    launchCatching { storageService.delete(trainingId) }
  }

  private fun onDuplicateTrainingClick(training: Training, openScreen: (String) -> Unit) {
    launchCatching {
      val newId = storageService.duplicate(training.copy(transient = true))
      openScreen("$EDIT_TRAINING_SCREEN?$TRAINING_ID={${newId}}")
    }
  }
}
