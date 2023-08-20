package it.polimi.dima.track.screens.edit_repetitions

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.CardSelector
import it.polimi.dima.track.common.composable.DistanceSelectionDialog
import it.polimi.dima.track.common.composable.FullScreenDialog
import it.polimi.dima.track.common.composable.RecoverSelectionDialog
import it.polimi.dima.track.common.composable.RegularCardEditor
import it.polimi.dima.track.common.composable.RepetitionBlockCard
import it.polimi.dima.track.common.composable.RepetitionsCard
import it.polimi.dima.track.common.composable.RepetitionsSelectionDialog
import it.polimi.dima.track.common.composable.SimpleExerciseCard
import it.polimi.dima.track.common.composable.TimeSelectionDialog
import it.polimi.dima.track.common.composable.rememberPickerState
import it.polimi.dima.track.common.ext.card
import it.polimi.dima.track.common.ext.secondsToHhMmSs
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.model.TrainingStep
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditRepetitionsScreen(
  popUpScreen: () -> Unit,
  trainingId: String,
  modifier: Modifier = Modifier,
  viewModel: EditRepetitionsViewModel = hiltViewModel()
) {
  // TODO is resetting on change orientation

  val trainingSteps by viewModel.trainingSteps

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }

  val openEditDialog = rememberSaveable { mutableStateOf(false) }
  // TODO remember saveable
  val currentStep = remember { mutableStateOf(TrainingStep()) }
  val currentEditHierarchy = rememberSaveable { mutableStateOf(listOf<String>()) }
  val deleteOnDismissEdit = rememberSaveable { mutableStateOf(false) }

  Scaffold(
    floatingActionButton = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        EditRepetitionsFABs(
          onPrincipalActionClick = {
            currentStep.value = viewModel.onAddClick(listOf())
            openEditDialog.value = true
            currentEditHierarchy.value = listOf()
            deleteOnDismissEdit.value = true
          },
          on3xClick = { viewModel.onAddBlockClick(listOf(), 3) },
          on5xClick = { viewModel.onAddBlockClick(listOf(), 5) }
        )
      }
    }
  ) {

    val openRepetitionsDialog = rememberSaveable { mutableStateOf(false) }
    val openRecoverDialog = rememberSaveable { mutableStateOf(false) }
    val repetitionsPickerState = rememberPickerState()
    val currentRepetitions = rememberSaveable { mutableStateOf(2) }
    val currentHierarchy = rememberSaveable { mutableStateOf(listOf<String>()) }

    if (openRepetitionsDialog.value) {
      RepetitionsSelectionDialog(
        onDismissRequest = { openRepetitionsDialog.value = false },
        onConfirm = {
          openRepetitionsDialog.value = false
          viewModel.onEditRepetitionsClick(
            currentHierarchy.value,
            repetitionsPickerState.selectedItem.toInt()
          )
        },
        currentRepetitions = currentRepetitions.value,
        repetitionsPickerState = repetitionsPickerState,
      )
    }

    val currentRecoverType = rememberSaveable { mutableStateOf(TrainingStep.DurationType.TIME) }
    val currentRecoverDuration = rememberSaveable { mutableStateOf(0) }
    val currentRecoverDistance = rememberSaveable { mutableStateOf(0) }
    val currentRecoverDistanceUnit = rememberSaveable { mutableStateOf("m") }
    val currentIsExtraRecover = rememberSaveable { mutableStateOf(false) }

    if (openRecoverDialog.value) {
      RecoverSelectionDialog(
        title = if (currentIsExtraRecover.value) {
          "Recover after block"
        } else {
          "In between recover"
        },
        onDismissRequest = { openRecoverDialog.value = false },
        onConfirm = { recoverType, recoverDuration, recoverDistance, recoverDistanceUnit ->
          openRecoverDialog.value = false
          viewModel.onEditRecoverClick(
            currentHierarchy.value,
            recoverType,
            recoverDuration,
            recoverDistance,
            recoverDistanceUnit,
            currentIsExtraRecover.value
          )
        },
        currentRecoverType = currentRecoverType.value,
        currentRecoverDuration = currentRecoverDuration.value,
        currentRecoverDistance = currentRecoverDistance.value,
        currentRecoverDistanceUnit = currentRecoverDistanceUnit.value,
      )
    }

    if (openEditDialog.value) {
      EditStepDialog(
        onDismissRequest = {
          openEditDialog.value = false
          if (deleteOnDismissEdit.value) {
            viewModel.onDeleteClick(currentEditHierarchy.value, currentStep.value.id)
            deleteOnDismissEdit.value = false
          }
        },
        onConfirm = {
          openEditDialog.value = false
          viewModel.onEditClick(currentEditHierarchy.value, currentStep.value)
        },
        currentStep = currentStep
      )
    }

    Column(
      modifier = modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      ActionToolbar(
        title = R.string.edit_repetitions,
        modifier = Modifier.toolbarActions(),
        startActionIcon = Icons.Rounded.Close,
        startActionDescription = R.string.close,
        startAction = { viewModel.onCancelClick(popUpScreen) },
        endActionIcon = Icons.Rounded.Check,
        endActionDescription = R.string.confirm,
        endAction = { viewModel.onDoneClick(popUpScreen) }
      )

      val state = rememberReorderableLazyListState(
        onMove = { from, to -> viewModel.moveStep(listOf(), from, to) }
      )
      LazyColumn(
        state = state.listState,
        modifier = Modifier
          .reorderable(state)
          .detectReorderAfterLongPress(state)
          .weight(1f)
      ) {
        items(trainingSteps, { it.id }) { trainingStep ->
          ReorderableItem(state, key = trainingStep.id) {

            when (trainingStep.type) {
              TrainingStep.Type.EXERCISES,
              TrainingStep.Type.STRENGTH,
              TrainingStep.Type.HURDLES -> SimpleExerciseCard(
                type = trainingStep.type,
                onlyDuration = true,
                trainingStep = trainingStep,
                onDeleteClick = { _, trainingStep ->
                  viewModel.onDeleteClick(
                    listOf(),
                    trainingStep.id
                  )
                },
                onEditClick = { _, trainingStep ->
                  openEditDialog.value = true
                  currentStep.value = trainingStep
                  currentEditHierarchy.value = listOf()
                }
              )

              TrainingStep.Type.WARM_UP,
              TrainingStep.Type.COOL_DOWN -> SimpleExerciseCard(
                type = trainingStep.type,
                onlyDuration = false,
                trainingStep = trainingStep,
                onDeleteClick = { _, trainingStep ->
                  viewModel.onDeleteClick(
                    listOf(),
                    trainingStep.id
                  )
                },
                onEditClick = { _, trainingStep ->
                  openEditDialog.value = true
                  currentStep.value = trainingStep
                  currentEditHierarchy.value = listOf()
                }
              )

              TrainingStep.Type.REPETITION -> RepetitionsCard(
                trainingStep,
                showRecover = trainingStep.id != trainingSteps.last().id,
                onDeleteClick = { _, trainingStep ->
                  viewModel.onDeleteClick(
                    listOf(),
                    trainingStep.id
                  )
                },
                onEditClick = { _, trainingStep ->
                  openEditDialog.value = true
                  currentStep.value = trainingStep
                  currentEditHierarchy.value = listOf()
                  deleteOnDismissEdit.value = false
                },
              )

              TrainingStep.Type.REPETITION_BLOCK -> RepetitionBlockCard(
                trainingStep,
                onDeleteClick = { hierarchy, trainingStep ->
                  viewModel.onDeleteClick(
                    hierarchy,
                    trainingStep.id
                  )
                },
                onEditClick = { hierarchy, trainingStep ->
                  openEditDialog.value = true
                  currentStep.value = trainingStep
                  currentEditHierarchy.value = hierarchy
                  deleteOnDismissEdit.value = false
                },
                onAddClick = { hierarchy ->
                  openEditDialog.value = true
                  currentStep.value = viewModel.onAddClick(hierarchy)
                  currentEditHierarchy.value = hierarchy
                  deleteOnDismissEdit.value = true
                },
                onAddBlockClick = { hierarchy, repetitions ->
                  viewModel.onAddBlockClick(
                    hierarchy,
                    repetitions
                  )
                },
                onRepetitionsClick = { hierarchy, repetitions ->
                  openRepetitionsDialog.value = true
                  currentRepetitions.value = repetitions
                  currentHierarchy.value = hierarchy
                },
                onRecoverClick = { hierarchy, recoverType, recoverDuration, recoverDistance, recoverDistanceUnit, extraRecover ->
                  openRecoverDialog.value = true
                  currentRecoverType.value = recoverType
                  currentRecoverDuration.value = recoverDuration
                  currentRecoverDistance.value = recoverDistance
                  currentRecoverDistanceUnit.value = recoverDistanceUnit
                  currentHierarchy.value = hierarchy
                  currentIsExtraRecover.value = extraRecover
                },
                onMove = { hierarchy, from, to -> viewModel.moveStep(hierarchy, from, to) },
                lastStep = trainingStep.id == trainingSteps.last().id,
                level = 1
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun EditRepetitionsFABs(
  onPrincipalActionClick: () -> Unit,
  on3xClick: () -> Unit,
  on5xClick: () -> Unit
) {
  ExtendedFloatingActionButton(
    onClick = onPrincipalActionClick,
    icon = { Icon(Icons.Rounded.Add, stringResource(R.string.add_repetition)) },
    text = { Text(text = stringResource(R.string.add_repetition)) },
  )
  SmallFloatingActionButton(
    containerColor = MaterialTheme.colorScheme.primary,
    onClick = on3xClick
  ) {
    Text(text = "3x")
  }
  SmallFloatingActionButton(
    containerColor = MaterialTheme.colorScheme.primary,
    onClick = on5xClick
  ) {
    Text(text = "5x")
  }
}

@Composable
fun EditStepDialog(
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  currentStep: MutableState<TrainingStep>
) {
  FullScreenDialog(
    onDismissRequest = onDismissRequest,
    onConfirm = onConfirm,
    title = stringResource(id = R.string.edit_repetition),
  ) {
    Column(
      modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
      EditStepDialogContent(currentStep)
    }
  }
}

@Composable
private fun EditStepDialogContent(currentStep: MutableState<TrainingStep>) {
  val typeSelection = currentStep.value.type
  val durationTypeSelection = currentStep.value.durationType
  val recoverTypeSelection = currentStep.value.recoverType

  CardSelector(
    label = R.string.type,
    options = TrainingStep.Type.getOptions(),
    selection = typeSelection,
    modifier = Modifier.card()
  ) { newValue ->
    currentStep.value = currentStep.value.copy(type = newValue)
    when (newValue) {
      TrainingStep.Type.WARM_UP -> currentStep.value =
        currentStep.value.copy(durationType = TrainingStep.DurationType.TIME, duration = 600)

      TrainingStep.Type.COOL_DOWN -> currentStep.value =
        currentStep.value.copy(durationType = TrainingStep.DurationType.TIME, duration = 300)

      TrainingStep.Type.EXERCISES,
      TrainingStep.Type.STRENGTH,
      TrainingStep.Type.HURDLES -> currentStep.value =
        currentStep.value.copy(durationType = TrainingStep.DurationType.TIME, duration = 1200)

      else -> Unit
    }
  }

  Divider(
    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
  )

  EditStepDurationSelection(durationTypeSelection, currentStep)

  if (currentStep.value.type == TrainingStep.Type.REPETITION) {
    Divider(
      modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
    )

    EditStepRecoverSelection(recoverTypeSelection, currentStep)
  }
}

@Composable
private fun EditStepRecoverSelection(
  recoverTypeSelection: String,
  currentStep: MutableState<TrainingStep>
) {
  CardSelector(
    label = R.string.recover_type,
    options = TrainingStep.DurationType.getFullOptions(),
    selection = recoverTypeSelection,
    modifier = Modifier.card()
  ) { newValue ->
    currentStep.value = currentStep.value.copy(
      recoverType = newValue,
      recover = newValue != TrainingStep.DurationType.NONE
    )
  }

  if (currentStep.value.recoverType == TrainingStep.DurationType.TIME) {
    val openTimeDialog = rememberSaveable { mutableStateOf(false) }

    RegularCardEditor(
      R.string.recover_duration,
      Icons.Rounded.Timer,
      currentStep.value.recoverDuration.secondsToHhMmSs(),
      Modifier.card()
    ) {
      openTimeDialog.value = true
    }

    val durationSelection = currentStep.value.recoverDuration
    val durationHourPickerState = rememberPickerState()
    val durationMinutePickerState = rememberPickerState()
    val durationSecondPickerState = rememberPickerState()

    if (openTimeDialog.value) {
      TimeSelectionDialog(
        onDismissRequest = { openTimeDialog.value = false },
        onConfirm = {
          openTimeDialog.value = false
          currentStep.value = currentStep.value.copy(
            recoverDuration = durationHourPickerState.selectedItem.toInt() * 3600 + durationMinutePickerState.selectedItem.toInt() * 60 + durationSecondPickerState.selectedItem.toInt()
          )
        },
        durationSelection = durationSelection,
        hourPickerState = durationHourPickerState,
        minutePickerState = durationMinutePickerState,
        secondPickerState = durationSecondPickerState
      )
    }
  } else if (currentStep.value.recoverType == TrainingStep.DurationType.DISTANCE) {
    val openDistanceDialog = rememberSaveable { mutableStateOf(false) }

    RegularCardEditor(
      R.string.distance,
      Icons.Rounded.Straighten,
      "${currentStep.value.recoverDistance} ${currentStep.value.recoverDistanceUnit}",
      Modifier.card()
    ) {
      openDistanceDialog.value = true
    }

    val distanceSelection = currentStep.value.recoverDistance
    val distanceUnitSelection = currentStep.value.recoverDistanceUnit
    val mostSignificantDigitsPickerState = rememberPickerState()
    val leastSignificantDigitsPickerState = rememberPickerState()
    val measurementPickerState = rememberPickerState()

    if (openDistanceDialog.value) {
      DistanceSelectionDialog(
        onDismissRequest = { openDistanceDialog.value = false },
        onConfirm = {
          openDistanceDialog.value = false
          currentStep.value = currentStep.value.copy(
            recoverDistance = mostSignificantDigitsPickerState.selectedItem.toInt() * 100 + leastSignificantDigitsPickerState.selectedItem.toInt(),
            recoverDistanceUnit = measurementPickerState.selectedItem
          )
        },
        distanceSelection = distanceSelection,
        distanceUnitSelection = distanceUnitSelection,
        mostSignificantDigitsPickerState = mostSignificantDigitsPickerState,
        leastSignificantDigitsPickerState = leastSignificantDigitsPickerState,
        measurementPickerState = measurementPickerState
      )
    }
  }
}

@Composable
private fun EditStepDurationSelection(
  durationTypeSelection: String,
  currentStep: MutableState<TrainingStep>
) {
  CardSelector(
    label = R.string.duration_type,
    options = TrainingStep.DurationType.getOptions(),
    selection = durationTypeSelection,
    modifier = Modifier.card()
  ) { newValue ->
    currentStep.value = currentStep.value.copy(durationType = newValue, results = listOf())
  }

  if (currentStep.value.durationType == TrainingStep.DurationType.TIME) {
    val openTimeDialog = rememberSaveable { mutableStateOf(false) }

    RegularCardEditor(
      R.string.duration,
      Icons.Rounded.Timer,
      currentStep.value.duration.secondsToHhMmSs(),
      Modifier.card()
    ) {
      openTimeDialog.value = true
    }

    val durationSelection = currentStep.value.duration
    val durationHourPickerState = rememberPickerState()
    val durationMinutePickerState = rememberPickerState()
    val durationSecondPickerState = rememberPickerState()

    if (openTimeDialog.value) {
      TimeSelectionDialog(
        onDismissRequest = { openTimeDialog.value = false },
        onConfirm = {
          openTimeDialog.value = false
          currentStep.value = currentStep.value.copy(
            duration = durationHourPickerState.selectedItem.toInt() * 3600 + durationMinutePickerState.selectedItem.toInt() * 60 + durationSecondPickerState.selectedItem.toInt()
          )
        },
        durationSelection = durationSelection,
        hourPickerState = durationHourPickerState,
        minutePickerState = durationMinutePickerState,
        secondPickerState = durationSecondPickerState
      )
    }
  } else {
    val openDistanceDialog = rememberSaveable { mutableStateOf(false) }

    RegularCardEditor(
      R.string.distance,
      Icons.Rounded.Straighten,
      "${currentStep.value.distance} ${currentStep.value.distanceUnit}",
      Modifier.card()
    ) {
      openDistanceDialog.value = true
    }

    val distanceSelection = currentStep.value.distance
    val distanceUnitSelection = currentStep.value.distanceUnit
    val mostSignificantDigitPickerState = rememberPickerState()
    val leastSignificantDigitsPickerState = rememberPickerState()
    val measurementPickerState = rememberPickerState()

    if (openDistanceDialog.value) {
      DistanceSelectionDialog(
        onDismissRequest = { openDistanceDialog.value = false },
        onConfirm = {
          openDistanceDialog.value = false
          currentStep.value = currentStep.value.copy(
            distance = mostSignificantDigitPickerState.selectedItem.toInt() * 100 + leastSignificantDigitsPickerState.selectedItem.toInt(),
            distanceUnit = measurementPickerState.selectedItem
          )
        },
        distanceSelection = distanceSelection,
        distanceUnitSelection = distanceUnitSelection,
        mostSignificantDigitsPickerState = mostSignificantDigitPickerState,
        leastSignificantDigitsPickerState = leastSignificantDigitsPickerState,
        measurementPickerState = measurementPickerState
      )
    }
  }
}




