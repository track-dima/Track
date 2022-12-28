package it.polimi.dima.track

import android.content.res.Resources
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.polimi.dima.track.common.snackbar.SnackBarManager
import it.polimi.dima.track.screens.edit_training.EditTrainingScreen
import it.polimi.dima.track.screens.trainings.TrainingsScreen
import it.polimi.dima.track.screens.login.LoginScreen
import it.polimi.dima.track.screens.settings.SettingsScreen
import it.polimi.dima.track.screens.signup.SignUpScreen
import it.polimi.dima.track.screens.splash.SplashScreen
import it.polimi.dima.track.ui.theme.TrackTheme
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackApp() {
    TrackTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = appState.snackbarHostState,
                        modifier = Modifier.padding(8.dp),
                        snackbar = { snackBarData ->
                            Snackbar(snackBarData, contentColor = MaterialTheme.colorScheme.onPrimary)
                        }
                    )
                }
                //scaffoldState = appState.scaffoldState
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    trackGraph(appState)
                }
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
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(snackbarHostState, navController, snackbarManager, resources, coroutineScope) {
        TrackAppState(snackbarHostState, navController, snackbarManager, resources, coroutineScope)
    }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

fun NavGraphBuilder.trackGraph(appState: TrackAppState) {
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
        TrainingsScreen(openScreen = { route -> appState.navigate(route) })
    }

    composable(
        route = "$EDIT_TRAINING_SCREEN$TRAINING_ID_ARG",
        arguments = listOf(navArgument(TRAINING_ID) { defaultValue = TRAINING_DEFAULT_ID })
    ) {
        EditTrainingScreen(
            popUpScreen = { appState.popUp() },
            trainingId = it.arguments?.getString(TRAINING_ID) ?: TRAINING_DEFAULT_ID
        )
    }
}