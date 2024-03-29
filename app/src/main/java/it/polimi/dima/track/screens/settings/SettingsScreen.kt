package it.polimi.dima.track.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.BasicToolbar
import it.polimi.dima.track.common.composable.DangerousCardEditor
import it.polimi.dima.track.common.composable.DialogCancelButton
import it.polimi.dima.track.common.composable.DialogConfirmButton
import it.polimi.dima.track.common.composable.RegularCardEditor
import it.polimi.dima.track.common.ext.bigSpacer
import it.polimi.dima.track.common.ext.card

@Composable
fun SettingsScreen(
  restartApp: (String) -> Unit,
  openScreen: (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState(initial = SettingsUiState())
  val context = LocalContext.current

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    BasicToolbar(R.string.settings)

    Spacer(modifier = Modifier.bigSpacer())

    if (uiState.isAnonymousAccount) {
      RegularCardEditor(R.string.sign_in, Icons.Rounded.Person, "", Modifier.card()) {
        viewModel.onLoginClick(openScreen)
      }

      RegularCardEditor(
        R.string.create_account,
        Icons.Rounded.PersonAdd,
        "",
        Modifier.card()
      ) {
        viewModel.onSignUpClick(openScreen)
      }
    } else {
      SignOutCard { viewModel.onSignOutClick() }
      DeleteMyAccountCard { viewModel.onDeleteMyAccountClick(restartApp) }
      Divider(Modifier.padding(16.dp))
      if (!uiState.isFitbitConnected) {
        RegularCardEditor(
          R.string.connect_to_your_fitbit_account,
          Icons.Rounded.Watch,
          "",
          Modifier.card(),
        ) {
          viewModel.onFitbitButtonClick(context)
        }
      } else {
        Text(stringResource(R.string.fitbit_account_connected))
      }
    }
  }
}

@Composable
private fun SignOutCard(signOut: () -> Unit) {
  var showWarningDialog by remember { mutableStateOf(false) }

  RegularCardEditor(R.string.sign_out, Icons.Rounded.ExitToApp, "", Modifier.card()) {
    showWarningDialog = true
  }

  if (showWarningDialog) {
    AlertDialog(
      title = { Text(stringResource(R.string.sign_out_title)) },
      text = { Text(stringResource(R.string.sign_out_description)) },
      dismissButton = { DialogCancelButton(R.string.cancel) { showWarningDialog = false } },
      confirmButton = {
        DialogConfirmButton(R.string.sign_out) {
          signOut()
          showWarningDialog = false
        }
      },
      onDismissRequest = { showWarningDialog = false }
    )
  }
}

@Composable
private fun DeleteMyAccountCard(deleteMyAccount: () -> Unit) {
  var showWarningDialog by remember { mutableStateOf(false) }

  DangerousCardEditor(
    R.string.delete_my_account,
    Icons.Rounded.Delete,
    "",
    Modifier.card()
  ) {
    showWarningDialog = true
  }

  if (showWarningDialog) {
    AlertDialog(
      title = { Text(stringResource(R.string.delete_account_title)) },
      text = { Text(stringResource(R.string.delete_account_description)) },
      dismissButton = { DialogCancelButton(R.string.cancel) { showWarningDialog = false } },
      confirmButton = {
        DialogConfirmButton(R.string.delete_my_account) {
          deleteMyAccount()
          showWarningDialog = false
        }
      },
      onDismissRequest = { showWarningDialog = false }
    )
  }
}
