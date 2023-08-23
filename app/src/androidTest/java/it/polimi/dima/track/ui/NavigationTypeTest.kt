package it.polimi.dima.track.ui

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.TestCompactWidth
import it.polimi.dima.track.TestExpandedWidth
import it.polimi.dima.track.TestMediumWidth
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.utlis.compactWindowSize
import it.polimi.dima.track.utlis.expandedWindowSize
import it.polimi.dima.track.utlis.mediumWindowSize
import it.polimi.dima.track.utlis.onNodeWithTagForStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class NavigationTypeTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestCompactWidth
  fun compactDevice_verifyUsingBottomNavigation() {
    // Set up compact window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }
    // Bottom navigation is displayed
    composeTestRule.onNodeWithTagForStringId(
      R.string.navigation_bottom
    ).assertExists()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestMediumWidth
  fun mediumDevice_verifyUsingNavigationRail() {
    // Set up medium window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = mediumWindowSize),
        width = mediumWindowSize.width
      )
    }
    // Navigation rail is displayed
    composeTestRule.onNodeWithTagForStringId(
      R.string.navigation_rail
    ).assertExists()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestExpandedWidth
  fun expandedDevice_verifyUsingNavigationDrawer() {
    // Set up expanded window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = expandedWindowSize),
        width = expandedWindowSize.width
      )
    }
    // Navigation drawer is displayed
    composeTestRule.onNodeWithTagForStringId(
      R.string.navigation_drawer
    ).assertExists()
  }
}