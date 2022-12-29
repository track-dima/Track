package it.polimi.dima.track.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.BasicButton
import it.polimi.dima.track.common.ext.basicButton

@Composable
fun SplashScreen(
  openAndPopUp: (String, String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SplashViewModel = hiltViewModel()
) {
  Column(
    modifier =
      modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(color = MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    if (viewModel.showError.value) {
      Text(text = stringResource(R.string.generic_error))

      BasicButton(R.string.try_again, Modifier.basicButton()) { viewModel.onAppStart(openAndPopUp) }
    } else {
      CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
    }
  }

  LaunchedEffect(true) {
    viewModel.onAppStart(openAndPopUp)
  }
}
