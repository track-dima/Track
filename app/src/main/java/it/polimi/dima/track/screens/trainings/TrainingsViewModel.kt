package it.polimi.dima.track.screens.trainings

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.EDIT_MODE
import it.polimi.dima.track.EDIT_MODE_ARG
import it.polimi.dima.track.EDIT_MODE_NEW
import it.polimi.dima.track.EDIT_TRAINING_SCREEN
import it.polimi.dima.track.SETTINGS_SCREEN
import it.polimi.dima.track.TRAININGS_SCREEN
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.common.ext.parseTraining
import it.polimi.dima.track.common.utils.copyToClipboard
import it.polimi.dima.track.common.utils.sendIntent
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.ConfigurationService
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.screens.TrackViewModel
import it.polimi.dima.track.screens.training.TrainingActionOption
import javax.inject.Inject

@HiltViewModel
class TrainingsViewModel @Inject constructor(
  logService: LogService,
  private val trainingStorageService: TrainingStorageService,
  private val configurationService: ConfigurationService
) : TrackViewModel(logService) {
  val actions = mutableStateOf<List<String>>(listOf())
  val trainings = trainingStorageService.trainings

  fun loadTaskOptions() {
    actions.value = TrainingActionOption.getOptions(TRAININGS_SCREEN)
  }

  fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

  fun onAddClick(openScreen: (String) -> Unit) = openScreen("$EDIT_TRAINING_SCREEN?$EDIT_MODE=${EDIT_MODE_NEW}")

  fun onTrainingActionClick(
    openScreen: (String) -> Unit,
    training: Training,
    action: String,
    context: Context
  ) {
    when (TrainingActionOption.getByTitle(action)) {
      TrainingActionOption.EditTraining -> openScreen("$EDIT_TRAINING_SCREEN?$TRAINING_ID=${training.id}")
      TrainingActionOption.ShareLink -> sendIntent(
        context = context,
        text = "https://track.com/training/${training.id}"
      )

      TrainingActionOption.CopyTraining -> copyToClipboard(
        context = context,
        text = training.parseTraining(),
        label = "Training",
      )

      TrainingActionOption.ToggleFavourite -> onFavouriteTrainingClick(training)
      else -> Unit
    }
  }

  private fun onFavouriteTrainingClick(training: Training) {
    launchCatching { trainingStorageService.updateTraining(training.copy(favorite = !training.favorite)) }
  }
}
