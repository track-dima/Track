package it.polimi.dima.track.screens.edit_training

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.EDIT_MODE_DEFAULT
import it.polimi.dima.track.EDIT_MODE_EDIT
import it.polimi.dima.track.EDIT_REPETITIONS_SCREEN
import it.polimi.dima.track.R
import it.polimi.dima.track.TRAINING_DEFAULT_ID
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.common.ext.calculateSearchTokens
import it.polimi.dima.track.common.ext.emptyResults
import it.polimi.dima.track.common.ext.toClockPattern
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.TrainingStep
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.screens.TrackViewModel
import org.burnoutcrew.reorderable.ItemPosition
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditTrainingViewModel @Inject constructor(
  logService: LogService,
  private val trainingStorageService: TrainingStorageService,
  savedStateHandle: SavedStateHandle
) : TrackViewModel(logService) {
  private val trainingId: String = savedStateHandle["trainingId"] ?: TRAINING_DEFAULT_ID
  private val editMode: String = savedStateHandle["editMode"] ?: EDIT_MODE_DEFAULT

  val training = mutableStateOf(Training())
  val trainingSteps = mutableStateOf(listOf<TrainingStep>())
  val titleResource = mutableStateOf(R.string.edit_training)

  init {
    launchCatching {
      if (trainingId != TRAINING_DEFAULT_ID) {
        val storageTraining = trainingStorageService.getTraining(trainingId) ?: Training()
        training.value = if (editMode == EDIT_MODE_EDIT) {
          storageTraining
        } else {
          titleResource.value = R.string.duplicate_training
          // Duplicate training
          storageTraining.copy(
            id = "",
            favorite = false,
            personalBest = false,
            trainingSteps = emptyResults(storageTraining.trainingSteps)
          )
        }
        trainingSteps.value = training.value.trainingSteps
      }
      else {
        titleResource.value = R.string.new_training
      }
    }
  }

  fun onTitleChange(newValue: String) {
    training.value = training.value.copy(title = newValue)
  }

  fun onDescriptionChange(newValue: String) {
    training.value = training.value.copy(description = newValue)
  }

  fun onNotesChange(newValue: String) {
    training.value = training.value.copy(notes = newValue)
  }

  fun onDateChange(newValue: Long) {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC))
    calendar.timeInMillis = newValue
    training.value = training.value.copy(
      dueDate = calendar.time,
      dueDateString = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(calendar.time)
    )
  }

  fun onTimeChange(hour: Int, minute: Int) {
    val newDueTime = "${hour.toClockPattern()}:${minute.toClockPattern()}"
    training.value = training.value.copy(
      dueTime = mapOf("hour" to hour, "minute" to minute),
      dueTimeString = newDueTime
    )
  }

  fun onFavoriteToggle(newValue: String) {
    val newFavoriteOption = EditFavouriteOption.getBooleanValue(newValue)
    training.value = training.value.copy(favorite = newFavoriteOption)
  }

  fun onTypeChange(newValue: String) {
    training.value = training.value.copy(type = newValue)
  }

  fun onEditSteps(openScreen: (String) -> Unit) {
    openScreen("$EDIT_REPETITIONS_SCREEN?$TRAINING_ID=${training.value.id}")
  }

  fun onDoneClick(popUpScreen: () -> Unit) {
    launchCatching {
      saveTraining()
      popUpScreen()
    }
  }

  private suspend fun saveTraining(): String {
    val editedTraining = training.value.copy(searchable = training.value.calculateSearchTokens())
    return if (editedTraining.id.isBlank()) {
      trainingStorageService.saveTraining(editedTraining)
    } else {
      trainingStorageService.updateTraining(editedTraining)
      editedTraining.id
    }
  }

  fun onCancelClick(popUpScreen: () -> Unit) {
    popUpScreen()
  }


  fun onAddStepClick(hierarchy: List<String>): TrainingStep {
    val trainingStep = TrainingStep(
      id = UUID.randomUUID().toString(),
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 200,
      recover = true,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 180,
    )

    trainingSteps.value = onAddStepClickHelper(hierarchy, trainingSteps.value, trainingStep)
    return trainingStep
  }

  private fun onAddStepClickHelper(
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
            stepsInRepetition = onAddStepClickHelper(
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

  fun deleteStep(hierarchy: List<String>, stepId: String) {
    trainingSteps.value =
      deleteStepHelper(hierarchy, trainingSteps.value.toMutableList(), stepId)
  }

  private fun deleteStepHelper(
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
            stepsInRepetition = deleteStepHelper(
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

  fun editStep(hierarchy: List<String>, trainingStep: TrainingStep) {
    trainingSteps.value =
      editStepHelper(hierarchy, trainingSteps.value.toMutableList(), trainingStep)
  }

  private fun editStepHelper(
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
            stepsInRepetition = editStepHelper(
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

  fun onEditRepetitions(hierarchy: List<String>, repetitions: Int) {
    trainingSteps.value = onEditRepetitionsHelper(hierarchy, trainingSteps.value, repetitions)
  }

  private fun onEditRepetitionsHelper(
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
            stepsInRepetition = onEditRepetitionsHelper(
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

  fun onEditRecover(
    hierarchy: List<String>,
    recoverType: String,
    recoverDuration: Int,
    recoverDistance: Int,
    recoverDistanceUnit: String,
    extraRecover: Boolean,
  ) {
    trainingSteps.value = onEditRecoverHelper(
      hierarchy,
      trainingSteps.value,
      recoverType,
      recoverDuration,
      recoverDistance,
      recoverDistanceUnit,
      extraRecover
    )
  }

  private fun onEditRecoverHelper(
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
            stepsInRepetition = onEditRecoverHelper(
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

  fun onSaveStepsClick(popUpScreen: () -> Unit) {
    training.value = training.value.copy(
      trainingSteps = trainingSteps.value,
    )
    popUpScreen()
  }

  fun onDiscardStepsClick(popUpScreen: () -> Unit) {
    popUpScreen()
    trainingSteps.value = training.value.trainingSteps
  }

  companion object {
    const val UTC = "UTC"
    const val DATE_FORMAT = "EEE, d MMM yyyy"
  }
}
