package it.polimi.dima.track

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.polimi.dima.track.common.snackbar.SnackBarManager
import it.polimi.dima.track.common.utils.*
import it.polimi.dima.track.navigation.*
import it.polimi.dima.track.screens.agenda.AgendaScreen
import it.polimi.dima.track.screens.edit_training.EditTrainingScreen
import it.polimi.dima.track.screens.trainings.TrainingsScreen
import it.polimi.dima.track.screens.login.LoginScreen
import it.polimi.dima.track.screens.settings.SettingsScreen
import it.polimi.dima.track.screens.signup.SignUpScreen
import it.polimi.dima.track.screens.splash.SplashScreen
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackApp(
    windowSize: WindowSizeClass,
    viewModel: TrackAppViewModel = hiltViewModel()
) {
    viewModel.calculateNavigationType(windowSize)
    val uiState by viewModel.uiState
    val appState = rememberAppState()
    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: AGENDA_SCREEN

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
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
            if (uiState.navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
                PermanentNavigationDrawer(drawerContent = {
                    PermanentNavigationDrawerContent(
                        selectedDestination = selectedDestination,
                        navigationContentPosition = uiState.navigationContentPosition,
                        navigateToTopLevelDestination = appState::drawerNavigate,
                        openScreen = { route -> appState.navigate(route) }
                    )
                }) {
                    TrackAppContent(
                        appState = appState,
                        navigationType = uiState.navigationType,
                        navigationContentPosition = uiState.navigationContentPosition,
                        innerPadding = innerPaddingModifier,
                        selectedDestination = selectedDestination,
                        navigateToTopLevelDestination = appState::drawerNavigate
                    )
                }
            } else {
                TrackAppContent(
                    appState = appState,
                    navigationType = uiState.navigationType,
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
    selectedDestination: String,
    innerPadding: PaddingValues,
    navigationContentPosition: NavigationContentPosition,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == NavigationType.NAVIGATION_RAIL) {
            TrackNavigationRail(
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
                startDestination = AGENDA_SCREEN,
                modifier = Modifier.weight(1f)
            ) {
                trackGraph(appState, navigationType)
            }
            AnimatedVisibility(visible = navigationType == NavigationType.BOTTOM_NAVIGATION) {
                TrackBottomNavigationBar(
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

fun NavGraphBuilder.trackGraph(appState: TrackAppState, navigationType: NavigationType) {
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
            openScreen = { route -> appState.navigate(route) },
            navigationType = navigationType
        )
    }

    composable(AGENDA_SCREEN) {
        AgendaScreen(
            openScreen = { route -> appState.navigate(route) },
            navigationType = navigationType
        )
    }
    composable(PROFILE_SCREEN) {
        // ProfileScreen(openScreen = { route -> appState.navigate(route) })
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