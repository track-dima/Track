package it.polimi.dima.track.screens.edit_repetitions

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.TRAINING_DEFAULT_ID
import it.polimi.dima.track.common.ext.calculateSearchTokens
import it.polimi.dima.track.common.ext.idFromParameter
import it.polimi.dima.track.common.ext.parseTraining
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

  fun onAddClick(hierarchy: List<String>): TrainingStep {
    val trainingStep = TrainingStep(
      id = UUID.randomUUID().toString(),
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 200,
      recover = true,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 180,
    )

    trainingSteps.value = onAddClickHelper(hierarchy, trainingSteps.value, trainingStep)
    return trainingStep
  }

  private fun onAddClickHelper(
    hierarchy: List<String>,
    trainingSteps: List<TrainingStep>,
    trainingStep: TrainingStep
  ): List<TrainingStep> {
    return if (hierarchy.isEmpty()) {
      trainingSteps + trainingStep
    } else {
      trainingSteps.map {
        if (it.id == hierarchy.first()) {
          it.copy(
            stepsInRepetition = onAddClickHelper(
              hierarchy.drop(1),
              it.stepsInRepetition,
              trainingStep
            )
          )
        } else {
          it
        }
      }
    }
  }

  fun onAddBlockClick(hierarchy: List<String>, repetitions: Int) {
    trainingSteps.value = onAddBlockClickHelper(hierarchy, trainingSteps.value, repetitions)
  }

  private fun onAddBlockClickHelper(
    hierarchy: List<String>,
    trainingSteps: List<TrainingStep>,
    repetitions: Int
  ): List<TrainingStep> {
    val repetitionBlock = TrainingStep(
      id = UUID.randomUUID().toString(),
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = repetitions,
      recover = true,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 180,
      extraRecoverType = TrainingStep.DurationType.TIME,
      extraRecoverDuration = 300,
    )

    return if (hierarchy.isEmpty()) {
      trainingSteps + repetitionBlock
    } else {
      trainingSteps.map {
        if (it.id == hierarchy.first()) {
          it.copy(
            stepsInRepetition = onAddBlockClickHelper(
              hierarchy.drop(1),
              it.stepsInRepetition,
              repetitions
            )
          )
        } else {
          it
        }
      }
    }
  }

  fun onDeleteClick(hierarchy: List<String>, stepId: String) {
    trainingSteps.value =
      onDeleteClickHelper(hierarchy, trainingSteps.value.toMutableList(), stepId)
  }

  private fun onDeleteClickHelper(
    hierarchy: List<String>,
    trainingSteps: MutableList<TrainingStep>,
    stepId: String
  ): List<TrainingStep> {
    return if (hierarchy.isEmpty()) {
      trainingSteps.apply {
        val index = indexOfFirst { it.id == stepId }
        removeAt(index)
      }
      trainingSteps
    } else {
      trainingSteps.map {
        if (it.id == hierarchy.first()) {
          it.copy(
            stepsInRepetition = onDeleteClickHelper(
              hierarchy.drop(1),
              it.stepsInRepetition.toMutableList(),
              stepId
            )
          )
        } else {
          it
        }
      }
    }
  }

  fun onEditClick(hierarchy: List<String>, trainingStep: TrainingStep) {
    trainingSteps.value =
      onEditClickHelper(hierarchy, trainingSteps.value.toMutableList(), trainingStep)
  }

  private fun onEditClickHelper(
    hierarchy: List<String>,
    trainingSteps: MutableList<TrainingStep>,
    trainingStep: TrainingStep
  ): List<TrainingStep> {
    return if (hierarchy.isEmpty()) {
      trainingSteps.apply {
        val index = indexOfFirst { it.id == trainingStep.id }
        set(index, trainingStep)
      }
    } else {
      trainingSteps.map {
        if (it.id == hierarchy.first()) {
          it.copy(
            stepsInRepetition = onEditClickHelper(
              hierarchy.drop(1),
              it.stepsInRepetition.toMutableList(),
              trainingStep
            )
          )
        } else {
          it
        }
      }
    }
  }

  fun moveStep(hierarchy: List<String>, from: ItemPosition, to: ItemPosition) {
    trainingSteps.value = moveStepHelper(hierarchy, trainingSteps.value.toMutableList(), from, to)
  }

  private fun moveStepHelper(
    hierarchy: List<String>,
    trainingSteps: MutableList<TrainingStep>,
    from: ItemPosition,
    to: ItemPosition
  ): List<TrainingStep> {
    return if (hierarchy.isEmpty()) {
      trainingSteps.apply {
        add(to.index, removeAt(from.index))
      }
    } else {
      trainingSteps.map {
        if (it.id == hierarchy.first()) {
          it.copy(
            stepsInRepetition = moveStepHelper(
              hierarchy.drop(1),
              it.stepsInRepetition.toMutableList(),
              from,
              to
            )
          )
        } else {
          it
        }
      }
    }
  }

  fun onEditRepetitionsClick(hierarchy: List<String>, repetitions: Int) {
    trainingSteps.value = onEditRepetitionsClickHelper(hierarchy, trainingSteps.value, repetitions)
  }

  private fun onEditRepetitionsClickHelper(
    hierarchy: List<String>,
    trainingSteps: List<TrainingStep>,
    repetitions: Int
  ): List<TrainingStep> {
    return if (hierarchy.size == 1) {
      trainingSteps.map {
        if (it.id == hierarchy.first()) {
          it.copy(repetitions = repetitions)
        } else {
          it
        }
      }
    } else {
      trainingSteps.map {
        if (it.id == hierarchy.first()) {
          it.copy(
            stepsInRepetition = onEditRepetitionsClickHelper(
              hierarchy.drop(1),
              it.stepsInRepetition,
              repetitions
            )
          )
        } else {
          it
        }
      }
    }
  }

  fun onEditRecoverClick(
    hierarchy: List<String>,
    recoverType: String,
    recoverDuration: Int,
    recoverDistance: Int,
    recoverDistanceUnit: String,
    extraRecover: Boolean,
  ) {
    trainingSteps.value = onEditRecoverClickHelper(
      hierarchy,
      trainingSteps.value,
      recoverType,
      recoverDuration,
      recoverDistance,
      recoverDistanceUnit,
      extraRecover
    )
  }

  private fun onEditRecoverClickHelper(
    hierarchy: List<String>,
    trainingSteps: List<TrainingStep>,
    recoverType: String,
    recoverDuration: Int,
    recoverDistance: Int,
    recoverDistanceUnit: String,
    extraRecover: Boolean,
  ): List<TrainingStep> {
    return if (hierarchy.size == 1) {
      trainingSteps.map {
        if (it.id == hierarchy.first()) {
          if (extraRecover) {
            it.copy(
              extraRecoverType = recoverType,
              extraRecoverDuration = recoverDuration,
              extraRecoverDistance = recoverDistance,
              extraRecoverDistanceUnit = recoverDistanceUnit,
            )
          } else {
            it.copy(
              recoverType = recoverType,
              recoverDuration = recoverDuration,
              recoverDistance = recoverDistance,
              recoverDistanceUnit = recoverDistanceUnit,
            )
          }
        } else {
          it
        }
      }
    } else {
      trainingSteps.map {
        if (it.id == hierarchy.first()) {
          it.copy(
            stepsInRepetition = onEditRecoverClickHelper(
              hierarchy.drop(1),
              it.stepsInRepetition,
              recoverType,
              recoverDuration,
              recoverDistance,
              recoverDistanceUnit,
              extraRecover
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
      training.value = training.value.copy(
        trainingSteps = trainingSteps.value,
        searchable = training.value.calculateSearchTokens()
      )
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
