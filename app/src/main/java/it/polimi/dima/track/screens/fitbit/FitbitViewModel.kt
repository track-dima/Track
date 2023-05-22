package it.polimi.dima.track.screens.fitbit

import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.services.fitbit.FitbitAuthManager
import javax.inject.Inject

@HiltViewModel
class FitbitViewModel @Inject constructor(
    private val fitbitAuthManager: FitbitAuthManager
) : ViewModel() {
    fun handleAppLink(appLinkIntent: Intent) {
        if (!appLinkIntent.action.equals(Intent.ACTION_VIEW)) return
        val appLinkData = appLinkIntent.data ?: return
        if (appLinkData.path.equals("/fitbit")) {
            val code = appLinkData.getQueryParameter("code") ?: return
            val token = fitbitAuthManager.exchangeCodeForToken(code)
            throw Exception(token.accessToken)
        }
    }
}