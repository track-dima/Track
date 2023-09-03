package it.polimi.dima.track.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.LOGIN_SCREEN
import it.polimi.dima.track.SIGN_UP_SCREEN
import it.polimi.dima.track.SPLASH_SCREEN
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.screens.TrackViewModel
import it.polimi.dima.track.model.service.fitbit.FitbitAuthManager
import it.polimi.dima.track.model.service.storage.UserStorageService
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@HiltViewModel
class SettingsViewModel @Inject constructor(
  logService: LogService,
  userStorageService: UserStorageService,
  private val accountService: AccountService,
  private val fitbitAuthManager: FitbitAuthManager,
) : TrackViewModel(logService) {
  val uiState = userStorageService.user.map {
    SettingsUiState(
      it.isAnonymous,
      it.fitbitToken != null
    )
  }

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

  fun onFitbitButtonClick(context: Context) {
    val intent = CustomTabsIntent.Builder().build()
    intent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.launchUrl(context, Uri.parse(fitbitAuthManager.createAuthorizationUrl()))
  }
}
