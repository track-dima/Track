package it.polimi.dima.track

import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import it.polimi.dima.track.common.snackbar.SnackBarManager
import it.polimi.dima.track.common.utils.NavigationContentPosition
import it.polimi.dima.track.common.utils.NavigationType
import it.polimi.dima.track.common.utils.TrackContentType
import it.polimi.dima.track.navigation.PermanentNavigationDrawerContent
import it.polimi.dima.track.navigation.TopLevelDestination
import it.polimi.dima.track.navigation.TrackBottomNavigationBar
import it.polimi.dima.track.navigation.TrackNavigationRail
import it.polimi.dima.track.screens.agenda.AgendaScreen
import it.polimi.dima.track.screens.edit_repetitions.EditRepetitionsScreen
import it.polimi.dima.track.screens.edit_training.EditTrainingScreen
import it.polimi.dima.track.screens.edit_training.EditTrainingViewModel
import it.polimi.dima.track.screens.fill_repetitions.FillRepetitionsScreen
import it.polimi.dima.track.screens.fitbit.FitbitScreen
import it.polimi.dima.track.screens.login.LoginScreen
import it.polimi.dima.track.screens.profile.ProfileScreen
import it.polimi.dima.track.screens.search.SearchScreen
import it.polimi.dima.track.screens.settings.SettingsScreen
import it.polimi.dima.track.screens.signup.SignUpScreen
import it.polimi.dima.track.screens.splash.SplashScreen
import it.polimi.dima.track.screens.training.TrainingScreen
import it.polimi.dima.track.screens.trainings.TrainingsScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun TrackApp(
  windowSize: WindowSizeClass,
  width: Dp,
  viewModel: TrackAppViewModel = hiltViewModel()
) {
  viewModel.calculateNavigationType(windowSize, width)
  val uiState by viewModel.uiState
  val appState = rememberAppState()
  val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
  val selectedDestination = navBackStackEntry?.destination?.route ?: AGENDA_SCREEN
  val orientation = LocalConfiguration.current.orientation

  Surface(color = MaterialTheme.colorScheme.background) {
    Scaffold(
      contentWindowInsets = if (orientation != Configuration.ORIENTATION_LANDSCAPE) WindowInsets(0.dp) else WindowInsets.navigationBars,
      snackbarHost = {
        SnackbarHost(
          hostState = appState.snackbarHostState,
          modifier = Modifier.padding(8.dp),
          snackbar = { snackBarData ->
            Snackbar(
              snackBarData,
              contentColor = MaterialTheme.colorScheme.onPrimary
            )
          }
        )
      }
    ) { innerPaddingModifier ->
      PermanentNavigationDrawer(
        drawerContent = {
          if (uiState.navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            val colorScheme = MaterialTheme.colorScheme
            val navigationDrawerContentDescription = stringResource(R.string.navigation_drawer)
            SideEffect {
              systemUiController.setSystemBarsColor(
                colorScheme.background,
                darkIcons = useDarkIcons
              )
            }
            PermanentNavigationDrawerContent(
              modifier = Modifier.testTag(navigationDrawerContentDescription),
              selectedDestination = selectedDestination,
              navigationContentPosition = uiState.navigationContentPosition,
              navigateToTopLevelDestination = appState::drawerNavigate,
              openScreen = { route -> appState.navigate(route) }
            )
          }
        }) {
        TrackAppContent(
          appState = appState,
          navigationType = uiState.navigationType,
          contentType = uiState.contentType,
          navigationContentPosition = uiState.navigationContentPosition,
          innerPadding = innerPaddingModifier,
          selectedDestination = selectedDestination,
          navigateToTopLevelDestination = appState::drawerNavigate
        )
      }
    }
  }
}


@Composable
fun TrackAppContent(
  appState: TrackAppState,
  navigationType: NavigationType,
  contentType: TrackContentType,
  selectedDestination: String,
  innerPadding: PaddingValues,
  navigationContentPosition: NavigationContentPosition,
  navigateToTopLevelDestination: (TopLevelDestination) -> Unit,
) {
  Row(modifier = Modifier.fillMaxSize()) {
    AnimatedVisibility(visible = navigationType == NavigationType.NAVIGATION_RAIL) {
      val navigationRailContentDescription = stringResource(R.string.navigation_rail)
      TrackNavigationRail(
        modifier = Modifier.testTag(navigationRailContentDescription),
        selectedDestination = selectedDestination,
        navigationContentPosition = navigationContentPosition,
        navigateToTopLevelDestination = navigateToTopLevelDestination,
        openScreen = { route -> appState.navigate(route) },
      )
    }
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.inverseOnSurface)
        .padding(innerPadding)
    ) {
      NavHost(
        navController = appState.navController,
        startDestination = SPLASH_SCREEN,
        modifier = Modifier.weight(1f)
      ) {
        trackGraph(appState, navigationType, contentType)
      }
      AnimatedVisibility(visible = navigationType == NavigationType.BOTTOM_NAVIGATION) {
        val bottomNavigationContentDescription = stringResource(R.string.navigation_bottom)
        TrackBottomNavigationBar(
          modifier = Modifier.testTag(bottomNavigationContentDescription),
          selectedDestination = selectedDestination,
          navigateToTopLevelDestination = navigateToTopLevelDestination
        )
      }
    }
  }
}

@Composable
fun rememberAppState(
  snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
  navController: NavHostController = rememberNavController(),
  snackbarManager: SnackBarManager = SnackBarManager,
  resources: Resources = resources(),
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
) =
  remember(
    snackbarHostState,
    navController,
    snackbarManager,
    resources,
    coroutineScope,
  ) {
    TrackAppState(
      snackbarHostState,
      navController,
      snackbarManager,
      resources,
      coroutineScope,
    )
  }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
  LocalConfiguration.current
  return LocalContext.current.resources
}

fun NavGraphBuilder.trackGraph(
  appState: TrackAppState,
  navigationType: NavigationType,
  contentType: TrackContentType
) {
  composable(SPLASH_SCREEN) {
    SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
  }

  composable(SETTINGS_SCREEN) {
    SettingsScreen(
      restartApp = { route -> appState.clearAndNavigate(route) },
      openScreen = { route -> appState.navigate(route) }
    )
  }

  composable(LOGIN_SCREEN) {
    LoginScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
  }

  composable(SIGN_UP_SCREEN) {
    SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
  }

  composable(TRAININGS_SCREEN) {
    TrainingsScreen(
      navigationType = navigationType,
      openScreen = { route -> appState.navigate(route) },
      onTrainingPressed = { training -> appState.navigate("$TRAINING_SCREEN?$TRAINING_ID=${training.id}") },
      contentType = contentType
    )
  }

  composable(AGENDA_SCREEN) {
    AgendaScreen(
      openScreen = { route -> appState.navigate(route) },
      onTrainingPressed = { training -> appState.navigate("$TRAINING_SCREEN?$TRAINING_ID=${training.id}") },
      navigationType = navigationType,
      contentType = contentType
    )
  }

  composable(SEARCH_SCREEN) {
    SearchScreen(
      popUpScreen = { appState.popUp() },
      onTrainingPressed = { training -> appState.navigate("$TRAINING_SCREEN?$TRAINING_ID=${training.id}") }
    )
  }

  composable(PROFILE_SCREEN) {
    ProfileScreen(
      openScreen = { route -> appState.navigate(route) },
      contentType = contentType
    )
  }

  composable(
    route = "$EDIT_TRAINING_SCREEN?$TRAINING_ID_ARG&$EDIT_MODE_ARG",
    arguments = listOf(
      navArgument(TRAINING_ID) { defaultValue = TRAINING_DEFAULT_ID },
      navArgument(EDIT_MODE) { defaultValue = EDIT_MODE_DEFAULT }
    )
  ) {
    EditTrainingScreen(
      openScreen = { route -> appState.navigate(route) },
      popUpScreen = { appState.popUp() },
    )
  }

  composable(
    route = "$EDIT_REPETITIONS_SCREEN?$TRAINING_ID_ARG",
    arguments = listOf(navArgument(TRAINING_ID) { defaultValue = TRAINING_DEFAULT_ID })
  ){ backStackEntry ->
    val editTrainingEntry = remember(backStackEntry) {
      appState.getBackStackEntry(EDIT_TRAINING_SCREEN)
    }
    val editTrainingViewModel = hiltViewModel<EditTrainingViewModel>(editTrainingEntry)
    EditRepetitionsScreen(
      popUpScreen = { appState.popUp() },
      viewModel = editTrainingViewModel
    )
  }

  composable(
    route = "$FILL_REPETITIONS_SCREEN?$TRAINING_ID_ARG",
    arguments = listOf(navArgument(TRAINING_ID) { defaultValue = TRAINING_DEFAULT_ID })
  ) {
    FillRepetitionsScreen(
      popUpScreen = { appState.popUp() }
    )
  }

  composable(
    route = "$TRAINING_SCREEN?$TRAINING_ID_ARG",
    arguments = listOf(navArgument(TRAINING_ID) { defaultValue = TRAINING_DEFAULT_ID }),
    deepLinks = listOf(navDeepLink { uriPattern = "https://track.com/training/{trainingId}" })
  ) {
    TrainingScreen(
      popUpScreen = { appState.popUpOrNavigate(TRAININGS_SCREEN) },
      openScreen = { route -> appState.navigate(route) },
      trainingId = it.arguments?.getString(TRAINING_ID) ?: TRAINING_DEFAULT_ID,
      onEditPressed = { trainingId, editMode -> appState.navigate("$EDIT_TRAINING_SCREEN?$TRAINING_ID=${trainingId}&$EDIT_MODE=${editMode}") }
    )
  }

  composable(
    route = FITBIT_SCREEN,
    deepLinks = listOf(navDeepLink { uriPattern = "https://track.com/fitbit?code={authorizationCode}#_=_" })) {
      FitbitScreen(
        fitbitAuthorizationCode = it.arguments!!.getString("authorizationCode")!!,
        openScreen = { route -> appState.clearAndNavigate(route) }
      )
  }
}