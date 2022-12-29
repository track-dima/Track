package it.polimi.dima.track

import it.polimi.dima.track.common.utils.NavigationContentPosition
import it.polimi.dima.track.common.utils.NavigationType

data class TrackAppUiState(
  val navigationType: NavigationType = NavigationType.BOTTOM_NAVIGATION,
  val navigationContentPosition: NavigationContentPosition = NavigationContentPosition.TOP
)
