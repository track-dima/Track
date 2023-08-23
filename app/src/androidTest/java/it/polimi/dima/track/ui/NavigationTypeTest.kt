package it.polimi.dima.track.ui

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.onNodeWithTagForStringId
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

  private val compactWindowSize = DpSize(width = 400.dp, height = 600.dp)
  private val mediumWindowSize = DpSize(width = 700.dp, height = 600.dp)
  private val expandedWindowSize = DpSize(width = 900.dp, height = 600.dp)

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
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