package it.polimi.dima.track.screens.training

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.TRAINING_DEFAULT_ID
import it.polimi.dima.track.TRAINING_SCREEN
import it.polimi.dima.track.common.ext.emptyResults
import it.polimi.dima.track.model.FitbitData
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.fitbit.FitbitAuthManager
import it.polimi.dima.track.model.service.fitbit.FitbitOAuthToken
import it.polimi.dima.track.model.service.fitbit.FitbitService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.model.service.storage.UserStorageService
import it.polimi.dima.track.screens.TrackViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
  logService: LogService,
  private val trainingStorageService: TrainingStorageService,
  private val fitbitService: FitbitService,
  private val userStorageService: UserStorageService,
  private val fitbitAuthManager: FitbitAuthManager,
) : TrackViewModel(logService) {
  val training = mutableStateOf(Training())
  val options = mutableStateOf<List<String>>(listOf())
  val isFitbitConnected = mutableStateOf(false)
  private var fitbitToken: FitbitOAuthToken? = null

  fun initialize(trainingId: String) {
    launchCatching {
      if (trainingId != TRAINING_DEFAULT_ID) {
        training.value = trainingStorageService.getTraining(trainingId) ?: Training()
      }
      fitbitToken = userStorageService.user.first().fitbitToken
      isFitbitConnected.value = fitbitToken != null
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

  suspend fun onImportFitbitDataClick() {
    if (fitbitToken == null) {
      throw Exception()
    }
    if (!fitbitAuthManager.isAccessTokenActive(fitbitToken!!.accessToken)) {
      fitbitToken = fitbitAuthManager.refreshToken(fitbitToken!!.refreshToken)
    }
    userStorageService.updateFitbitToken(fitbitToken)
    fitbitService.token = fitbitToken!!
    val activities = fitbitService.getActivitiesByTraining(training.value)
    if (activities.isEmpty()) {
      return
    }
    val aggregatedActivity = activities.reduce { acc, activity ->
      acc.calories += activity.calories
      acc.steps += activity.steps
      acc.distance += activity.distance
      acc.elevationGain += activity.elevationGain
      acc
    }
    val fitbitData = FitbitData(
      aggregatedActivity.calories,
      aggregatedActivity.steps,
      aggregatedActivity.distance,
      aggregatedActivity.elevationGain,
    )
    training.value = training.value.copy(
      fitbitData = fitbitData
    )
    trainingStorageService.updateTraining(training.value)
  }
}
