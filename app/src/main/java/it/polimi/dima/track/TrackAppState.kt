package it.polimi.dima.track

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import it.polimi.dima.track.common.snackbar.SnackBarManager
import it.polimi.dima.track.common.snackbar.SnackBarMessage.Companion.toMessage
import it.polimi.dima.track.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Stable
class TrackAppState constructor(
  val snackbarHostState: SnackbarHostState,
  val navController: NavHostController,
  private val snackBarManager: SnackBarManager,
  private val resources: Resources,
  coroutineScope: CoroutineScope,
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

  fun drawerNavigate(destination: TopLevelDestination) {
    navController.navigate(destination.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      // on the back stack as users select items
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
      }
      // Avoid multiple copies of the same destination when
      // reselecting the same item
      launchSingleTop = true
      // Restore state when reselecting a previously selected item
      restoreState = true
    }
  }
}
