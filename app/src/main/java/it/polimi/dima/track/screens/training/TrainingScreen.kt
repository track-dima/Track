package it.polimi.dima.track.screens.training

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.Terrain
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.FILL_REPETITIONS_SCREEN
import it.polimi.dima.track.R
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.common.composable.DeleteDialog
import it.polimi.dima.track.common.composable.DropdownContextMenu
import it.polimi.dima.track.common.composable.IconButtonStyle
import it.polimi.dima.track.common.composable.NoTitleToolbar
import it.polimi.dima.track.common.composable.TrainingStepsListBox
import it.polimi.dima.track.common.ext.calculateTotalTime
import it.polimi.dima.track.common.ext.contextMenu
import it.polimi.dima.track.common.ext.hasDueDate
import it.polimi.dima.track.common.ext.hasDueTime
import it.polimi.dima.track.common.ext.parseTraining
import it.polimi.dima.track.common.ext.secondsToHhMm
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.common.utils.copyToClipboard
import it.polimi.dima.track.common.utils.sendIntent
import it.polimi.dima.track.model.Training

@Composable
fun TrainingScreen(
  popUpScreen: () -> Unit,
  openScreen: (String) -> Unit,
  trainingId: String,
  onEditPressed: (Training) -> Unit,
  viewModel: TrainingViewModel = hiltViewModel()
) {
  val training by viewModel.training
  val options by viewModel.options

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }

  val openDeleteDialog = rememberSaveable { mutableStateOf(false) }

  if (openDeleteDialog.value) {
    DeleteDialog(
      title = stringResource(R.string.delete_training),
      text = stringResource(R.string.delete_training_confirmation),
      onDeleteClick = {
        openDeleteDialog.value = false
        viewModel.onDeleteTaskClick(training, popUpScreen)
      },
      onDismissRequest = { openDeleteDialog.value = false }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
  ) {
    NoTitleToolbar(
      navigationIcon = {
        FilledTonalIconButton(
          modifier = Modifier.padding(8.dp, 0.dp),
          onClick = popUpScreen
        ) {
          Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.close))
        }
      }
    ) {
      TrainingToolbarActions(
        training = training,
        onFavoriteClick = { favorite -> viewModel.onFavoriteClick(favorite) },
        onEditPressed = { onEditPressed(training) },
        options = options,
        onDuplicateTrainingClick = {
          viewModel.onDuplicateTrainingClick(
            training,
            popUpScreen,
            onEditPressed
          )
        },
        onDeleteTaskClick = { openDeleteDialog.value = true }
      )
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.Center
    ) {
      TrainingInformation(training)
    }

    Spacer(modifier = Modifier.spacer())

    TrainingStepsListBox(
      training = training,
      filling = true,
      onFillSteps = { openScreen("$FILL_REPETITIONS_SCREEN?$TRAINING_ID=${training.id}") }
    )
  }

  LaunchedEffect(viewModel) { viewModel.loadTaskOptions() }
}

@Composable
private fun TrainingToolbarActions(
  training: Training,
  onFavoriteClick: (Boolean) -> Unit,
  onEditPressed: () -> Unit,
  options: List<String>,
  onDeleteTaskClick: () -> Unit,
  onDuplicateTrainingClick: () -> Unit
) {
  val context = LocalContext.current

  FilledTonalIconToggleButton(
    modifier = Modifier.padding(horizontal = 4.dp),
    checked = training.favorite,
    onCheckedChange = onFavoriteClick
  ) {
    Icon(
      if (training.favorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
      contentDescription = stringResource(R.string.favorite)
    )
  }
  FilledTonalIconButton(
    modifier = Modifier.padding(horizontal = 4.dp),
    onClick = onEditPressed
  ) {
    Icon(Icons.Rounded.Edit, contentDescription = stringResource(R.string.edit_training))
  }
  DropdownContextMenu(
    options = options,
    modifier = Modifier
      .contextMenu()
      .padding(start = 4.dp, end = 8.dp),
    onActionClick = { action ->
      when (TrainingActionOption.getByTitle(action)) {
        TrainingActionOption.DeleteTask -> onDeleteTaskClick()
        TrainingActionOption.DuplicateTraining -> onDuplicateTrainingClick()
        TrainingActionOption.Share -> sendIntent(
          context = context,
          text = "https://track.com/training/${training.id}"
        )
        TrainingActionOption.CopyTraining -> copyToClipboard(
          context = context,
          text = training.parseTraining(),
          label = "Training",
        )

        else -> Unit
      }
    },
    style = IconButtonStyle.FilledTonal,
  )
}

@Composable
private fun TrainingInformation(training: Training) {
  TrainingTitle(training)

  if (training.description.isNotEmpty()) {
    Spacer(modifier = Modifier.spacer())
    TrainingDescription(training)
  }

  if (training.hasDueDate() || training.hasDueTime()) {
    Spacer(modifier = Modifier.spacer())
    TrainingTime(training)
  }

  if (training.notes.isNotEmpty()) {
    Spacer(modifier = Modifier.spacer())
    TrainingNotes(training)
  }

  if (training.type.isNotEmpty()) {
    Spacer(modifier = Modifier.spacer())
    TrainingType(training)
  }
}

@Composable
private fun TrainingType(training: Training) {
  Row {
    Icon(
      Icons.Rounded.Terrain,
      contentDescription = stringResource(R.string.type),
      modifier = Modifier
        .padding(end = 16.dp)
        .align(Alignment.CenterVertically)
    )
    Text(text = training.type)
  }
}

@Composable
private fun TrainingTime(training: Training) {
  Row {
    Icon(
      Icons.Rounded.CalendarToday,
      contentDescription = stringResource(R.string.date),
      modifier = Modifier
        .padding(end = 16.dp)
        .align(Alignment.CenterVertically)
    )
    Column {
      Row {
        Text(text = training.dueDateString)
        if (training.hasDueDate() && training.hasDueTime()) Text(
          text = " ･ "
        )
        Text(text = training.dueTimeString)
      }
      Text(
        text = "estimated ${training.calculateTotalTime().secondsToHhMm()}h",
        style = MaterialTheme.typography.bodyMedium
      )
    }
  }
}

@Composable
private fun TrainingDescription(training: Training) {
  Row {
    Icon(
      Icons.Rounded.Description,
      contentDescription = stringResource(R.string.description),
      modifier = Modifier
        .padding(end = 16.dp)
        .align(Alignment.CenterVertically)
    )
    SelectionContainer {
      Text(text = training.description)
    }
  }
}

@Composable
private fun TrainingNotes(training: Training) {
  Row {
    Icon(
      Icons.Rounded.Notes,
      contentDescription = stringResource(R.string.notes),
      modifier = Modifier
        .padding(end = 16.dp)
    )
    SelectionContainer {
      Text(text = training.notes)
    }
  }
}

@Composable
private fun TrainingTitle(training: Training) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 16.dp),
  ) {
    Icon(
      Icons.Rounded.DirectionsRun,
      contentDescription = stringResource(R.string.run),
      modifier = Modifier
        .padding(end = 16.dp)
        .align(Alignment.CenterVertically)
    )
    SelectionContainer {
      Text(
        text = training.title,
        style = MaterialTheme.typography.titleLarge
      )
    }
  }
}