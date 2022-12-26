package it.polimi.dima.track

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import it.polimi.dima.track.common.snackbar.SnackBarManager
import it.polimi.dima.track.common.snackbar.SnackBarMessage.Companion.toMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Stable
class TrackAppState(
  val snackbarHostState: SnackbarHostState,
  val navController: NavHostController,
  private val snackBarManager: SnackBarManager,
  private val resources: Resources,
  coroutineScope: CoroutineScope
) {
  init {
    coroutineScope.launch {
      snackBarManager.snackBarMessages.filterNotNull().collect { snackBarMessage ->
        val text = snackBarMessage.toMessage(resources)
        snackbarHostState.showSnackbar(text)
      }
    }
  }

  fun popUp() {
    navController.popBackStack()
  }

  fun navigate(route: String) {
    navController.navigate(route) { launchSingleTop = true }
  }

  fun navigateAndPopUp(route: String, popUp: String) {
    navController.navigate(route) {
      launchSingleTop = true
      popUpTo(popUp) { inclusive = true }
    }
  }

  fun clearAndNavigate(route: String) {
    navController.navigate(route) {
      launchSingleTop = true
      popUpTo(0) { inclusive = true }
    }
  }
}
