package it.polimi.dima.track.screens.trainings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.OutlinedCardWithHeader
import it.polimi.dima.track.common.ext.getCompleteTime
import it.polimi.dima.track.common.ext.smallSpacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.screens.training.TrainingCard


@Composable
fun TrainingsScreen(
  openScreen: (String) -> Unit,
  viewModel: TrainingsViewModel = hiltViewModel(),
  onTrainingPressed: (Training) -> Unit
) {
  val context = LocalContext.current
  val trainings = viewModel.trainings.collectAsStateWithLifecycle(emptyList())

  val sortedTrainings = trainings.value.sortedByDescending { it.getCompleteTime() }

  val now = System.currentTimeMillis()
  val lastTraining = sortedTrainings.firstOrNull { training ->
    val trainingDateTime = training.getCompleteTime()
    trainingDateTime <= now
  }
  val nextTraining = sortedTrainings.lastOrNull { training ->
    val trainingDateTime = training.getCompleteTime()
    trainingDateTime > now
  }
  val options by viewModel.options

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
  ) {
    ActionToolbar(
      title = R.string.trainings,
      modifier = Modifier.toolbarActions(),
      endActionIcon = Icons.Rounded.Settings,
      endActionDescription = R.string.settings,
      endAction = { viewModel.onSettingsClick(openScreen) }
    ) { }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
    ) {
      if (nextTraining != null) {
        OutlinedCardWithHeader(
          header = stringResource(id = R.string.next_scheduled_training),
          icon = Icons.Rounded.Event,
        ) {
          TrainingCard(
            modifier = Modifier.padding(8.dp),
            training = nextTraining,
            options = options,
            onClick = { onTrainingPressed(nextTraining) },
            onActionClick = { action ->
              viewModel.onTrainingActionClick(openScreen, nextTraining, action, context)
            }
          )
        }
      }

      if (lastTraining != null) {
        OutlinedCardWithHeader(
          header = stringResource(id = R.string.last_training),
          icon = Icons.Rounded.History
        ) {
          TrainingCard(
            modifier = Modifier.padding(8.dp),
            training = lastTraining,
            options = options,
            onClick = { onTrainingPressed(lastTraining) },
            onActionClick = { action ->
              viewModel.onTrainingActionClick(openScreen, lastTraining, action, context)
            }
          )
        }
      }
    }
  }

  LaunchedEffect(viewModel) { viewModel.loadTaskOptions() }
}
