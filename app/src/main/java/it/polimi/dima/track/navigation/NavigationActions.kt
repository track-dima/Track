package it.polimi.dima.track.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material.icons.rounded.ViewAgenda
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
    selectedIcon = Icons.Rounded.ViewAgenda,
    unselectedIcon = Icons.Outlined.ViewAgenda,
    iconTextId = R.string.agenda
  ),
  TopLevelDestination(
    route = TRAININGS_SCREEN,
    selectedIcon = Icons.Rounded.DirectionsRun,
    unselectedIcon = Icons.Rounded.DirectionsRun,
    iconTextId = R.string.trainings
  ),
  TopLevelDestination(
    route = PROFILE_SCREEN,
    selectedIcon = Icons.Rounded.Person,
    unselectedIcon = Icons.Rounded.PersonOutline,
    iconTextId = R.string.profile
  )

)
