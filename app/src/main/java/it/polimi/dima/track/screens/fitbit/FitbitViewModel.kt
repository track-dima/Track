package it.polimi.dima.track.screens.fitbit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.fitbit.FitbitAuthManager
import it.polimi.dima.track.model.service.fitbit.FitbitOAuthToken
import it.polimi.dima.track.screens.TrackViewModel
import javax.inject.Inject

@HiltViewModel
class FitbitViewModel @Inject constructor(
  private val fitbitAuthManager: FitbitAuthManager,
  logService: LogService,
) : TrackViewModel(logService) {
  val token: MutableState<FitbitOAuthToken?> = mutableStateOf(null)

  fun initialize(authenticationCode: String) {
    launchCatching {
      token.value = fitbitAuthManager.exchangeCodeForToken(authenticationCode)
    }
  }
}