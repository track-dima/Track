package it.polimi.dima.track.screens.training

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TrainingScreen(
  popUpScreen: () -> Unit,
  trainingId: String,
  modifier: Modifier = Modifier,
  viewModel: TrainingViewModel = hiltViewModel()
) {
  val training by viewModel.training

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }
}