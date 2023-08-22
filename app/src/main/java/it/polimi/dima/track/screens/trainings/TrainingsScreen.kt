package it.polimi.dima.track.screens.trainings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.OutlinedCardWithHeader
import it.polimi.dima.track.common.ext.getCompleteTime
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.common.utils.NavigationType
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.screens.training.TrainingCard


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TrainingsScreen(
  openScreen: (String) -> Unit,
  modifier: Modifier = Modifier,
  navigationType: NavigationType,
  viewModel: TrainingsViewModel = hiltViewModel(),
  onTrainingPressed: (Training) -> Unit
) {
  Scaffold(
    floatingActionButton = {
      if (navigationType == NavigationType.BOTTOM_NAVIGATION) {
        ExtendedFloatingActionButton(
          onClick = { viewModel.onAddClick(openScreen) },
          modifier = modifier.padding(16.dp),
          text = { Text(stringResource(id = R.string.add_training)) },
          icon = {
            Icon(
              Icons.Rounded.Add,
              contentDescription = stringResource(id = R.string.add_training),
            )
          },
        )
      }
    }
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
      modifier = Modifier.fillMaxSize()
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
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
      ) {
        OutlinedCardWithHeader(
          header = stringResource(id = R.string.next_scheduled_training),
          icon = Icons.Rounded.Event,
        ) {
          if (nextTraining != null) {
            TrainingCard(
              modifier = Modifier.padding(8.dp),
              training = nextTraining,
              actions = options,
              onClick = { onTrainingPressed(nextTraining) },
              onActionClick = { action ->
                viewModel.onTrainingActionClick(openScreen, nextTraining, action, context)
              }
            )
          } else {
            Text(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
              textAlign = TextAlign.Center,
              text = stringResource(id = R.string.no_planned_trainings),
              color = LocalContentColor.current.copy(alpha = 0.5f)
            )
          }
        }

        OutlinedCardWithHeader(
          header = stringResource(id = R.string.last_training),
          icon = Icons.Rounded.History
        ) {
          if (lastTraining != null) {
            TrainingCard(
              modifier = Modifier.padding(8.dp),
              training = lastTraining,
              actions = options,
              onClick = { onTrainingPressed(lastTraining) },
              onActionClick = { action ->
                viewModel.onTrainingActionClick(openScreen, lastTraining, action, context)
              }
            )
          } else {
            Text(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
              textAlign = TextAlign.Center,
              text = stringResource(id = R.string.no_trainings_in_history),
              color = LocalContentColor.current.copy(alpha = 0.5f)
            )
          }
        }
      }
    }

    LaunchedEffect(viewModel) { viewModel.loadTaskOptions() }
  }
}
