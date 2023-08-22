package it.polimi.dima.track

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.common.utils.NavigationContentPosition
import it.polimi.dima.track.common.utils.NavigationType
import it.polimi.dima.track.common.utils.TrackContentType
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.screens.TrackViewModel
import it.polimi.dima.track.screens.signup.SignUpUiState
import javax.inject.Inject

@HiltViewModel
class TrackAppViewModel @Inject constructor(
  logService: LogService
) : TrackViewModel(logService) {
  var uiState = mutableStateOf(TrackAppUiState())
    private set

  fun calculateNavigationType(windowSize: WindowSizeClass) {
    uiState.value = uiState.value.copy(
      navigationType = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
          NavigationType.BOTTOM_NAVIGATION
        }

        WindowWidthSizeClass.Medium -> {
          NavigationType.NAVIGATION_RAIL
        }

        WindowWidthSizeClass.Expanded -> {
          NavigationType.PERMANENT_NAVIGATION_DRAWER
        }

        else -> {
          NavigationType.BOTTOM_NAVIGATION
        }
      },
      navigationContentPosition = when (windowSize.heightSizeClass) {
        WindowHeightSizeClass.Compact -> {
          NavigationContentPosition.TOP
        }

        WindowHeightSizeClass.Medium,
        WindowHeightSizeClass.Expanded -> {
          NavigationContentPosition.CENTER
        }

        else -> {
          NavigationContentPosition.TOP
        }
      }
    )
  }
}
