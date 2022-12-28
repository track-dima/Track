package it.polimi.dima.track.screens.settings

import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.LOGIN_SCREEN
import it.polimi.dima.track.SIGN_UP_SCREEN
import it.polimi.dima.track.SPLASH_SCREEN
import it.polimi.dima.track.TRAININGS_SCREEN
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.screens.TrackViewModel
import it.polimi.dima.track.screens.settings.SettingsUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@HiltViewModel
class SettingsViewModel @Inject constructor(
  logService: LogService,
  private val accountService: AccountService,
) : TrackViewModel(logService) {
  val uiState = accountService.currentUser.map { SettingsUiState(it.isAnonymous) }

  fun onLoginClick(openScreen: (String) -> Unit) = openScreen(LOGIN_SCREEN)

  fun onSignUpClick(openScreen: (String) -> Unit) = openScreen(SIGN_UP_SCREEN)

  fun onSignOutClick() {
    launchCatching {
      accountService.signOut()
    }
  }

  fun onDeleteMyAccountClick(restartApp: (String) -> Unit) {
    launchCatching {
      accountService.deleteAccount()
      restartApp(SPLASH_SCREEN)
    }
  }
}
