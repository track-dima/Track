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
import org.burnoutcrew.reorderable.ItemPosition
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditRepetitionsViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
) : TrackViewModel(logService) {
  val training = mutableStateOf(Training())
  val trainingSteps = mutableStateOf(listOf<TrainingStep>())

  fun initialize(trainingId: String) {
    launchCatching {
      if (trainingId != TRAINING_DEFAULT_ID) {
        training.value = storageService.getTraining(trainingId.idFromParameter()) ?: Training()
        trainingSteps.value = training.value.trainingSteps
      }
    }
  }

  fun onAddClick() {
    val trainingStep = TrainingStep(id = UUID.randomUUID().toString(), distance = 1000)
    trainingSteps.value = trainingSteps.value + trainingStep
  }

  fun onDeleteClick(trainingStep: TrainingStep) {
    val trainingSteps = trainingSteps.value.toMutableList();
    trainingSteps.remove(trainingStep)
    this.trainingSteps.value = trainingSteps
  }

  fun onEditClick(trainingStep: TrainingStep) {

  }

  fun moveStep(from: ItemPosition, to: ItemPosition) {
    val trainingSteps = trainingSteps.value.toMutableList();
    trainingSteps.apply {
      add(to.index, removeAt(from.index))
    }
    this.trainingSteps.value = trainingSteps
  }

  fun onDoneClick(popUpScreen: () -> Unit) {
    launchCatching {
      training.value = training.value.copy(trainingSteps = trainingSteps.value)
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
