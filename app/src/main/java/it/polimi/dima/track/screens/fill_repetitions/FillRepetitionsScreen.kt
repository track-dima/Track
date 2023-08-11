package it.polimi.dima.track.screens.fill_repetitions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.UnmodifiableStepsList
import it.polimi.dima.track.common.composable.rememberPickerState
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.screens.edit_repetitions.TimeSelectionDialog
import it.polimi.dima.track.screens.edit_repetitions.secondsToHhMmSs

@Composable
fun FillRepetitionsScreen(
  modifier: Modifier = Modifier,
  popUpScreen: () -> Unit,
  trainingId: String,
  viewModel: FillRepetitionsViewModel = hiltViewModel()
) {
  val trainingSteps by viewModel.trainingSteps

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }


  Column(
    modifier = modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    ActionToolbar(
      title = R.string.fill_training,
      modifier = Modifier.toolbarActions(),
      startActionIcon = Icons.Default.Close,
      startActionDescription = R.string.close,
      startAction = { viewModel.onCancelClick(popUpScreen) },
      endActionIcon = Icons.Default.Check,
      endActionDescription = R.string.confirm,
      endAction = { viewModel.onDoneClick(popUpScreen) }
    )

    Spacer(modifier = Modifier.spacer())

    val openResultDialog = rememberSaveable { mutableStateOf(false) }
    val currentHierarchy = rememberSaveable { mutableStateOf(listOf<String>()) }
    val currentResultIndex = rememberSaveable { mutableStateOf(0) }
    val currentTrainingStep = rememberSaveable { mutableStateOf("") }
    val currentResult = rememberSaveable { mutableStateOf("") }

    val resultHourPickerState = rememberPickerState()
    val resultMinutePickerState = rememberPickerState()
    val resultSecondPickerState = rememberPickerState()
    val resultCentsPickerState = rememberPickerState()

    if (openResultDialog.value) {
      TimeSelectionDialog(
        title = stringResource(id = R.string.fill_result),
        onDismissRequest = { openResultDialog.value = false },
        onConfirm = { cents ->
          openResultDialog.value = false
          viewModel.onTimeFillClick(
            hierarchy = currentHierarchy.value,
            index = currentResultIndex.value,
            trainingStepId = currentTrainingStep.value,
            result = if (cents) "${resultMinutePickerState.selectedItem}:${resultSecondPickerState.selectedItem}.${resultCentsPickerState.selectedItem}"
            else "${resultHourPickerState.selectedItem}:${resultMinutePickerState.selectedItem}:${resultSecondPickerState.selectedItem}"
          )
        },
        durationSelection = timeToSeconds(currentResult.value),
        centsSelectable = true,
        centsSelection = extractCents(currentResult.value),
        hourPickerState = resultHourPickerState,
        minutePickerState = resultMinutePickerState,
        secondPickerState = resultSecondPickerState,
        centsPickerState = resultCentsPickerState
      )
    }

    UnmodifiableStepsList(
      trainingSteps = trainingSteps,
      fillTime = true,
      onTimeFillClick = { hierarchy, index, step ->
        currentHierarchy.value = hierarchy
        currentResultIndex.value = index
        currentTrainingStep.value = step.id
        currentResult.value = if (step.results.size > index) step.results[index] else ""
        openResultDialog.value = true
      },
    )
  }
}

fun timeToSeconds(result: String): Int {
  if (result.isEmpty()) return 0
  val split = result.split(":")
  return if (split.size < 3) {
    split[0].toInt() * 60 + split[1].split(".")[0].toInt()
  } else split[0].toInt() * 3600 + split[1].toInt() * 60 + split[2].toInt()
}

fun extractCents(result: String): Int {
  if (result.isEmpty()) return 0
  val split = result.split(".")
  if (split.size < 2) return 0
  return split[1].toInt()
}

