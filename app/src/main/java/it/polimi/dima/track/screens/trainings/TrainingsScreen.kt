package it.polimi.dima.track.screens.trainings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
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
      endActionIcon = Icons.Default.Settings,
      endActionDescription = R.string.settings,
      endAction = { viewModel.onSettingsClick(openScreen) }
    ) { }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
    ) {

      Spacer(modifier = Modifier.smallSpacer())

      if (nextTraining != null) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = stringResource(id = R.string.next_scheduled_training),
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.bodyLarge
          )
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
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = stringResource(id = R.string.last_training),
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.bodyLarge
          )
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
