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
    val trainingStep = TrainingStep(
      id = UUID.randomUUID().toString(),
      type = TrainingStep.Type.REPETITIONS,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 200,
      recover = true,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 180,
    )
    trainingSteps.value = trainingSteps.value + trainingStep
  }

  fun onAddRepetitionClick(repetitions: Int) {
    val startId = UUID.randomUUID().toString()
    val repetitionBlockStart = TrainingStep(
      id = startId,
      type = TrainingStep.Type.START_REP_BLOCK,
      repetitions = repetitions,
      recover = true,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 300,
    )
    val repetitionBlockEnd = TrainingStep(
      id = UUID.randomUUID().toString(),
      type = TrainingStep.Type.END_REP_BLOCK,
      repetitionBlock = startId
    )
    trainingSteps.value = trainingSteps.value + repetitionBlockStart + repetitionBlockEnd
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
    var trainingStep = trainingSteps.removeAt(from.index)
    trainingStep = if (to.index < trainingSteps.size)
      trainingStep.copy(repetitionBlock = trainingSteps[to.index].repetitionBlock)
    else trainingStep.copy(repetitionBlock = "")
    trainingSteps.add(to.index, trainingStep)
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
