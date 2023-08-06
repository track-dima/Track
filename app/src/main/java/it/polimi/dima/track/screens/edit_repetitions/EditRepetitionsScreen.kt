package it.polimi.dima.track.screens.edit_repetitions

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.CardSelector
import it.polimi.dima.track.common.composable.FullScreenDialog
import it.polimi.dima.track.common.composable.NumberPicker
import it.polimi.dima.track.common.composable.PickerState
import it.polimi.dima.track.common.composable.rememberPickerState
import it.polimi.dima.track.common.ext.card
import it.polimi.dima.track.common.ext.fieldModifier
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.model.TrainingStep
import org.burnoutcrew.reorderable.ItemPosition
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

  Scaffold(
    floatingActionButton = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ExtendedFloatingActionButton(
          onClick = { viewModel.onAddClick(listOf()) },
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
          ) },
        currentRepetitions = currentRepetitions.value,
        repetitionsPickerState = repetitionsPickerState,
      )
    }


    val openEditDialog = rememberSaveable { mutableStateOf(false) }
    // TODO remember saveable
    val currentStep = remember { mutableStateOf(TrainingStep()) }
    val currentEditHierarchy = rememberSaveable { mutableStateOf(listOf<String>()) }

    if (openEditDialog.value) {
      EditStepDialog(
        onDismissRequest = { openEditDialog.value = false },
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
              TrainingStep.Type.WARM_UP -> WarmUpCardContent(
                trainingStep,
                onDeleteClick = { _, trainingStep -> viewModel.onDeleteClick(listOf(), trainingStep) },
                onEditClick = { _, trainingStep ->
                  openEditDialog.value = true
                  currentStep.value = trainingStep
                  currentEditHierarchy.value = listOf() }
              )
              TrainingStep.Type.COOL_DOWN -> CoolDownCardContent(
                trainingStep,
                onDeleteClick = { _, trainingStep -> viewModel.onDeleteClick(listOf(), trainingStep) },
                onEditClick = { _, trainingStep ->
                  openEditDialog.value = true
                  currentStep.value = trainingStep
                  currentEditHierarchy.value = listOf() }
              )
              TrainingStep.Type.REPETITION -> RepetitionsCardContent(
                trainingStep,
                onDeleteClick = { _, trainingStep -> viewModel.onDeleteClick(listOf(), trainingStep) },
                onEditClick = { _, trainingStep ->
                  openEditDialog.value = true
                  currentStep.value = trainingStep
                  currentEditHierarchy.value = listOf() },
              )
              TrainingStep.Type.REPETITION_BLOCK -> RepetitionBlockContent(
                trainingStep,
                onDeleteClick = { hierarchy, trainingStep -> viewModel.onDeleteClick(hierarchy, trainingStep) },
                onEditClick = { hierarchy, trainingStep ->
                  openEditDialog.value = true
                  currentStep.value = trainingStep
                  currentEditHierarchy.value = hierarchy },
                onAddClick = { hierarchy -> viewModel.onAddClick(hierarchy) },
                onAddBlockClick = { hierarchy, repetitions -> viewModel.onAddBlockClick(hierarchy, repetitions) },
                onRepetitionsClick = { hierarchy, repetitions ->
                  openRepetitionsDialog.value = true
                  currentRepetitions.value = repetitions
                  currentHierarchy.value = hierarchy },
                onMove = { hierarchy, from, to -> viewModel.moveStep(hierarchy, from, to) }
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarmUpCardContent(
  trainingStep: TrainingStep,
  onDeleteClick: (List<String>, TrainingStep) -> Unit,
  onEditClick: (List<String>, TrainingStep) -> Unit
) {
  OutlinedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(70.dp),
    onClick = { onEditClick(listOf(trainingStep.id), trainingStep) }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column (
        modifier = Modifier.padding(8.dp, 0.dp)
      ) {
        Row {
          Text(text = "Riscaldamento", fontWeight = FontWeight.Bold)
        }
        Row {
          if (trainingStep.durationType == TrainingStep.DurationType.TIME)
            Text(text = trainingStep.duration.toString() + 's')
          else
            Text(text = trainingStep.distance.toString() + trainingStep.distanceUnit)
        }
      }
      IconButton(
        modifier = Modifier.padding(8.dp),
        onClick = { onDeleteClick(listOf(trainingStep.id), trainingStep) }
      ) {
        Icon(
          Icons.Outlined.Delete,
          contentDescription = stringResource(R.string.delete_repetition)
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoolDownCardContent(
    trainingStep: TrainingStep,
    onDeleteClick: (List<String>, TrainingStep) -> Unit,
    onEditClick: (List<String>, TrainingStep) -> Unit
) {
  OutlinedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(70.dp),
    onClick = { onEditClick(listOf(trainingStep.id), trainingStep) }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.padding(8.dp, 0.dp)
      ) {
        Row {
          Text(text = "Defaticamento", fontWeight = FontWeight.Bold)
        }
        Row {
          if (trainingStep.durationType == TrainingStep.DurationType.TIME)
            Text(text = trainingStep.duration.toString() + 's')
          else
            Text(text = trainingStep.distance.toString() + trainingStep.distanceUnit)
        }
      }
      IconButton(
        modifier = Modifier.padding(8.dp),
        onClick = { onDeleteClick(listOf(trainingStep.id), trainingStep) }
      ) {
        Icon(
          Icons.Outlined.Delete,
          contentDescription = stringResource(R.string.delete_repetition)
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepetitionsCardContent(
  trainingStep: TrainingStep,
  onDeleteClick: (List<String>, TrainingStep) -> Unit,
  onEditClick: (List<String>, TrainingStep) -> Unit
) {
  ElevatedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(70.dp),
    onClick = { onEditClick(listOf(trainingStep.id), trainingStep) }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column (
        modifier = Modifier.padding(8.dp, 0.dp)
      ) {
        Row {
          if (trainingStep.durationType == TrainingStep.DurationType.TIME) {
            Text(text = "Duration: ", fontWeight = FontWeight.Bold)
            Text(text = trainingStep.duration.toString() + 's')
          }
          else {
            Text(text = "Distance: ", fontWeight = FontWeight.Bold)
            Text(text = trainingStep.distance.toString() + trainingStep.distanceUnit)
          }
        }
        Row {
          if (trainingStep.recoverType == TrainingStep.DurationType.TIME) {
            Text(text = "Recover time: ", fontWeight = FontWeight.Bold)
            Text(text = trainingStep.recoverDuration.toString() + 's')
          }
          else {
            Text(text = "Recover distance: ", fontWeight = FontWeight.Bold)
            Text(text = trainingStep.recoverDistance.toString() + trainingStep.recoverDistanceUnit)
          }
        }
      }
      IconButton(
        modifier = Modifier.padding(8.dp),
        onClick = { onDeleteClick(listOf(trainingStep.id), trainingStep) }
      ) {
        Icon(
          Icons.Outlined.Delete,
          contentDescription = stringResource(R.string.delete_repetition)
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepetitionBlockContent(
    repetitionBlock: TrainingStep,
    onDeleteClick: (List<String>, TrainingStep) -> Unit,
    onEditClick: (List<String>, TrainingStep) -> Unit,
    onAddClick: (List<String>) -> Unit,
    onAddBlockClick: (List<String>, Int) -> Unit,
    onRepetitionsClick: (List<String>, Int) -> Unit,
    onMove: (List<String>, ItemPosition, ItemPosition) -> Unit
) {
  OutlinedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(70.dp + 80.dp * (repetitionBlock.stepsInRepetition.size + 1)),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp, 0.dp, 0.dp, 0.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Button(onClick = { onRepetitionsClick(listOf(repetitionBlock.id), repetitionBlock.repetitions) }) {
        Text(text = repetitionBlock.repetitions.toString() + " times")
      }
      IconButton(
        modifier = Modifier.padding(8.dp),
        onClick = { onDeleteClick(listOf(), repetitionBlock) }
      ) {
        Icon(
          Icons.Outlined.Delete,
          contentDescription = stringResource(R.string.delete_repetition)
        )
      }
    }

    val state = rememberReorderableLazyListState(
      onMove = { from, to -> onMove(listOf(repetitionBlock.id), from, to) }
    )
    LazyColumn(
      state = state.listState,
      modifier = Modifier
        .reorderable(state)
        .detectReorderAfterLongPress(state)
    ) {
      items(repetitionBlock.stepsInRepetition, { it.id }) { trainingStep ->
        ReorderableItem(state, key = trainingStep.id) {
          when (trainingStep.type) {
            TrainingStep.Type.WARM_UP -> WarmUpCardContent(
              trainingStep,
              onDeleteClick = { _, trainingStep -> onDeleteClick(listOf(repetitionBlock.id), trainingStep) },
              onEditClick = { _, trainingStep -> onEditClick(listOf(repetitionBlock.id), trainingStep) }
            )
            TrainingStep.Type.COOL_DOWN -> CoolDownCardContent(
              trainingStep,
              onDeleteClick = { _, trainingStep -> onDeleteClick(listOf(repetitionBlock.id), trainingStep) },
              onEditClick = { _, trainingStep -> onEditClick(listOf(repetitionBlock.id), trainingStep) }
            )
            TrainingStep.Type.REPETITION -> RepetitionsCardContent(
              trainingStep,
              onDeleteClick = { _, trainingStep -> onDeleteClick(listOf(repetitionBlock.id), trainingStep) },
              onEditClick = { _, trainingStep -> onEditClick(listOf(repetitionBlock.id), trainingStep) }
            )
            TrainingStep.Type.REPETITION_BLOCK -> RepetitionBlockContent(
              trainingStep,
              onDeleteClick = { descendants, trainingStep -> onDeleteClick(listOf(repetitionBlock.id) + descendants, trainingStep) },
              onEditClick = { descendants, trainingStep -> onEditClick(listOf(repetitionBlock.id) + descendants, trainingStep) },
              onAddClick = { descendants -> onAddClick(listOf(repetitionBlock.id) + descendants) },
              onAddBlockClick = { descendants, repetitions -> onAddBlockClick(listOf(repetitionBlock.id) + descendants, repetitions) },
              onRepetitionsClick = { descendants, repetitions -> onRepetitionsClick(listOf(repetitionBlock.id) + descendants, repetitions) },
              onMove = { descendants, from, to -> onMove(listOf(repetitionBlock.id) + descendants, from, to) }
            )
          }
        }
      }
    }
    AddButtons(
      onAddClick = { _ -> onAddClick(listOf(repetitionBlock.id)) },
      onAddBlockClick = { _, repetitions -> onAddBlockClick(listOf(repetitionBlock.id), repetitions) }
    )
  }
}


@Composable
fun AddButtons(
  onAddClick: (List<String>) -> Unit,
  onAddBlockClick: (List<String>, Int) -> Unit
) {
  BoxWithConstraints {
    val availableWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() }
    val isEnoughSpaceForIcon = availableWidth > 320.dp
    val isEnoughSpaceForText = availableWidth > 260.dp

    Row (
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      if (isEnoughSpaceForText) {
        Button(
          onClick = { onAddClick(listOf()) }
        ) {
          Text(text = "Add repetition")
        }
      } else {
        FilledIconButton(
          onClick = { onAddClick(listOf()) }
        ) {
          Icon(
            Icons.Outlined.Add,
            contentDescription = stringResource(R.string.add_repetition)
          )
        }
      }
      FilledTonalIconButton(
        onClick = { onAddBlockClick(listOf(), 3) }
      ) {
        Text(text = "3x")
      }
      FilledTonalIconButton(
        onClick = { onAddBlockClick(listOf(), 5) }
      ) {
        Text(text = "5x")
      }
      if (isEnoughSpaceForIcon) {
        FilledTonalIconButton(
          onClick = { onAddBlockClick(listOf(), 10) }
        ) {
          Text(text = "10x")
        }
      }
    }
  }
}

@Composable
fun TimeSelectionDialog(
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  durationSelection: Int,
  hourPickerState: PickerState,
  minutePickerState: PickerState,
  secondPickerState: PickerState
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(text = "Duration time (h:m:s)")
    },
    text = {
      Surface(modifier = Modifier.height(160.dp)) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize()
        ) {

          Row(modifier = Modifier.fillMaxWidth()) {
            NumberPicker(
              state = hourPickerState,
              items = remember { (0..23).map { it.toString() } },
              modifier = Modifier.weight(1f),
              visibleItemsCount = 3,
              startIndex = durationSelection / 3600,
              textModifier = Modifier.padding(8.dp),
              textStyle = TextStyle(fontSize = 24.sp)
            )
            NumberPicker(
              state = minutePickerState,
              items = remember { (0..59).map { it.toString() } },
              visibleItemsCount = 3,
              modifier = Modifier.weight(1f),
              startIndex = durationSelection / 60 % 60,
              textModifier = Modifier.padding(8.dp),
              textStyle = TextStyle(fontSize = 24.sp)
            )
            NumberPicker(
              state = secondPickerState,
              items = remember { (0..59).map { it.toString() } },
              visibleItemsCount = 3,
              modifier = Modifier.weight(1f),
              startIndex = durationSelection % 60,
              textModifier = Modifier.padding(8.dp),
              textStyle = TextStyle(fontSize = 24.sp)
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(
        onClick = onConfirm
      ) {
        Text("Confirm")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismissRequest
      ) {
        Text("Dismiss")
      }
    }
  )
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
    },
    confirmButton = {
      TextButton(
        onClick = onConfirm
      ) {
        Text("Confirm")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismissRequest
      ) {
        Text("Dismiss")
      }
    }
  )
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
      TextButton(
        onClick = onConfirm
      ) {
        Text("Confirm")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismissRequest
      ) {
        Text("Dismiss")
      }
    }
  )
}

@Composable
fun EditStepDialog (
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  currentStep: MutableState<TrainingStep>
) {
  FullScreenDialog (
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

      CardSelector(
        label = R.string.duration_type,
        options = TrainingStep.DurationType.getOptions(),
        selection = durationTypeSelection,
        modifier = Modifier.card()
      ) { newValue -> currentStep.value = currentStep.value.copy(durationType = newValue) }

      if (currentStep.value.durationType == TrainingStep.DurationType.TIME) {
        val openTimeDialog = rememberSaveable { mutableStateOf(false) }

        Button(onClick = { openTimeDialog.value = true }) {
          Text(text = "Select time")
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

        Button(onClick = { openDistanceDialog.value = true }) {
          Text(text = "Select distance")
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
        CardSelector(
          label = R.string.recover_type,
          options = TrainingStep.DurationType.getOptions(),
          selection = recoverTypeSelection,
          modifier = Modifier.card()
        ) { newValue -> currentStep.value = currentStep.value.copy(recoverType = newValue) }

        if (currentStep.value.recoverType == TrainingStep.DurationType.TIME) {
          val openTimeDialog = rememberSaveable { mutableStateOf(false) }

          Button(onClick = { openTimeDialog.value = true }) {
            Text(text = "Select recover time")
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
        } else {
          val openDistanceDialog = rememberSaveable { mutableStateOf(false) }

          Button(onClick = { openDistanceDialog.value = true }) {
            Text(text = "Select distance")
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


