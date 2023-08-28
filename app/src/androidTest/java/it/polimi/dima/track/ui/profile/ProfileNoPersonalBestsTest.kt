package it.polimi.dima.track.ui.profile

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.utlis.compactWindowSize
import it.polimi.dima.track.utlis.onAllNodesWithStringId
import it.polimi.dima.track.utlis.onNodeWithContentDescriptionForStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ProfileNoPersonalBestsTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun profileScreen_noPersonalBests() {
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    // Open profile screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.profile
    ).assertIsDisplayed().performClick()

    // The no training steps string are shown
    composeTestRule.onAllNodesWithStringId(
      R.string.no_personal_bests_yet
    ).assertCountEquals(2)
  }
}