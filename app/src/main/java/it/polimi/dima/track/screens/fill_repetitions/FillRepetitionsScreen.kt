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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.ReadOnlyStepsList
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.common.ext.toolbarActions

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

    ReadOnlyStepsList(trainingSteps = trainingSteps)
  }
}


