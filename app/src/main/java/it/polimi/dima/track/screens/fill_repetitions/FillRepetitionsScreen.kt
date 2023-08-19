package it.polimi.dima.track.screens.fill_repetitions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.PaceSelectionDialog
import it.polimi.dima.track.common.composable.TimeSelectionDialog
import it.polimi.dima.track.common.composable.UnmodifiableStepsList
import it.polimi.dima.track.common.composable.rememberPickerState
import it.polimi.dima.track.common.ext.calculateTree
import it.polimi.dima.track.common.ext.extractCents
import it.polimi.dima.track.common.ext.extractPaceUnit
import it.polimi.dima.track.common.ext.paceToSeconds
import it.polimi.dima.track.common.ext.bigSpacer
import it.polimi.dima.track.common.ext.timeToSeconds
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.model.TrainingStep

@Composable
fun FillRepetitionsScreen(
  modifier: Modifier = Modifier,
  popUpScreen: () -> Unit,
  trainingId: String,
  viewModel: FillRepetitionsViewModel = hiltViewModel()
) {
  val training by viewModel.training
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


    val openResultDialog = rememberSaveable { mutableStateOf(false) }
    val currentHierarchy = rememberSaveable { mutableStateOf(listOf<String>()) }
    val currentResultIndex = rememberSaveable { mutableStateOf(0) }
    val currentTrainingStep = rememberSaveable { mutableStateOf("") }
    val currentResult = rememberSaveable { mutableStateOf("") }
    val currentDurationType = rememberSaveable { mutableStateOf("") }

    val resultHourPickerState = rememberPickerState()
    val resultMinutePickerState = rememberPickerState()
    val resultSecondPickerState = rememberPickerState()
    val resultCentsPickerState = rememberPickerState()
    val resultPaceUnitPickerState = rememberPickerState()

    if (openResultDialog.value) {
      if (currentDurationType.value == TrainingStep.DurationType.DISTANCE) {
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
          durationSelection = currentResult.value.timeToSeconds(),
          centsSelectable = true,
          centsSelection = currentResult.value.extractCents(),
          hourPickerState = resultHourPickerState,
          minutePickerState = resultMinutePickerState,
          secondPickerState = resultSecondPickerState,
          centsPickerState = resultCentsPickerState
        )
      } else {
        PaceSelectionDialog(
          title = stringResource(id = R.string.fill_result),
          onDismissRequest = { openResultDialog.value = false },
          onConfirm = {
            openResultDialog.value = false
            viewModel.onTimeFillClick(
              hierarchy = currentHierarchy.value,
              index = currentResultIndex.value,
              trainingStepId = currentTrainingStep.value,
              result = "${resultMinutePickerState.selectedItem}:${resultSecondPickerState.selectedItem} ${resultPaceUnitPickerState.selectedItem}"
            )
          },
          paceSelection = currentResult.value.paceToSeconds(),
          paceUnitSelection = currentResult.value.extractPaceUnit(),
          minutePickerState = resultMinutePickerState,
          secondPickerState = resultSecondPickerState,
          paceUnitPickerState = resultPaceUnitPickerState
        )
      }
    }

    Column(
      modifier = modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
    ) {
      Spacer(modifier = Modifier.bigSpacer())
      val tree = training.calculateTree()

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .height(max(32.dp + 78.dp * (tree.first) + 70.dp * (tree.second), 128.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        UnmodifiableStepsList(
          trainingSteps = trainingSteps,
          fillTime = true,
          onTimeFillClick = { hierarchy, index, step ->
            currentHierarchy.value = hierarchy
            currentResultIndex.value = index
            currentTrainingStep.value = step.id
            currentResult.value = if (step.results.size > index) step.results[index] else ""
            currentDurationType.value = step.durationType
            openResultDialog.value = true
          },
        )
      }
    }
  }
}


