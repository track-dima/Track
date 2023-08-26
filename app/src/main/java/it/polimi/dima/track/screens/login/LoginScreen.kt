package it.polimi.dima.track.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.*
import it.polimi.dima.track.common.ext.basicButton
import it.polimi.dima.track.common.ext.fieldModifier
import it.polimi.dima.track.common.ext.textButton

@Composable
fun LoginScreen(
  openAndPopUp: (String, String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LoginViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState

  BasicToolbar(R.string.login_details)

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    EmailField(uiState.email, viewModel::onEmailChange, Modifier.fieldModifier())
    PasswordField(uiState.password, viewModel::onPasswordChange, Modifier.fieldModifier())

    BasicButton(R.string.sign_in, Modifier.basicButton()) {
      // TODO close keyboard
      viewModel.onSignInClick(openAndPopUp)
    }

    BasicTextButton(R.string.forgot_password, Modifier.textButton()) {
      viewModel.onForgotPasswordClick()
    }
  }
}
