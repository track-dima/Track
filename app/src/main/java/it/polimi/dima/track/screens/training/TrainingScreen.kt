package it.polimi.dima.track.screens.training

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.NoTitleToolbar
import it.polimi.dima.track.model.Training

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
  popUpScreen: () -> Unit,
  trainingId: String,
  onEditPressed: (Training) -> Unit,
  viewModel: TrainingViewModel = hiltViewModel()
) {
  val training by viewModel.training

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }

  Column(
    modifier = Modifier.fillMaxWidth(),
  ) {
    NoTitleToolbar(
      navigationIcon = {
        FilledTonalIconButton(
          modifier = Modifier.padding(8.dp, 0.dp),
          onClick = popUpScreen
        ) {
          Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.close))
        }
      }
    ) {
      FilledTonalIconButton(
        modifier = Modifier.padding(4.dp, 0.dp),
        onClick = { onEditPressed(training) }
      ) {
        Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.edit_training))
      }
      FilledTonalIconButton(
        modifier = Modifier.padding(4.dp, 0.dp, 8.dp, 0.dp),
        onClick = { /* doSomething() */ }
      ) {
        Icon(Icons.Outlined.MoreVert, contentDescription = stringResource(R.string.settings))
      }
    }

    Text(text = training.title, style = MaterialTheme.typography.titleLarge)
  }
}