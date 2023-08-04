package it.polimi.dima.track.screens.edit_repetitions

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRepetitionsScreen(
  popUpScreen: () -> Unit,
  trainingId: String,
  modifier: Modifier = Modifier,
  viewModel: EditRepetitionsViewModel = hiltViewModel()
) {
  val trainingSteps by viewModel.trainingSteps
  val data = remember { mutableStateOf<List<TrainingStep>>(emptyList()) }

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }

  if (trainingSteps.isNotEmpty()) {
    data.value = List(trainingSteps.size) { trainingSteps[it] }
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

    val state = rememberReorderableLazyListState(onMove = { from, to ->
      viewModel.moveStep(from, to)
    })
    LazyColumn(
      state = state.listState,
      modifier = Modifier
        .reorderable(state)
        .detectReorderAfterLongPress(state)
    ) {
      items(trainingSteps, { it.id }) { trainingStep ->
        ReorderableItem(state, key = trainingStep.id) { isDragging ->
          val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "card elevation")
          OutlinedCard(
            modifier = Modifier
              .fieldModifier()
              .fillMaxWidth()
              .height(70.dp),
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
              ){
                Row {
                  Text(text = "Distance: ", fontWeight = FontWeight.Bold)
                  Text(text = trainingStep.distance.toString() + 'm')
                }
                Row {
                  Text(text = "Recover time: ", fontWeight = FontWeight.Bold)
                  Text(text = trainingStep.recoverDuration.toString() + 's')
                }
              }
              FilledTonalIconButton(
                modifier = Modifier.padding(8.dp),
                onClick = { viewModel.onDeleteClick(trainingStep) }
              ) {
                Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.delete_repetition))
              }
            }
          }
        }
      }
    }

    OutlinedCard(
      modifier = Modifier
        .fieldModifier()
        .fillMaxWidth()
        .height(70.dp),
      onClick = { viewModel.onAddClick() }
    ) {
      Column(
        modifier = modifier
          .fillMaxWidth()
          .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(text = "+", style = MaterialTheme.typography.titleLarge)
      }
    }
  }
}