package it.polimi.dima.track.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.*
import it.polimi.dima.track.common.ext.card
import it.polimi.dima.track.common.ext.spacer

@Composable
fun SettingsScreen(
  restartApp: (String) -> Unit,
  openScreen: (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState(initial = SettingsUiState(false))

  Column(
    modifier = modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    BasicToolbar(R.string.settings)

    Spacer(modifier = Modifier.spacer())

    if (uiState.isAnonymousAccount) {
      RegularCardEditor(R.string.sign_in, R.drawable.ic_sign_in, "", Modifier.card()) {
        viewModel.onLoginClick(openScreen)
      }

      RegularCardEditor(R.string.create_account, R.drawable.ic_create_account, "", Modifier.card()) {
        viewModel.onSignUpClick(openScreen)
      }
    } else {
      SignOutCard { viewModel.onSignOutClick(restartApp) }
      DeleteMyAccountCard { viewModel.onDeleteMyAccountClick(restartApp) }
    }
  }
}

@Composable
private fun SignOutCard(signOut: () -> Unit) {
  var showWarningDialog by remember { mutableStateOf(false) }

  RegularCardEditor(R.string.sign_out, R.drawable.ic_exit, "", Modifier.card()) {
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
    R.drawable.ic_delete_my_account,
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
