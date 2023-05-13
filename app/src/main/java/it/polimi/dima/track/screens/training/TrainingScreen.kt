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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R

@Composable
fun TrainingScreen(
  popUpScreen: () -> Unit,
  trainingId: String,
  onEditPressed: () -> Unit,
  viewModel: TrainingViewModel = hiltViewModel()
) {
  val training by viewModel.training

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }

  Column(
  ) {
    Row(
      modifier = Modifier.padding(12.dp, 8.dp).fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Box() {
        FilledTonalIconButton(
          onClick = popUpScreen
        ) {
          Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.close))
        }
      }
      Row() {
        FilledTonalIconButton(
          modifier = Modifier.padding(4.dp, 0.dp),
          onClick = onEditPressed
        ) {
          Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.edit_training))
        }
        FilledTonalIconButton(
          modifier = Modifier.padding(4.dp, 0.dp),
          onClick = { /* doSomething() */ }
        ) {
          Icon(Icons.Outlined.MoreVert, contentDescription = stringResource(R.string.settings))
        }
      }
    }
    Text(text = training.title, style = MaterialTheme.typography.titleLarge)
  }
}