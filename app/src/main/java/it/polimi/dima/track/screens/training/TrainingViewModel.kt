package it.polimi.dima.track.screens.training

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.TRAINING_DEFAULT_ID
import it.polimi.dima.track.common.ext.idFromParameter
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.StorageService
import it.polimi.dima.track.screens.TrackViewModel
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
) : TrackViewModel(logService) {
  val training = mutableStateOf(Training())
  val options = mutableStateOf<List<String>>(listOf())

  fun initialize(trainingId: String) {
    launchCatching {
      if (trainingId != TRAINING_DEFAULT_ID) {
        training.value = storageService.getTraining(trainingId.idFromParameter()) ?: Training()
      }
    }
  }

  fun loadTaskOptions() {
    options.value = TrainingActionOption.getOptions(true)
  }

  fun onFavoriteClick() {
    training.value = training.value.copy(favorite = !training.value.favorite)
    launchCatching {
      storageService.update(training.value)
    }
  }

  fun onTrainingActionClick(editTraining: (Training) -> Unit, popUpScreen: () -> Unit, training: Training, action: String) {
    when (TrainingActionOption.getByTitle(action)) {
      TrainingActionOption.DeleteTask -> onDeleteTaskClick(training, popUpScreen)
      TrainingActionOption.DuplicateTraining -> onDuplicateTrainingClick(training, popUpScreen, editTraining)
      else -> {}
    }
  }

  private fun onDeleteTaskClick(training: Training, popUpScreen: () -> Unit) {
    launchCatching {
      storageService.delete(training.id)
      popUpScreen()
    }
  }

  private fun onDuplicateTrainingClick(training: Training, popUpScreen: () -> Unit, editTraining: (Training) -> Unit) {
    launchCatching {
      // TODO duplicate non dovrebbe salvare finché non si conferma la modifica
      val newId = storageService.duplicate(training)
      popUpScreen()
      editTraining(Training(id = newId))
    }
  }
}
