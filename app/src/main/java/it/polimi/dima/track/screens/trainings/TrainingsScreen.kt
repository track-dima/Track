package it.polimi.dima.track.screens.trainings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.EDIT_TRAINING_SCREEN
import it.polimi.dima.track.R
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.OutlinedCardWithHeader
import it.polimi.dima.track.common.ext.getCompleteTime
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.common.utils.NavigationType
import it.polimi.dima.track.common.utils.TrackContentType
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.screens.training.TrainingCard
import it.polimi.dima.track.screens.training.TrainingScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TrainingsScreen(
  openScreen: (String) -> Unit,
  navigationType: NavigationType,
  contentType: TrackContentType,
  viewModel: TrainingsViewModel = hiltViewModel(),
  onTrainingPressed: (Training) -> Unit
) {
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

  if (contentType == TrackContentType.LIST_AND_DETAIL) {
    var selectedTrainingId by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(sortedTrainings) {
      if ((lastTraining != null || nextTraining != null) && selectedTrainingId != lastTraining?.id && selectedTrainingId != nextTraining?.id) {
        selectedTrainingId = lastTraining?.id ?: nextTraining!!.id
      }
    }

    Row {
      TrainingsContent(
        modifier = Modifier.fillMaxWidth(0.5f),
        showFab = false,
        nextTraining = nextTraining,
        lastTraining = lastTraining,
        onSettingsClick = { viewModel.onSettingsClick(openScreen) },
        onTrainingPressed = { selectedTrainingId = it.id },
        selectedTrainingId = selectedTrainingId,
        showActions = false
      )
      if (selectedTrainingId.isNotEmpty()) {
        TrainingScreen(
          modifier = Modifier.padding(start = 8.dp),
          compactMode = true,
          openScreen = openScreen,
          trainingId = selectedTrainingId,
          onEditPressed = { openScreen("$EDIT_TRAINING_SCREEN?$TRAINING_ID=${selectedTrainingId}") }
        )
      }
    }
  } else {
    val actions by viewModel.actions
    val context = LocalContext.current

    TrainingsContent(
      showFab = navigationType == NavigationType.BOTTOM_NAVIGATION,
      onFabClick = { viewModel.onAddClick(openScreen) },
      nextTraining = nextTraining,
      lastTraining = lastTraining,
      onSettingsClick = { viewModel.onSettingsClick(openScreen) },
      actions = actions,
      onActionClick = { action, trainingItem ->
        viewModel.onTrainingActionClick(openScreen, trainingItem, action, context)
      },
      onTrainingPressed = onTrainingPressed
    )
  }

  LaunchedEffect(viewModel) { viewModel.loadTaskOptions() }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun TrainingsContent(
  modifier: Modifier = Modifier,
  onFabClick: () -> Unit = {},
  showFab: Boolean,
  nextTraining: Training?,
  lastTraining: Training?,
  selectedTrainingId: String = "",
  onSettingsClick: () -> Unit,
  showActions: Boolean = true,
  actions: List<String> = emptyList(),
  onActionClick: (String, Training) -> Unit = { _, _ -> },
  onTrainingPressed: (Training) -> Unit,
) {
  Scaffold(
    modifier = modifier,
    floatingActionButton = {
      if (showFab) {
        ExtendedFloatingActionButton(
          onClick = onFabClick,
          modifier = Modifier.padding(16.dp),
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
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      ActionToolbar(
        title = R.string.trainings,
        modifier = Modifier.toolbarActions(),
        endActionIcon = Icons.Rounded.Settings,
        endActionDescription = R.string.settings,
        endAction = onSettingsClick
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
              selected = nextTraining.id == selectedTrainingId,
              showActions = showActions,
              actions = actions,
              onClick = { onTrainingPressed(nextTraining) },
              onActionClick = { action -> onActionClick(action, nextTraining) }
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
              selected = lastTraining.id == selectedTrainingId,
              showActions = showActions,
              actions = actions,
              onClick = { onTrainingPressed(lastTraining) },
              onActionClick = { action -> onActionClick(action, lastTraining) }
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
  }
}
