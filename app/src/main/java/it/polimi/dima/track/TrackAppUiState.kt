package it.polimi.dima.track

import it.polimi.dima.track.common.utils.NavigationContentPosition
import it.polimi.dima.track.common.utils.NavigationType
import it.polimi.dima.track.common.utils.TrackContentType

data class TrackAppUiState(
  val navigationType: NavigationType = NavigationType.BOTTOM_NAVIGATION,
  val contentType: TrackContentType = TrackContentType.LIST_ONLY,
  val navigationContentPosition: NavigationContentPosition = NavigationContentPosition.TOP
)
