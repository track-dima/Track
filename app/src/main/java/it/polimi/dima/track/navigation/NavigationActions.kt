package it.polimi.dima.track.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.ui.graphics.vector.ImageVector
import it.polimi.dima.track.AGENDA_SCREEN
import it.polimi.dima.track.PROFILE_SCREEN
import it.polimi.dima.track.R
import it.polimi.dima.track.TRAININGS_SCREEN

data class TopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(
        route = AGENDA_SCREEN,
        selectedIcon = Icons.Filled.ViewAgenda,
        unselectedIcon = Icons.Outlined.ViewAgenda,
        iconTextId = R.string.agenda
    ),
    TopLevelDestination(
        route = TRAININGS_SCREEN,
        selectedIcon = Icons.Filled.DirectionsRun,
        unselectedIcon = Icons.Outlined.DirectionsRun,
        iconTextId = R.string.trainings
    ),
    TopLevelDestination(
        route = PROFILE_SCREEN,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        iconTextId = R.string.profile
    )

)
