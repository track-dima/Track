package it.polimi.dima.track.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.*
import it.polimi.dima.track.common.ext.basicButton
import it.polimi.dima.track.common.ext.fieldModifier
import it.polimi.dima.track.screens.signup.SignUpViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(
  openAndPopUp: (String, String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: SignUpViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState
  val fieldModifier = Modifier.fieldModifier()
  Column(
    modifier = Modifier.fillMaxSize(),
  ) {
    BasicToolbar(R.string.create_account)

    Column(
      modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val keyboardController = LocalSoftwareKeyboardController.current

      EmailField(uiState.email, viewModel::onEmailChange, fieldModifier)
      PasswordField(uiState.password, viewModel::onPasswordChange, fieldModifier)
      RepeatPasswordField(uiState.repeatPassword, viewModel::onRepeatPasswordChange, fieldModifier)

      BasicButton(R.string.create_account, Modifier.basicButton()) {
        keyboardController?.hide()
        viewModel.onSignUpClick(openAndPopUp)
      }
    }
  }
}
