package it.polimi.dima.track.screens.training

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.TRAINING_DEFAULT_ID
import it.polimi.dima.track.common.ext.idFromParameter
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.ConfigurationService
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.StorageService
import it.polimi.dima.track.screens.TrackViewModel
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
  private val configurationService: ConfigurationService
) : TrackViewModel(logService) {
  val training = mutableStateOf(Training())
  val options = mutableStateOf<List<String>>(listOf())

  fun loadTaskOptions() {
    val hasEditOption = configurationService.isShowTrainingEditButtonConfig
    options.value = TrainingActionOption.getOptions(hasEditOption)
  }

  fun onTrainingActionClick(training: Training, action: String, popUpScreen: () -> Unit) {
    when (TrainingActionOption.getByTitle(action)) {
      TrainingActionOption.DeleteTask -> onDeleteTaskClick(training, popUpScreen)
      else -> {}
    }
  }

  private fun onDeleteTaskClick(training: Training, popUpScreen: () -> Unit) {
    launchCatching {
      storageService.delete(training.id)
      popUpScreen()
    }
  }

  fun initialize(trainingId: String) {
    launchCatching {
      if (trainingId != TRAINING_DEFAULT_ID) {
        training.value = storageService.getTraining(trainingId.idFromParameter()) ?: Training()
      }
    }
  }
}
