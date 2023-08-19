package it.polimi.dima.track.screens.training

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.TRAINING_DEFAULT_ID
import it.polimi.dima.track.TRAINING_SCREEN
import it.polimi.dima.track.common.ext.emptyResults
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.screens.TrackViewModel
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
  logService: LogService,
  private val trainingStorageService: TrainingStorageService,
) : TrackViewModel(logService) {
  val training = mutableStateOf(Training())
  val options = mutableStateOf<List<String>>(listOf())

  fun initialize(trainingId: String) {
    launchCatching {
      if (trainingId != TRAINING_DEFAULT_ID) {
        training.value = trainingStorageService.getTraining(trainingId) ?: Training()
      }
    }
  }

  fun loadTaskOptions() {
    options.value = TrainingActionOption.getOptions(TRAINING_SCREEN)
  }

  fun onFavoriteClick(favorite: Boolean) {
    training.value = training.value.copy(favorite = favorite)
    launchCatching {
      trainingStorageService.updateTraining(training.value)
    }
  }

  fun onDeleteTaskClick(training: Training, popUpScreen: () -> Unit) {
    launchCatching {
      trainingStorageService.deleteTraining(training.id)
      popUpScreen()
    }
  }

  fun onDuplicateTrainingClick(
    training: Training,
    popUpScreen: () -> Unit,
    editTraining: (Training) -> Unit
  ) {
    launchCatching {
      val newId = trainingStorageService.duplicateTraining(
        training.copy(
          transient = true,
          favorite = false,
          personalBest = false,
          trainingSteps = emptyResults(training.trainingSteps)
        )
      )
      popUpScreen()
      editTraining(Training(id = newId))
    }
  }
}
