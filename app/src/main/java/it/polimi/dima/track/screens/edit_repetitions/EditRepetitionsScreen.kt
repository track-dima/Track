package it.polimi.dima.track.screens.edit_repetitions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.polimi.dima.track.EDIT_REPETITIONS_SCREEN
import it.polimi.dima.track.R
import it.polimi.dima.track.TRAINING_ID
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.ext.fieldModifier
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.screens.training.TrainingCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRepetitionsScreen(
  popUpScreen: () -> Unit,
  trainingId: String,
  modifier: Modifier = Modifier,
  viewModel: EditRepetitionsViewModel = hiltViewModel()
) {
  val training by viewModel.training

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // val steps = training.trainingSteps.toMutableStateList()

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

    LazyColumn {
      items(training.trainingSteps) { trainingStep ->
        OutlinedCard(
          modifier = Modifier
            .fieldModifier()
            .fillMaxWidth()
            .height(60.dp),
          onClick = { viewModel.onEditClick(trainingStep) }
        ) {
          FilledTonalIconButton(
            modifier = Modifier.align(Alignment.End).padding(8.dp),
            onClick = { viewModel.onDeleteClick(trainingStep) }
          ) {
            Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.delete_repetition))
          }
        }
      }
    }

    OutlinedCard(
      modifier = Modifier
        .fieldModifier()
        .fillMaxWidth()
        .height(60.dp),
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