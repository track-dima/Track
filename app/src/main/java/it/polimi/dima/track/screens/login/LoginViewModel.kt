package it.polimi.dima.track.screens.login

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.LOGIN_SCREEN
import it.polimi.dima.track.R
import it.polimi.dima.track.SETTINGS_SCREEN
import it.polimi.dima.track.TRAININGS_SCREEN
import it.polimi.dima.track.common.ext.isValidEmail
import it.polimi.dima.track.common.snackbar.SnackBarManager
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.screens.TrackViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val accountService: AccountService,
  logService: LogService
) : TrackViewModel(logService) {
  var uiState = mutableStateOf(LoginUiState())
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

  fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
    if (!email.isValidEmail()) {
      SnackBarManager.showMessage(R.string.email_error)
      return
    }

    if (password.isBlank()) {
      SnackBarManager.showMessage(R.string.empty_password_error)
      return
    }

    launchCatching {
      accountService.authenticate(email, password)
      openAndPopUp(TRAININGS_SCREEN, LOGIN_SCREEN)
    }
  }

  fun onForgotPasswordClick() {
    if (!email.isValidEmail()) {
      SnackBarManager.showMessage(R.string.email_error)
      return
    }

    launchCatching {
      accountService.sendRecoveryEmail(email)
      SnackBarManager.showMessage(R.string.recovery_email_sent)
    }
  }
}
