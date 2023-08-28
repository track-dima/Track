package it.polimi.dima.track.screens.training

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.PinDrop
import androidx.compose.material.icons.rounded.Snowshoeing
import androidx.compose.material.icons.rounded.Stairs
import androidx.compose.material.icons.rounded.Terrain
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.EDIT_MODE_EDIT
import it.polimi.dima.track.FILL_REPETITIONS_SCREEN
import it.polimi.dima.track.R
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.common.composable.DeleteDialog
import it.polimi.dima.track.common.composable.DropdownContextMenu
import it.polimi.dima.track.common.composable.IconButtonStyle
import it.polimi.dima.track.common.composable.NoTitleTransparentToolbar
import it.polimi.dima.track.common.composable.TrainingStepsListBox
import it.polimi.dima.track.common.ext.bigSpacer
import it.polimi.dima.track.common.ext.calculateTotalTime
import it.polimi.dima.track.common.ext.contextMenu
import it.polimi.dima.track.common.ext.hasDueDate
import it.polimi.dima.track.common.ext.hasDueTime
import it.polimi.dima.track.common.ext.parseTraining
import it.polimi.dima.track.common.ext.secondsToHhMm
import it.polimi.dima.track.common.utils.addToCalendar
import it.polimi.dima.track.common.utils.copyToClipboard
import it.polimi.dima.track.common.utils.sendIntent
import it.polimi.dima.track.common.utils.addToCalendar
import it.polimi.dima.track.model.FitbitData
import it.polimi.dima.track.model.Training
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TrainingScreen(
  modifier: Modifier = Modifier,
  compactMode: Boolean = false,
  popUpScreen: () -> Unit = {},
  openScreen: (String) -> Unit,
  trainingId: String,
  onEditPressed: (String, String) -> Unit,
  viewModel: TrainingViewModel = hiltViewModel()
) {
  val training by viewModel.training
  val options by viewModel.options
  val isFitbitConnected by viewModel.isFitbitConnected

  LaunchedEffect(trainingId) {
    // TODO is called on configuration change
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
    modifier = modifier
      .verticalScroll(rememberScrollState()),
  ) {
    NoTitleTransparentToolbar(
      navigationIcon = {
        if (!compactMode) {
          FilledTonalIconButton(
            modifier = Modifier.padding(8.dp, 0.dp),
            onClick = popUpScreen
          ) {
            Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.close))
          }
        }
      }
    ) {
      TrainingToolbarActions(
        training = training,
        onFavoriteClick = { favorite -> viewModel.onFavoriteClick(favorite) },
        onEditPressed = { onEditPressed(training.id, EDIT_MODE_EDIT) },
        options = options,
        onDuplicateTrainingClick = {
          viewModel.onDuplicateTrainingClick(
            training.id,
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

      if (isFitbitConnected) {
        Spacer(modifier = Modifier.bigSpacer())
        TrainingFitbitData(training) { viewModel.onImportFitbitDataClick() }
      }
    }

    Spacer(modifier = Modifier.bigSpacer())

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
        TrainingActionOption.DeleteTraining -> onDeleteTaskClick()
        TrainingActionOption.DuplicateTraining -> onDuplicateTrainingClick()
        TrainingActionOption.ShareLink -> sendIntent(
          context = context,
          text = "https://track.com/training/${training.id}"
        )

        TrainingActionOption.CopyTraining -> copyToClipboard(
          context = context,
          text = training.parseTraining(),
          label = "Training",
        )

        TrainingActionOption.AddToCalendar -> addToCalendar(
          context = context,
          training = training
        )

        else -> Unit
      }
    },
    style = IconButtonStyle.FilledTonal,
  )
}

@Composable
private fun TrainingInformation(training: Training) {
  if (training.title.isNotEmpty()) {
    TrainingTitle(training)
  }

  if (training.description.isNotEmpty()) {
    Spacer(modifier = Modifier.bigSpacer())
    TrainingDescription(training)
  }

  if (training.hasDueDate() || training.hasDueTime()) {
    Spacer(modifier = Modifier.bigSpacer())
    TrainingTime(training)
  }

  if (training.notes.isNotEmpty()) {
    Spacer(modifier = Modifier.bigSpacer())
    TrainingNotes(training)
  }

  if (training.type.isNotEmpty()) {
    Spacer(modifier = Modifier.bigSpacer())
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
      modifier = Modifier.padding(end = 16.dp)
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
      contentDescription = stringResource(R.string.title),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainingFitbitData(
  training: Training,
  onImportFitbitDataClick: suspend () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val showDialog = remember { mutableStateOf(false) }

  if (training.fitbitData == null) {
    Button(
      onClick = {
        showDialog.value = true
        coroutineScope.launch {
          withContext(Dispatchers.IO) {
            onImportFitbitDataClick()
            showDialog.value = false
          }
        }
      },
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text("Import Fitbit data")
    }
  } else {
    TrainingCalories(training.fitbitData)
    TrainingSteps(training.fitbitData)
    TrainingDistance(training.fitbitData)
    TrainingElevationGain(training.fitbitData)
  }

  if (showDialog.value) {
    AlertDialog(onDismissRequest = { showDialog.value = false }) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(100.dp)
          .background(Color.White, shape = RoundedCornerShape(8.dp))
      ) {
        CircularProgressIndicator()
      }
    }
  }
}

@Composable
private fun TrainingCalories(fitbitData: FitbitData) {
  Row {
    Icon(
      Icons.Rounded.LocalFireDepartment,
      contentDescription = stringResource(R.string.calories),
      modifier = Modifier.padding(end = 16.dp)
    )
    SelectionContainer {
      Text(text = "${fitbitData.calories} calories")
    }
  }
}

@Composable
private fun TrainingSteps(fitbitData: FitbitData) {
  Row {
    Icon(
      Icons.Rounded.Snowshoeing,
      contentDescription = stringResource(R.string.steps),
      modifier = Modifier.padding(end = 16.dp)
    )
    SelectionContainer {
      Text(text = "${fitbitData.steps} steps")
    }
  }
}

@Composable
private fun TrainingDistance(fitbitData: FitbitData) {
  Row {
    Icon(
      Icons.Rounded.PinDrop,
      contentDescription = stringResource(R.string.distance),
      modifier = Modifier.padding(end = 16.dp)
    )
    SelectionContainer {
      Text(text = "${fitbitData.distance} m")
    }
  }
}

@Composable
private fun TrainingElevationGain(fitbitData: FitbitData) {
  Row {
    Icon(
      Icons.Rounded.Stairs,
      contentDescription = stringResource(R.string.elevation_gain),
      modifier = Modifier.padding(end = 16.dp)
    )
    SelectionContainer {
      Text(text = fitbitData.elevationGain.toString())
    }
  }
}