package it.polimi.dima.track.screens.edit_repetitions

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.TRAINING_DEFAULT_ID
import it.polimi.dima.track.common.ext.idFromParameter
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.TrainingStep
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.StorageService
import it.polimi.dima.track.screens.TrackViewModel
import javax.inject.Inject

@HiltViewModel
class EditRepetitionsViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
) : TrackViewModel(logService) {
  val training = mutableStateOf(Training())

  fun initialize(trainingId: String) {
    launchCatching {
      if (trainingId != TRAINING_DEFAULT_ID) {
        training.value = storageService.getTraining(trainingId.idFromParameter()) ?: Training()
      }
    }
  }

  fun onAddClick() {
    val trainingStep = TrainingStep(distance = 1000, duration = 60)
    training.value = training.value.copy(trainingSteps = training.value.trainingSteps + trainingStep)
  }

  fun onDeleteClick(trainingStep: TrainingStep) {
    val trainingSteps = training.value.trainingSteps.toMutableList();
    trainingSteps.remove(trainingStep)
    training.value = training.value.copy(trainingSteps = trainingSteps)
  }

  fun onEditClick(trainingStep: TrainingStep) {

  }

  fun onDoneClick(popUpScreen: () -> Unit) {
    launchCatching {
      val editedTraining = training.value
      if (editedTraining.id.isBlank()) {
        storageService.save(editedTraining)
      } else {
        storageService.update(editedTraining)
      }
      popUpScreen()
    }
  }

  fun onCancelClick(popUpScreen: () -> Unit) {
    popUpScreen()
  }
}
