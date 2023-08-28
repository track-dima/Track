package it.polimi.dima.track.screens.fitbit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.TRAININGS_SCREEN
import it.polimi.dima.track.common.ext.bigSpacer

@Composable
fun FitbitScreen(
  fitbitAuthorizationCode: String,
  openScreen: (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: FitbitViewModel = hiltViewModel()
) {
  val token by viewModel.token

  LaunchedEffect(Unit) {
    viewModel.initialize(fitbitAuthorizationCode)
  }

  Column(
    modifier = modifier.fillMaxSize()
  ) {
    Spacer(modifier = Modifier.bigSpacer())

    if (token != null) {
      AlertDialog(
        title = { Text("Fitbit account connected!") },
        text = { Text(token!!.accessToken) },
        confirmButton = {
          Button(
            onClick = { openScreen(TRAININGS_SCREEN) }
          ) {
            Text("Ok")
          }
        },
        onDismissRequest = { }
      )
    }
  }
}
