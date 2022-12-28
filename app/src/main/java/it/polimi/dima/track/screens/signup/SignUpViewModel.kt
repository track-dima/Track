package it.polimi.dima.track.screens.signup

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.R
import it.polimi.dima.track.SETTINGS_SCREEN
import it.polimi.dima.track.SIGN_UP_SCREEN
import it.polimi.dima.track.TRAININGS_SCREEN
import it.polimi.dima.track.common.ext.isValidEmail
import it.polimi.dima.track.common.ext.isValidPassword
import it.polimi.dima.track.common.ext.passwordMatches
import it.polimi.dima.track.common.snackbar.SnackBarManager
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.screens.TrackViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
  private val accountService: AccountService,
  logService: LogService
) : TrackViewModel(logService) {
  var uiState = mutableStateOf(SignUpUiState())
    private set

  private val email
    get() = uiState.value.email
  private val password
    get() = uiState.value.password

  fun onEmailChange(newValue: String) {
    uiState.value = uiState.value.copy(email = newValue)
  }

  fun onPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(password = newValue)
  }

  fun onRepeatPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(repeatPassword = newValue)
  }

  fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
    if (!email.isValidEmail()) {
      SnackBarManager.showMessage(R.string.email_error)
      return
    }

    if (!password.isValidPassword()) {
      SnackBarManager.showMessage(R.string.password_error)
      return
    }

    if (!password.passwordMatches(uiState.value.repeatPassword)) {
      SnackBarManager.showMessage(R.string.password_match_error)
      return
    }

    launchCatching {
      accountService.linkAccount(email, password)
      openAndPopUp(TRAININGS_SCREEN, SIGN_UP_SCREEN)
    }
  }
}
