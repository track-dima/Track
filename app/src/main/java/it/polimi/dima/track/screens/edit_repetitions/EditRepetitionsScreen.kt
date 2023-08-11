package it.polimi.dima.track.screens.edit_repetitions

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.CardSelector
import it.polimi.dima.track.common.composable.CoolDownCard
import it.polimi.dima.track.common.composable.DialogCancelButton
import it.polimi.dima.track.common.composable.DialogConfirmButton
import it.polimi.dima.track.common.composable.FullScreenDialog
import it.polimi.dima.track.common.composable.NumberPicker
import it.polimi.dima.track.common.composable.PickerState
import it.polimi.dima.track.common.composable.RegularCardEditor
import it.polimi.dima.track.common.composable.RepetitionBlockCard
import it.polimi.dima.track.common.composable.RepetitionsCard
import it.polimi.dima.track.common.composable.SegmentedControl
import it.polimi.dima.track.common.composable.WarmUpCard
import it.polimi.dima.track.common.composable.rememberPickerState
import it.polimi.dima.track.common.ext.card
import it.polimi.dima.track.common.ext.removeLeadingZeros
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
        ExtendedFloatingActionButton(
          onClick = {
            currentStep.value = viewModel.onAddClick(listOf())
            openEditDialog.value = true
            currentEditHierarchy.value = listOf()
            deleteOnDismissEdit.value = true
          },
          icon = { Icon(Icons.Filled.Add, stringResource(R.string.add_repetition)) },
          text = { Text(text = stringResource(R.string.add_repetition)) },
        )
        SmallFloatingActionButton(
          containerColor = MaterialTheme.colorScheme.primary,
          onClick = { viewModel.onAddBlockClick(listOf(), 3) }
        ) {
          Text(text = "3x")
        }
        SmallFloatingActionButton(
          containerColor = MaterialTheme.colorScheme.primary,
          onClick = { viewModel.onAddBlockClick(listOf(), 5) }
        ) {
          Text(text = "5x")
        }
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
            viewModel.onDeleteClick(currentEditHierarchy.value, currentStep.value)
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
      modifier = modifier
        .fillMaxWidth()
        .fillMaxHeight(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      ActionToolbar(
        title = R.string.edit_repetitions,
        modifier = Modifier.toolbarActions(),
        startActionIcon = Icons.Default.Close,
        startActionDescription = R.string.close,
        startAction = { viewModel.onCancelClick(popUpScreen) },
        endActionIcon = Icons.Default.Check,
        endActionDescription = R.string.confirm,
        endAction = { viewModel.onDoneClick(popUpScreen) }
      )

      Spacer(modifier = Modifier.spacer())

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
              TrainingStep.Type.WARM_UP -> WarmUpCard(
                trainingStep,
                onDeleteClick = { _, trainingStep ->
                  viewModel.onDeleteClick(
                    listOf(),
                    trainingStep
                  )
                },
                onEditClick = { _, trainingStep ->
                  openEditDialog.value = true
                  currentStep.value = trainingStep
                  currentEditHierarchy.value = listOf()
                }
              )

              TrainingStep.Type.COOL_DOWN -> CoolDownCard(
                trainingStep,
                onDeleteClick = { _, trainingStep ->
                  viewModel.onDeleteClick(
                    listOf(),
                    trainingStep
                  )
                },
                onEditClick = { _, trainingStep ->
                  openEditDialog.value = true
                  currentStep.value = trainingStep
                  currentEditHierarchy.value = listOf()
                  deleteOnDismissEdit.value = false
                }
              )

              TrainingStep.Type.REPETITION -> RepetitionsCard(
                trainingStep,
                showRecover = trainingStep.id != trainingSteps.last().id,
                onDeleteClick = { _, trainingStep ->
                  viewModel.onDeleteClick(
                    listOf(),
                    trainingStep
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
                    trainingStep
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
fun TimeSelectionDialog(
  title: String = "Duration time (h:m:s)",
  onDismissRequest: () -> Unit,
  onConfirm: (Boolean) -> Unit,
  durationSelection: Int,
  centsSelectable: Boolean = false,
  centsSelection: Int = 0,
  hourPickerState: PickerState,
  minutePickerState: PickerState,
  secondPickerState: PickerState,
  centsPickerState: PickerState = PickerState()
) {
  val displayCents = rememberSaveable { mutableStateOf(centsSelection != 0) }

  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(text = title)
    },
    text = {
      Surface(modifier = Modifier.height(if (centsSelectable) 220.dp else 160.dp)) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize()
        ) {
          if (centsSelectable) {
            SegmentedControl(
              items = listOf("HH:MM:SS", "MM:SS.CC"),
              defaultSelectedItemIndex = if (centsSelection == 0) 0 else 1,
              cornerRadius = 50,
              color = MaterialTheme.colorScheme.primary,
              onItemSelection = { displayCents.value = it == 1 }
            )

            Spacer(modifier = Modifier.height(16.dp))
          }

          if (displayCents.value) {
            TimeSelectionDialogContent(
              durationSelection = durationSelection,
              centsSelection = centsSelection,
              minutePickerState = minutePickerState,
              secondPickerState = secondPickerState,
              centsPickerState = centsPickerState,
              centsSelectable = true
            )
          } else {
            TimeSelectionDialogContent(
              durationSelection = durationSelection,
              hourPickerState = hourPickerState,
              minutePickerState = minutePickerState,
              secondPickerState = secondPickerState,
              centsSelectable = false
            )
          }
        }
      }
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirm(displayCents.value)
      }
    },
    dismissButton = {
      if (centsSelectable) {
        DialogCancelButton(text = R.string.clear) {
          hourPickerState.selectedItem = "0"
          minutePickerState.selectedItem = "00"
          secondPickerState.selectedItem = "00"
          centsPickerState.selectedItem = "00"
          onConfirm(displayCents.value)
        }
      }

      DialogCancelButton(text = R.string.dismiss) {
        onDismissRequest()
      }
    },
  )
}

@Composable
fun TimeSelectionDialogContent(
  durationSelection: Int,
  centsSelection: Int = 0,
  hourPickerState: PickerState = PickerState(),
  minutePickerState: PickerState,
  secondPickerState: PickerState,
  centsPickerState: PickerState = PickerState(),
  centsSelectable: Boolean = false
) {
  Surface(modifier = Modifier.height(160.dp)) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxSize()
    ) {

      Row(modifier = Modifier.fillMaxWidth()) {
        if (!centsSelectable) {
          NumberPicker(
            state = hourPickerState,
            items = remember { (0..23).map { it.toString() } },
            modifier = Modifier.weight(1f),
            visibleItemsCount = 3,
            startIndex = durationSelection / 3600,
            textModifier = Modifier.padding(8.dp),
            textStyle = TextStyle(fontSize = 24.sp)
          )
        }
        NumberPicker(
          state = minutePickerState,
          items = remember { (0..59).map { it.toString().padStart(2, '0') } },
          visibleItemsCount = 3,
          modifier = Modifier.weight(1f),
          startIndex = durationSelection / 60 % 60,
          textModifier = Modifier.padding(8.dp),
          textStyle = TextStyle(fontSize = 24.sp)
        )
        NumberPicker(
          state = secondPickerState,
          items = remember { (0..59).map { it.toString().padStart(2, '0') } },
          visibleItemsCount = 3,
          modifier = Modifier.weight(1f),
          startIndex = durationSelection % 60,
          textModifier = Modifier.padding(8.dp),
          textStyle = TextStyle(fontSize = 24.sp)
        )
        if (centsSelectable) {
          Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
          ) {
            Text(text = ".", fontSize = 24.sp)
          }
          NumberPicker(
            state = centsPickerState,
            items = remember { (0..99).map { it.toString().padStart(2, '0') } },
            visibleItemsCount = 3,
            modifier = Modifier.weight(1f),
            startIndex = centsSelection,
            textModifier = Modifier.padding(8.dp),
            textStyle = TextStyle(fontSize = 24.sp)
          )
        }
      }
    }
  }
}

@Composable
fun DistanceSelectionDialog(
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  distanceSelection: Int,
  distanceUnitSelection: String,
  mostSignificantDigitPickerState: PickerState,
  leastSignificantDigitsPickerState: PickerState,
  measurementPickerState: PickerState
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(text = "Distance")
    },
    text = {
      DistanceSelectionDialogContent(
        distanceSelection,
        distanceUnitSelection,
        mostSignificantDigitPickerState,
        leastSignificantDigitsPickerState,
        measurementPickerState
      )
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirm()
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.dismiss) {
        onDismissRequest()
      }
    }
  )
}

@Composable
fun DistanceSelectionDialogContent(
  distanceSelection: Int,
  distanceUnitSelection: String,
  mostSignificantDigitPickerState: PickerState,
  leastSignificantDigitsPickerState: PickerState,
  measurementPickerState: PickerState
) {
  Surface(modifier = Modifier.height(160.dp)) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxSize()
    ) {

      Row(modifier = Modifier.fillMaxWidth()) {
        NumberPicker(
          state = mostSignificantDigitPickerState,
          items = remember { (0..9).map { it.toString() } },
          modifier = Modifier.weight(1f),
          visibleItemsCount = 3,
          startIndex = distanceSelection / 100,
          textModifier = Modifier.padding(8.dp),
          textStyle = TextStyle(fontSize = 24.sp)
        )
        NumberPicker(
          state = leastSignificantDigitsPickerState,
          items = remember { (0..99).map { it.toString().padStart(2, '0') } },
          modifier = Modifier.weight(1f),
          visibleItemsCount = 3,
          startIndex = distanceSelection % 100,
          textModifier = Modifier.padding(8.dp),
          textStyle = TextStyle(fontSize = 24.sp)
        )
        NumberPicker(
          state = measurementPickerState,
          items = remember { listOf("m", "km", "mi") },
          visibleItemsCount = 3,
          modifier = Modifier.weight(1f),
          startIndex = when (distanceUnitSelection) {
            "km" -> 1
            "mi" -> 2
            else -> 0
          },
          textModifier = Modifier.padding(8.dp),
          textStyle = TextStyle(fontSize = 24.sp)
        )
      }
    }
  }
}

@Composable
fun RepetitionsSelectionDialog(
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  currentRepetitions: Int,
  repetitionsPickerState: PickerState
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(text = "Repetition number")
    },
    text = {
      Surface(modifier = Modifier.height(160.dp)) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize()
        ) {

          val values = remember { (2..50).map { it.toString() } }

          NumberPicker(
            state = repetitionsPickerState,
            items = values,
            visibleItemsCount = 3,
            startIndex = currentRepetitions - 2,
            textModifier = Modifier.padding(8.dp),
            textStyle = TextStyle(fontSize = 24.sp)
          )
        }
      }
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirm()
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.dismiss) {
        onDismissRequest()
      }
    }
  )
}

@Composable
fun RecoverSelectionDialog(
  title : String,
  onDismissRequest: () -> Unit,
  onConfirm: (String, Int, Int, String) -> Unit,
  currentRecoverType: String,
  currentRecoverDuration: Int,
  currentRecoverDistance: Int,
  currentRecoverDistanceUnit: String,
) {
  val recoverType = rememberSaveable { mutableStateOf(currentRecoverType) }
  val durationHourPickerState = rememberPickerState()
  val durationMinutePickerState = rememberPickerState()
  val durationSecondPickerState = rememberPickerState()
  val distanceMostSignificantDigitPickerState = rememberPickerState()
  val distanceLeastSignificantDigitsPickerState = rememberPickerState()
  val distanceMeasurementPickerState = rememberPickerState()

  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(text = title)
    },
    text = {
      Surface(modifier = Modifier.height(220.dp)) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize()
        ) {
          SegmentedControl(
            items = TrainingStep.DurationType.getOptions(),
            defaultSelectedItemIndex = if (currentRecoverType == TrainingStep.DurationType.TIME) 0 else 1,
            cornerRadius = 50,
            color = MaterialTheme.colorScheme.primary,
            onItemSelection = { index ->
              recoverType.value =
                if (index == 0) TrainingStep.DurationType.TIME else TrainingStep.DurationType.DISTANCE
            }
          )

          Spacer(modifier = Modifier.height(16.dp))

          if (recoverType.value == TrainingStep.DurationType.TIME) {
            TimeSelectionDialogContent(
              durationSelection = currentRecoverDuration,
              hourPickerState = durationHourPickerState,
              minutePickerState = durationMinutePickerState,
              secondPickerState = durationSecondPickerState,
              centsSelectable = false
            )
          } else {
            DistanceSelectionDialogContent(
              distanceSelection = currentRecoverDistance,
              distanceUnitSelection = currentRecoverDistanceUnit,
              mostSignificantDigitPickerState = distanceMostSignificantDigitPickerState,
              leastSignificantDigitsPickerState = distanceLeastSignificantDigitsPickerState,
              measurementPickerState = distanceMeasurementPickerState
            )
          }
        }
      }
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirm(
          recoverType.value,
          if (recoverType.value == TrainingStep.DurationType.TIME)
            durationHourPickerState.selectedItem.toInt() * 3600 + durationMinutePickerState.selectedItem.toInt() * 60 + durationSecondPickerState.selectedItem.toInt()
          else currentRecoverDuration,
          if (recoverType.value == TrainingStep.DurationType.DISTANCE)
            distanceMostSignificantDigitPickerState.selectedItem.toInt() * 100 + distanceLeastSignificantDigitsPickerState.selectedItem.toInt()
          else currentRecoverDistance,
          if (recoverType.value == TrainingStep.DurationType.DISTANCE)
            distanceMeasurementPickerState.selectedItem
          else currentRecoverDistanceUnit
        )
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.dismiss) {
        onDismissRequest()
      }
    }
  )
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
    Column {
      val typeSelection = currentStep.value.type
      val durationTypeSelection = currentStep.value.durationType
      val recoverTypeSelection = currentStep.value.recoverType

      CardSelector(
        label = R.string.type,
        options = TrainingStep.Type.getOptions(),
        selection = typeSelection,
        modifier = Modifier.card()
      ) { newValue -> currentStep.value = currentStep.value.copy(type = newValue) }

      Divider(
        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
      )

      CardSelector(
        label = R.string.duration_type,
        options = TrainingStep.DurationType.getOptions(),
        selection = durationTypeSelection,
        modifier = Modifier.card()
      ) { newValue -> currentStep.value = currentStep.value.copy(durationType = newValue) }

      if (currentStep.value.durationType == TrainingStep.DurationType.TIME) {
        val openTimeDialog = rememberSaveable { mutableStateOf(false) }

        RegularCardEditor(
          R.string.duration,
          Icons.Filled.Timer,
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

        // TODO change icon
        RegularCardEditor(
          R.string.distance,
          Icons.Filled.Straighten,
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
            mostSignificantDigitPickerState = mostSignificantDigitPickerState,
            leastSignificantDigitsPickerState = leastSignificantDigitsPickerState,
            measurementPickerState = measurementPickerState
          )
        }
      }

      if (currentStep.value.type == TrainingStep.Type.REPETITION) {
        Divider(
          modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

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
            Icons.Filled.Timer,
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

          // TODO change icon
          RegularCardEditor(
            R.string.distance,
            Icons.Filled.Straighten,
            "${currentStep.value.recoverDistance} ${currentStep.value.recoverDistanceUnit}",
            Modifier.card()
          ) {
            openDistanceDialog.value = true
          }

          val distanceSelection = currentStep.value.recoverDistance
          val distanceUnitSelection = currentStep.value.recoverDistanceUnit
          val mostSignificantDigitPickerState = rememberPickerState()
          val leastSignificantDigitsPickerState = rememberPickerState()
          val measurementPickerState = rememberPickerState()

          if (openDistanceDialog.value) {
            DistanceSelectionDialog(
              onDismissRequest = { openDistanceDialog.value = false },
              onConfirm = {
                openDistanceDialog.value = false
                currentStep.value = currentStep.value.copy(
                  recoverDistance = mostSignificantDigitPickerState.selectedItem.toInt() * 100 + leastSignificantDigitsPickerState.selectedItem.toInt(),
                  recoverDistanceUnit = measurementPickerState.selectedItem
                )
              },
              distanceSelection = distanceSelection,
              distanceUnitSelection = distanceUnitSelection,
              mostSignificantDigitPickerState = mostSignificantDigitPickerState,
              leastSignificantDigitsPickerState = leastSignificantDigitsPickerState,
              measurementPickerState = measurementPickerState
            )
          }
        }
      }
    }
  }
}




