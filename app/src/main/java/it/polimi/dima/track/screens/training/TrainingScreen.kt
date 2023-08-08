package it.polimi.dima.track.screens.training

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
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
import it.polimi.dima.track.common.composable.DropdownContextMenu
import it.polimi.dima.track.common.composable.IconButtonStyle
import it.polimi.dima.track.common.composable.NoTitleToolbar
import it.polimi.dima.track.common.composable.TrainingStepsListBox
import it.polimi.dima.track.common.ext.contextMenu
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.model.Training

@Composable
fun TrainingScreen(
  popUpScreen: () -> Unit,
  openScreen: (String) -> Unit,
  trainingId: String,
  onEditPressed: (Training) -> Unit,
  viewModel: TrainingViewModel = hiltViewModel()
) {
  val training by viewModel.training
  val options by viewModel.options

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
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
      FilledTonalIconToggleButton(
        modifier = Modifier.padding(4.dp, 0.dp),
        checked = training.favorite,
        onCheckedChange = { viewModel.onFavoriteClick() }) {
          Icon(
            if (training.favorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = stringResource(R.string.favorite)
          )
      }
      FilledTonalIconButton(
        modifier = Modifier.padding(4.dp, 0.dp),
        onClick = { onEditPressed(training) }
      ) {
        Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.edit_training))
      }
      DropdownContextMenu(
        options = options,
        modifier = Modifier
          .contextMenu()
          .padding(4.dp, 0.dp, 8.dp, 0.dp),
        onActionClick = { action -> viewModel.onTrainingActionClick(onEditPressed, popUpScreen, training, action) },
        style = IconButtonStyle.FilledTonal,
      )
    }

    Text(text = training.title, style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.spacer())

    TrainingStepsListBox(training, openScreen)
  }

  LaunchedEffect(viewModel) { viewModel.loadTaskOptions() }
}