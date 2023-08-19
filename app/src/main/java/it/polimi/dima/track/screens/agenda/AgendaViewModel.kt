package it.polimi.dima.track.screens.agenda

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.AGENDA_SCREEN
import it.polimi.dima.track.EDIT_TRAINING_SCREEN
import it.polimi.dima.track.SETTINGS_SCREEN
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.common.ext.emptyResults
import it.polimi.dima.track.common.ext.parseTraining
import it.polimi.dima.track.common.utils.copyToClipboard
import it.polimi.dima.track.common.utils.sendIntent
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.ConfigurationService
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.StorageService
import it.polimi.dima.track.screens.TrackViewModel
import it.polimi.dima.track.screens.training.TrainingActionOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
  private val configurationService: ConfigurationService
) : TrackViewModel(logService) {
  val options = mutableStateOf<List<String>>(listOf())
  private val trainings = storageService.trainings

  private val favoriteSelected = MutableStateFlow(false)
  val isFavoriteFilterActive: Boolean get() = favoriteSelected.value

  val filteredTrainings: Flow<List<Training>> = combine(
    favoriteSelected,
    trainings
  ) { filtered, trainingsList ->
    if (filtered) {
      trainingsList.filter { training -> training.favorite }
    } else {
      trainingsList
    }
  }

  fun loadTaskOptions() {
    // TODO just as example of configuration service val hasEditOption = configurationService.isShowTrainingEditButtonConfig
    options.value = TrainingActionOption.getOptions(AGENDA_SCREEN)
  }

  fun onFavoriteToggle(showFavorites: Boolean) {
    favoriteSelected.value = showFavorites
  }

  fun onAddClick(openScreen: (String) -> Unit) = openScreen(EDIT_TRAINING_SCREEN)

  fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

  fun onTrainingActionClick(
    openScreen: (String) -> Unit,
    training: Training,
    action: String,
    context: Context
  ) {
    when (TrainingActionOption.getByTitle(action)) {
      TrainingActionOption.EditTraining -> openScreen("$EDIT_TRAINING_SCREEN?$TRAINING_ID=${training.id}")
      TrainingActionOption.ShareLink -> sendIntent(
        context = context,
        text = "https://track.com/training/${training.id}"
      )

      TrainingActionOption.CopyTraining -> copyToClipboard(
        context = context,
        text = training.parseTraining(),
        label = "Training",
      )

      TrainingActionOption.DuplicateTraining -> onDuplicateTrainingClick(training, openScreen)
      TrainingActionOption.ToggleFavourite -> onFavouriteTrainingClick(training)
      else -> Unit
    }
  }

  private fun onFavouriteTrainingClick(training: Training) {
    launchCatching { storageService.updateTraining(training.copy(favorite = !training.favorite)) }
  }

  fun onDeleteTaskClick(trainingId: String) {
    launchCatching { storageService.deleteTraining(trainingId) }
  }

  private fun onDuplicateTrainingClick(training: Training, openScreen: (String) -> Unit) {
    launchCatching {
      val newId = storageService.duplicateTraining(
        training.copy(
          transient = true,
          favorite = false,
          personalBest = false,
          trainingSteps = emptyResults(training.trainingSteps)
        )
      )
      openScreen("$EDIT_TRAINING_SCREEN?$TRAINING_ID=${newId}")
    }
  }
}
