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
import it.polimi.dima.track.common.composable.NumberPicker
import it.polimi.dima.track.common.composable.rememberPickerState
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
      AlertDialog(
        onDismissRequest = {
          openRepetitionsDialog.value = false
        },
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
                startIndex = currentRepetitions.value - 2,
                textModifier = Modifier.padding(8.dp),
                textStyle = TextStyle(fontSize = 24.sp)
              )
            }
          }
        },
        confirmButton = {
          TextButton(
            onClick = {
              openRepetitionsDialog.value = false
              viewModel.onEditRepetitionsClick(
                currentHierarchy.value,
                repetitionsPickerState.selectedItem.toInt()
              )
            }
          ) {
            Text("Confirm")
          }
        },
        dismissButton = {
          TextButton(
            onClick = {
              openRepetitionsDialog.value = false
            }
          ) {
            Text("Dismiss")
          }
        }
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
                onEditClick = { _, trainingStep -> viewModel.onEditClick(listOf(), trainingStep) }
              )
              TrainingStep.Type.COOL_DOWN -> CoolDownCardContent(
                trainingStep,
                onDeleteClick = { _, trainingStep -> viewModel.onDeleteClick(listOf(), trainingStep) },
                onEditClick = { _, trainingStep -> viewModel.onEditClick(listOf(), trainingStep) }
              )
              TrainingStep.Type.REPETITIONS -> RepetitionsCardContent(
                trainingStep,
                onDeleteClick = { _, trainingStep -> viewModel.onDeleteClick(listOf(), trainingStep) },
                onEditClick = { _, trainingStep -> viewModel.onEditClick(listOf(), trainingStep) }
              )
              TrainingStep.Type.REPETITION_BLOCK -> RepetitionBlockContent(
                trainingStep,
                onDeleteClick = { hierarchy, trainingStep -> viewModel.onDeleteClick(hierarchy, trainingStep) },
                onEditClick = { hierarchy, trainingStep -> viewModel.onEditClick(hierarchy, trainingStep) },
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
            Text(text = trainingStep.distance.toString() + 'm')
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
            Text(text = trainingStep.distance.toString() + 'm')
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
            Text(text = trainingStep.distance.toString() + 'm')
          }
        }
        Row {
          if (trainingStep.recoverType == TrainingStep.DurationType.TIME) {
            Text(text = "Recover time: ", fontWeight = FontWeight.Bold)
            Text(text = trainingStep.recoverDuration.toString() + 's')
          }
          else {
            Text(text = "Recover distance: ", fontWeight = FontWeight.Bold)
            Text(text = trainingStep.recoverDistance.toString() + 'm')
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
            TrainingStep.Type.REPETITIONS -> RepetitionsCardContent(
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