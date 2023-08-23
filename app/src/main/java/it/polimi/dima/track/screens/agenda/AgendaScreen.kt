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
import it.polimi.dima.track.EDIT_TRAINING_SCREEN
import it.polimi.dima.track.R
import it.polimi.dima.track.SEARCH_SCREEN
import it.polimi.dima.track.TRAINING_ID
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
import it.polimi.dima.track.common.utils.TrackContentType
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.screens.training.TrainingActionOption
import it.polimi.dima.track.screens.training.TrainingCard
import it.polimi.dima.track.screens.training.TrainingScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AgendaScreen(
  openScreen: (String) -> Unit,
  navigationType: NavigationType,
  contentType: TrackContentType,
  viewModel: AgendaViewModel = hiltViewModel(),
  onTrainingPressed: (Training) -> Unit
) {
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
  val trainings by viewModel.filteredTrainings.collectAsStateWithLifecycle(emptyList())
  val sortedTrainings = trainings.sortedByDescending { it.getCompleteTime() }

  if (contentType == TrackContentType.LIST_AND_DETAIL) {
    var selectedTrainingId by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(sortedTrainings) {
      if (sortedTrainings.isNotEmpty() && !sortedTrainings.any { it.id == selectedTrainingId }) {
        selectedTrainingId = sortedTrainings.first().id
      }
    }

    Row {
      AgendaContent(
        modifier = Modifier.fillMaxWidth(0.5f),
        showFab = false,
        trainings = sortedTrainings,
        onSettingsClick = { viewModel.onSettingsClick(openScreen) },
        openScreen = openScreen,
        onTrainingPressed = { selectedTrainingId = it.id },
        selectedTrainingId = selectedTrainingId,
        isFavoriteFilterActive = viewModel.isFavoriteFilterActive,
        onFavoriteToggle = { viewModel.onFavoriteToggle(it) },
        showActions = false
      )
      if (selectedTrainingId.isNotEmpty()) {
        // TODO favorite reloads the list
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

    AgendaContent(
      onFabClick = { viewModel.onAddClick(openScreen) },
      showFab = navigationType == NavigationType.BOTTOM_NAVIGATION,
      trainings = sortedTrainings,
      onSettingsClick = { viewModel.onSettingsClick(openScreen) },
      openScreen = openScreen,
      onTrainingPressed = onTrainingPressed,
      isFavoriteFilterActive = viewModel.isFavoriteFilterActive,
      onFavoriteToggle = { viewModel.onFavoriteToggle(it) },
      actions = actions,
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


  LaunchedEffect(viewModel) { viewModel.loadTrainingOptions() }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun AgendaContent(
  modifier: Modifier = Modifier,
  showFab: Boolean = true,
  onFabClick: () -> Unit = {},
  trainings: List<Training>,
  selectedTrainingId: String = "",
  openScreen: (String) -> Unit,
  onSettingsClick: () -> Unit,
  onTrainingPressed: (Training) -> Unit,
  isFavoriteFilterActive: Boolean,
  onFavoriteToggle: (Boolean) -> Unit,
  showActions: Boolean = true,
  actions: List<String> = emptyList(),
  onActionClick: (String, Training) -> Unit = { _, _ -> }
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
      modifier = Modifier.fillMaxSize(),
    ) {
      ActionToolbar(
        title = R.string.agenda,
        modifier = Modifier.toolbarActions(),
        endActionIcon = Icons.Rounded.Settings,
        endActionDescription = R.string.settings,
        endAction = onSettingsClick
      ) {
        IconButton(onClick = { openScreen(SEARCH_SCREEN) }) {
          Icon(
            Icons.Rounded.Search,
            contentDescription = stringResource(R.string.search_trainings)
          )
        }

        IconToggleButton(
          checked = isFavoriteFilterActive,
          onCheckedChange = onFavoriteToggle
        ) {
          Icon(
            if (isFavoriteFilterActive) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = stringResource(R.string.favorite)
          )
        }
      }

      if (trainings.isEmpty()) {
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
          targetState = trainings,
          label = "Agenda trainings",
          transitionSpec = {
            fadeIn() with fadeOut()
          }
        ) { trainings ->
          AgendaTrainings(
            trainings = trainings,
            selectedTrainingId = selectedTrainingId,
            onTrainingPressed = onTrainingPressed,
            showActions = showActions,
            actions = actions,
            onActionClick = { action, training ->
              onActionClick(action, training)
            }
          )
        }
      }
    }
  }
}

@Composable
fun AgendaTrainings(
  trainings: List<Training>,
  selectedTrainingId: String = "",
  onTrainingPressed: (Training) -> Unit,
  showActions: Boolean = true,
  onActionClick: (String, Training) -> Unit = { _, _ -> },
  actions: List<String> = listOf()
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
                  actions = actions,
                  selected = trainingItem.id == selectedTrainingId,
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
