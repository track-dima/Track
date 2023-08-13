package it.polimi.dima.track.screens.agenda

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.DeleteDialog
import it.polimi.dima.track.common.ext.getDay
import it.polimi.dima.track.common.ext.getDayName
import it.polimi.dima.track.common.ext.isToday
import it.polimi.dima.track.common.ext.smallSpacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.common.utils.NavigationType
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.screens.training.TrainingActionOption
import it.polimi.dima.track.screens.training.TrainingCard
import java.util.Calendar
import java.util.Date
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AgendaScreen(
  openScreen: (String) -> Unit,
  modifier: Modifier = Modifier,
  navigationType: NavigationType,
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

  Scaffold(
    floatingActionButton = {
      if (navigationType == NavigationType.BOTTOM_NAVIGATION) {
        LargeFloatingActionButton(
          onClick = { viewModel.onAddClick(openScreen) },
          modifier = modifier.padding(16.dp)
        ) {
          Icon(
            Icons.Filled.Add,
            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
            contentDescription = stringResource(id = R.string.add_training),
          )
        }
      }
    }
  ) {
    val trainings = viewModel.trainings.collectAsStateWithLifecycle(emptyList())

    // TODO is this sorting efficient?
    val sortedTrainings = trainings.value.sortedByDescending { it.dueDate }
    val options by viewModel.options

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
    ) {
      ActionToolbar(
        title = R.string.agenda,
        modifier = Modifier.toolbarActions(),
        endActionIcon = Icons.Default.Settings,
        endActionDescription = R.string.settings,
        endAction = { viewModel.onSettingsClick(openScreen) }
      )

      Spacer(modifier = Modifier.smallSpacer())

      LazyColumn {
        val groupedTrainings = sortedTrainings.groupBy {
          // TODO simplify and use functions from DateExt
          val calendar = Calendar.getInstance()
          if (it.dueDate != null) {
            calendar.time = it.dueDate
            val startOfWeek = calendar.clone() as Calendar
            startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
            val endOfWeek = calendar.clone() as Calendar
            endOfWeek.set(Calendar.DAY_OF_WEEK, endOfWeek.firstDayOfWeek)
            endOfWeek.add(Calendar.DAY_OF_WEEK, 6)

            val startMonth =
              startOfWeek.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
            val endMonth =
              endOfWeek.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

            if (startMonth == endMonth) {
              "${startOfWeek.get(Calendar.DAY_OF_MONTH)}-${endOfWeek.get(Calendar.DAY_OF_MONTH)} " +
                  "$startMonth ${startOfWeek.get(Calendar.YEAR)}"
            } else {
              "${startOfWeek.get(Calendar.DAY_OF_MONTH)} $startMonth - " +
                  "${endOfWeek.get(Calendar.DAY_OF_MONTH)} $endMonth ${startOfWeek.get(Calendar.YEAR)}"
            }
          } else "No date"
        }

        groupedTrainings.forEach { (weekOfYear, trainingsForWeek) ->
          item {
            Text(
              text = weekOfYear,
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
                Column (
                  modifier = Modifier.width(48.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ){
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
                      onActionClick = { action ->
                        when (TrainingActionOption.getByTitle(action)) {
                          TrainingActionOption.DeleteTask -> {
                            currentTraining.value = trainingItem.id
                            openDeleteDialog.value = true
                          }

                          else -> viewModel.onTrainingActionClick(openScreen, trainingItem, action)
                        }
                      }
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  LaunchedEffect(viewModel) { viewModel.loadTaskOptions() }
}
