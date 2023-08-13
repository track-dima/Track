package it.polimi.dima.track.screens.fill_repetitions

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
class FillRepetitionsViewModel @Inject constructor(
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

  fun onTimeFillClick(hierarchy: List<String>, index: Int, trainingStepId: String, result: String) {
    trainingSteps.value =
      onTimeFillClickHelper(hierarchy, trainingSteps.value.toMutableList(), index, trainingStepId, result)
  }

  private fun onTimeFillClickHelper(
    hierarchy: List<String>,
    trainingSteps: MutableList<TrainingStep>,
    index: Int,
    trainingStepId: String,
    result: String
  ): List<TrainingStep> {
    return if (hierarchy.isEmpty()) {
      trainingSteps.apply {
        val trainingIndex = indexOfFirst { it.id == trainingStepId }
        val trainingStep = get(trainingIndex)
        val results = trainingStep.results.toMutableList()
        if (results.size > index) {
          results[index] = result
        } else {
          results.addAll(List(index - results.size) { "" })
          results.add(result)
        }
        set(trainingIndex, trainingStep.copy(results = results))
      }
    } else {
      trainingSteps.map {
        if (it.id == hierarchy.first()) {
          it.copy(
            stepsInRepetition = onTimeFillClickHelper(
              hierarchy.drop(1),
              it.stepsInRepetition.toMutableList(),
              index,
              trainingStepId,
              result
            )
          )
        } else {
          it
        }
      }
    }
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
