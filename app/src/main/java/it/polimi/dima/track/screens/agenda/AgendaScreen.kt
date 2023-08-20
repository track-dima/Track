package it.polimi.dima.track.screens.agenda

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.R
import it.polimi.dima.track.SEARCH_SCREEN
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.DeleteDialog
import it.polimi.dima.track.common.ext.getCompleteTime
import it.polimi.dima.track.common.ext.getDay
import it.polimi.dima.track.common.ext.getDayName
import it.polimi.dima.track.common.ext.getWeekInterval
import it.polimi.dima.track.common.ext.isToday
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.common.utils.NavigationType
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.screens.training.TrainingActionOption
import it.polimi.dima.track.screens.training.TrainingCard


@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AgendaScreen(
  openScreen: (String) -> Unit,
  modifier: Modifier = Modifier,
  navigationType: NavigationType,
  viewModel: AgendaViewModel = hiltViewModel(),
  onTrainingPressed: (Training) -> Unit
) {
  val context = LocalContext.current
  val openDeleteDialog = rememberSaveable { mutableStateOf(false) }
  val currentTraining = rememberSaveable { mutableStateOf("") }

  if (openDeleteDialog.value) {
    DeleteDialog(
      title = stringResource(R.string.delete_training),
      text = stringResource(R.string.delete_training_confirmation),
      onDeleteClick = {
        openDeleteDialog.value = false
        viewModel.onDeleteTaskClick(currentTraining.value)
      },
      onDismissRequest = { openDeleteDialog.value = false }
    )
  }

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
    val trainings = viewModel.filteredTrainings.collectAsStateWithLifecycle(emptyList())

    // TODO is this sorting efficient?
    val sortedTrainings = trainings.value.sortedByDescending { it.getCompleteTime() }
    val options by viewModel.options

    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      ActionToolbar(
        title = R.string.agenda,
        modifier = Modifier.toolbarActions(),
        endActionIcon = Icons.Rounded.Settings,
        endActionDescription = R.string.settings,
        endAction = { viewModel.onSettingsClick(openScreen) }
      ) {
        IconButton(onClick = { openScreen(SEARCH_SCREEN) }) {
          Icon(
            Icons.Rounded.Search,
            contentDescription = stringResource(R.string.search_trainings)
          )
        }

        IconToggleButton(
          checked = viewModel.isFavoriteFilterActive,
          onCheckedChange = { viewModel.onFavoriteToggle(it) }
        ) {
          Icon(
            if (viewModel.isFavoriteFilterActive) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = stringResource(R.string.favorite)
          )
        }
      }

      if (sortedTrainings.isEmpty()) {
        Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = stringResource(R.string.no_trainings),
          )
        }
      } else {
        AnimatedContent(
          targetState = sortedTrainings,
          label = "Agenda trainings",
          transitionSpec = {
            fadeIn() with fadeOut()
          }
        ) { trainings ->
          AgendaTrainings(
            trainings = trainings,
            options = options,
            onTrainingPressed = onTrainingPressed,
            onActionClick = { action, trainingItem ->
              when (TrainingActionOption.getByTitle(action)) {
                TrainingActionOption.DeleteTraining -> {
                  currentTraining.value = trainingItem.id
                  openDeleteDialog.value = true
                }

                else -> viewModel.onTrainingActionClick(openScreen, trainingItem, action, context)
              }
            }
          )
        }
      }
    }
  }

  LaunchedEffect(viewModel) { viewModel.loadTaskOptions() }
}

@Composable
fun AgendaTrainings(
  trainings: List<Training>,
  options: List<String> = listOf(),
  onTrainingPressed: (Training) -> Unit,
  onActionClick: (String, Training) -> Unit = { _, _ -> },
  showActions: Boolean = true
) {
  LazyColumn {
    item { Spacer(modifier = Modifier.spacer()) }

    val groupedTrainings = trainings.groupBy {
      it.dueDate.getWeekInterval("No date")
    }

    groupedTrainings.forEach { (weekInterval, trainingsForWeek) ->
      item {
        Text(
          text = weekInterval,
          modifier = Modifier.padding(start = 48.dp, top = 16.dp, bottom = 8.dp),
          style = MaterialTheme.typography.labelLarge
        )
      }

      val dayGroupedTrainings = trainingsForWeek.groupBy {
        it.dueDate
      }

      dayGroupedTrainings.forEach { (date, trainingsForDay) ->
        item {
          Row {
            Column(
              modifier = Modifier.width(48.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              val isToday = date != null && date.isToday()
              Text(
                text = date.getDayName(),
                style = MaterialTheme.typography.labelMedium,
                color = if (isToday) MaterialTheme.colorScheme.primary else Color.Unspecified
              )
              Text(
                text = date.getDay(),
                style = MaterialTheme.typography.labelLarge,
                color = if (isToday) MaterialTheme.colorScheme.primary else Color.Unspecified
              )
            }
            Column {
              trainingsForDay.forEach { trainingItem ->
                TrainingCard(
                  modifier = Modifier.padding(bottom = 8.dp, end = 8.dp),
                  training = trainingItem,
                  options = options,
                  onClick = { onTrainingPressed(trainingItem) },
                  onActionClick = { onActionClick(it, trainingItem) },
                  showActions = showActions
                )
              }
            }
          }
        }
      }
    }
  }
}
