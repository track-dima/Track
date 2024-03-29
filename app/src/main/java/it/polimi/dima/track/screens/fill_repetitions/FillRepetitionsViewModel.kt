package it.polimi.dima.track.screens.fill_repetitions

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.TRAINING_DEFAULT_ID
import it.polimi.dima.track.common.ext.getBestResults
import it.polimi.dima.track.common.ext.paceBetterThan
import it.polimi.dima.track.common.ext.paceWorseThan
import it.polimi.dima.track.common.ext.timeBetterThan
import it.polimi.dima.track.common.ext.timeWorseThan
import it.polimi.dima.track.model.PersonalBest
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.TrainingStep
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.storage.PersonalBestStorageService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.screens.TrackViewModel
import javax.inject.Inject

@HiltViewModel
class FillRepetitionsViewModel @Inject constructor(
  logService: LogService,
  private val trainingStorageService: TrainingStorageService,
  private val personalBestStorageService: PersonalBestStorageService,
  savedStateHandle: SavedStateHandle
) : TrackViewModel(logService) {
  private val trainingId: String = savedStateHandle["trainingId"] ?: TRAINING_DEFAULT_ID

  val training = mutableStateOf(Training())
  val trainingSteps = mutableStateOf(listOf<TrainingStep>())

  init {
    launchCatching {
      if (trainingId != TRAINING_DEFAULT_ID) {
        training.value = trainingStorageService.getTraining(trainingId) ?: Training()
        trainingSteps.value = training.value.trainingSteps
      }
    }
  }

  fun onTimeFillClick(hierarchy: List<String>, index: Int, trainingStepId: String, result: String) {
    trainingSteps.value =
      onTimeFillClickHelper(
        hierarchy,
        trainingSteps.value.toMutableList(),
        index,
        trainingStepId,
        result
      )
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
      val id = if (editedTraining.id.isBlank()) {
        trainingStorageService.saveTraining(editedTraining)
      } else {
        trainingStorageService.updateTraining(editedTraining)
        editedTraining.id
      }
      updatePersonalBests(training.value.copy(id = id))
      popUpScreen()
    }
  }

  private suspend fun updatePersonalBests(training: Training) {
    val newBestsInTraining = training.getBestResults()
    val bestForDistances = newBestsInTraining.first
    val bestForTimes = newBestsInTraining.second

    bestForDistances.forEach { (distance, result) ->
      val trainingBestForDistance =
        personalBestStorageService.getPersonalBestFromDistanceAndTraining(distance, training.id)
      when {
        trainingBestForDistance == null -> {
          val isNewPersonalBest = updateGlobalDistancePersonalBest(distance, result, training.id)
          saveDistancePersonalBestForTraining(distance, result, training.id, isNewPersonalBest)
        }
        trainingBestForDistance.result.timeWorseThan(result) -> {
          val isNewPersonalBest = updateGlobalDistancePersonalBest(distance, result, training.id)
          updatePersonalBestForTraining(trainingBestForDistance, result, training.id, isNewPersonalBest)
        }
        trainingBestForDistance.globalPersonalBest && trainingBestForDistance.result.timeBetterThan(result) -> {
          val promoted = promoteSecondGlobalDistancePersonalBest(distance, result)
          updatePersonalBestForTraining(trainingBestForDistance, result, training.id, !promoted)
        }
      }
    }

    bestForTimes.forEach { (duration, result) ->
      val trainingBestForDuration =
        personalBestStorageService.getPersonalBestFromDurationAndTraining(duration, training.id)
      when {
        trainingBestForDuration == null -> {
          val isNewPersonalBest = updateGlobalDurationPersonalBest(duration, result, training.id)
          saveDurationPersonalBestForTraining(duration, result, training.id, isNewPersonalBest)
        }
        trainingBestForDuration.result.paceWorseThan(result) -> {
          val isNewPersonalBest = updateGlobalDurationPersonalBest(duration, result, training.id)
          updatePersonalBestForTraining(trainingBestForDuration, result, training.id, isNewPersonalBest)
        }
        trainingBestForDuration.globalPersonalBest && trainingBestForDuration.result.paceBetterThan(result) -> {
          val promoted = promoteSecondGlobalDurationPersonalBest(duration, result)
          updatePersonalBestForTraining(trainingBestForDuration, result, training.id, !promoted)
        }
      }
    }

    updatePersonalBestFlag(training.id)
  }

  private suspend fun updatePersonalBestForTraining(
    trainingBestForDistance: PersonalBest,
    result: String,
    trainingId: String,
    isGeneralPersonalBest: Boolean
  ) {
    personalBestStorageService.updatePersonalBest(
      trainingBestForDistance.copy(
        result = result,
        trainingId = trainingId,
        globalPersonalBest = isGeneralPersonalBest
      )
    )
  }

  private suspend fun saveDistancePersonalBestForTraining(
    distance: Int,
    result: String,
    trainingId: String,
    isGeneralPersonalBest: Boolean
  ) {
    personalBestStorageService.savePersonalBest(
      PersonalBest(
        distance = distance,
        type = TrainingStep.DurationType.DISTANCE,
        result = result,
        trainingId = trainingId,
        globalPersonalBest = isGeneralPersonalBest
      )
    )
  }

  private suspend fun saveDurationPersonalBestForTraining(
    duration: Int,
    result: String,
    trainingId: String,
    isGeneralPersonalBest: Boolean
  ) {
    personalBestStorageService.savePersonalBest(
      PersonalBest(
        duration = duration,
        type = TrainingStep.DurationType.TIME,
        result = result,
        trainingId = trainingId,
        globalPersonalBest = isGeneralPersonalBest
      )
    )
  }

  private suspend fun updateGlobalDistancePersonalBest(
    distance: Int,
    result: String,
    trainingId: String
  ): Boolean {
    val bestForDistance = personalBestStorageService.getGlobalPersonalBestFromDistance(distance)
    val isNewPersonalBest =
      if (bestForDistance == null) true
      else if (bestForDistance.result.timeWorseThan(result)) {
        if (trainingId != bestForDistance.trainingId) {
          personalBestStorageService.updatePersonalBest(
            bestForDistance.copy(
              globalPersonalBest = false
            )
          )
          updatePersonalBestFlag(bestForDistance.trainingId)
        }
        true
      } else false
    return isNewPersonalBest
  }

  private suspend fun updateGlobalDurationPersonalBest(
    duration: Int,
    result: String,
    trainingId: String
  ): Boolean {
    val bestForDuration = personalBestStorageService.getGlobalPersonalBestFromDuration(duration)
    val isNewPersonalBest =
      if (bestForDuration == null) true
      else if (bestForDuration.result.paceWorseThan(result)) {
        if (trainingId != bestForDuration.trainingId) {
          personalBestStorageService.updatePersonalBest(
            bestForDuration.copy(
              globalPersonalBest = false
            )
          )
          updatePersonalBestFlag(bestForDuration.trainingId)
        }
        true
      } else false
    return isNewPersonalBest
  }

  private suspend fun promoteSecondGlobalDistancePersonalBest(distance: Int, result: String): Boolean {
    val secondBestForDistance = personalBestStorageService.getSecondGlobalPersonalBestFromDistance(distance)
    val promoted =
      if (secondBestForDistance == null) false
      else if (secondBestForDistance.result.timeBetterThan(result)) {
        personalBestStorageService.updatePersonalBest(
          secondBestForDistance.copy(
            globalPersonalBest = true
          )
        )
        trainingStorageService.updatePersonalBestFlag(secondBestForDistance.trainingId, true)
        true
      }
      else false
    return promoted
  }

  private suspend fun promoteSecondGlobalDurationPersonalBest(duration: Int, result: String): Boolean {
    val secondBestForDuration = personalBestStorageService.getSecondGlobalPersonalBestFromDuration(duration)
    val promoted =
      if (secondBestForDuration == null) false
      else if (secondBestForDuration.result.paceBetterThan(result)) {
        personalBestStorageService.updatePersonalBest(
          secondBestForDuration.copy(
            globalPersonalBest = true
          )
        )
        trainingStorageService.updatePersonalBestFlag(secondBestForDuration.trainingId, true)
        true
      }
      else false
    return promoted
  }

  private suspend fun updatePersonalBestFlag(trainingId: String) {
    val exists = personalBestStorageService.existsGlobalPersonalBestWithTrainingId(trainingId)
    trainingStorageService.updatePersonalBestFlag(trainingId, exists)
  }

  fun onCancelClick(popUpScreen: () -> Unit) {
    popUpScreen()
  }
}
