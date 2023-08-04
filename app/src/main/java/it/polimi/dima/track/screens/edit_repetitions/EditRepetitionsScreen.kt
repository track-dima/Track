package it.polimi.dima.track.screens.edit_repetitions

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.ext.fieldModifier
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
  val trainingSteps by viewModel.trainingSteps
  val draggingIds: MutableMap<String, Boolean> = mutableMapOf()

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }

  Scaffold(
    floatingActionButton = {
      Column(
        horizontalAlignment = Alignment.End,
      ) {
        SmallFloatingActionButton(
          modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp),
          containerColor = MaterialTheme.colorScheme.primary,
          onClick = { /*TODO*/ }
        ) {
          Text(text = "10x")
        }
        SmallFloatingActionButton(
          modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp),
          containerColor = MaterialTheme.colorScheme.primary,
          onClick = { /*TODO*/ }
        ) {
          Text(text = "5x")
        }
        SmallFloatingActionButton(
          modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 12.dp),
          containerColor = MaterialTheme.colorScheme.primary,
          onClick = { viewModel.onAddRepetitionClick(3) }
        ) {
          Text(text = "3x")
        }
        ExtendedFloatingActionButton(
          onClick = { viewModel.onAddClick() },
          icon = { Icon(Icons.Filled.Add, stringResource(R.string.add_repetition)) },
          text = { Text(text = stringResource(R.string.add_repetition)) },
        )
      }
    }
  ) {
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
        onMove = { from, to -> viewModel.moveStep(from, to) },
        canDragOver = { from, to -> !(from.index == 0 && to.index == 1) }
      )
      LazyColumn(
        state = state.listState,
        modifier = Modifier
          .reorderable(state)
          .detectReorderAfterLongPress(state)
          .weight(1f)
      ) {
        items(trainingSteps, { it.id }) { trainingStep ->
          ReorderableItem(state, key = trainingStep.id) { isDragging ->
            if (trainingStep.type == TrainingStep.Type.START_REP_BLOCK) {
              draggingIds[trainingStep.id] = isDragging
            }
            if (draggingIds[trainingStep.repetitionBlock] != true) {
              when (trainingStep.type) {
                TrainingStep.Type.WARM_UP -> WarmUpCardContent(trainingStep, viewModel)
                TrainingStep.Type.COOL_DOWN -> CoolDownCardContent(trainingStep, viewModel)
                TrainingStep.Type.REPETITIONS -> RepetitionsCardContent(trainingStep, viewModel)
                TrainingStep.Type.START_REP_BLOCK -> RepetitionBlockStartContent(trainingStep, viewModel)
                TrainingStep.Type.END_REP_BLOCK -> RepetitionBlockEndContent(trainingStep, viewModel)
              }
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
  viewModel: EditRepetitionsViewModel
) {
  OutlinedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(70.dp),
    onClick = { viewModel.onEditClick(trainingStep) }
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
      FilledTonalIconButton(
        modifier = Modifier.padding(8.dp),
        onClick = { viewModel.onDeleteClick(trainingStep) }
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
    viewModel: EditRepetitionsViewModel
    ) {
  OutlinedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(70.dp),
    onClick = { viewModel.onEditClick(trainingStep) }
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
      FilledTonalIconButton(
        modifier = Modifier.padding(8.dp),
        onClick = { viewModel.onDeleteClick(trainingStep) }
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
  viewModel: EditRepetitionsViewModel
) {
  OutlinedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(70.dp),
    onClick = { viewModel.onEditClick(trainingStep) }
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
      FilledTonalIconButton(
        modifier = Modifier.padding(8.dp),
        onClick = { viewModel.onDeleteClick(trainingStep) }
      ) {
        Icon(
          Icons.Outlined.Delete,
          contentDescription = stringResource(R.string.delete_repetition)
        )
      }
    }
  }
}

@Composable
fun RepetitionBlockStartContent(
    trainingStep: TrainingStep,
    viewModel: EditRepetitionsViewModel
) {
  OutlinedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(60.dp),
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
        Text(text = trainingStep.repetitions.toString() + "x", fontWeight = FontWeight.Bold)
      }
      FilledTonalIconButton(
        modifier = Modifier.padding(8.dp),
        onClick = { viewModel.onDeleteClick(trainingStep) }
      ) {
        Icon(
          Icons.Outlined.Delete,
          contentDescription = stringResource(R.string.delete_repetition)
        )
      }
    }
  }
}

@Composable
fun RepetitionBlockEndContent(
  trainingStep: TrainingStep,
  viewModel: EditRepetitionsViewModel
) {
  OutlinedCard(
    modifier = Modifier
      .fieldModifier()
      .fillMaxWidth()
      .height(10.dp),
  ) {}
}