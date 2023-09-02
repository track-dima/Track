package it.polimi.dima.track.ui.profile

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.TestCompactWidth
import it.polimi.dima.track.TestExpandedTabletWidth
import it.polimi.dima.track.TestExpandedWidth
import it.polimi.dima.track.TestMediumWidth
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.injection.InjectingTestCase
import it.polimi.dima.track.utils.compactWindowSize
import it.polimi.dima.track.utils.expandedTabletWindowSize
import it.polimi.dima.track.utils.expandedWindowSize
import it.polimi.dima.track.utils.mediumWindowSize
import it.polimi.dima.track.utils.onNodeWithContentDescriptionForStringId
import it.polimi.dima.track.utils.onNodeWithTagForStringId
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ProfileWindowSizeTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestCompactWidth
  fun compactDevice_profileScreen() {
    // Setup compact window
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

    // Cards are placed just in 1 column
    val lazyGridWidth = composeTestRule.onNodeWithTagForStringId(R.string.lazy_grid_profile_tag).fetchSemanticsNode().size.width

    composeTestRule.onNodeWithTagForStringId(R.string.lazy_grid_profile_tag).onChildren()
      .assertCountEquals(2).fetchSemanticsNodes().forEach {
        assert(it.size.width < lazyGridWidth)
        assert(it.size.width > lazyGridWidth / 2)
      }
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestMediumWidth
  fun mediumDevice_profileScreen() {
    // Setup medium window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = mediumWindowSize),
        width = mediumWindowSize.width
      )
    }

    // Open profile screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.profile
    ).assertIsDisplayed().performClick()

    // Cards are placed just in 1 column
    val lazyGridWidth = composeTestRule.onNodeWithTagForStringId(R.string.lazy_grid_profile_tag).fetchSemanticsNode().size.width

    composeTestRule.onNodeWithTagForStringId(R.string.lazy_grid_profile_tag).onChildren()
      .assertCountEquals(2).fetchSemanticsNodes().forEach {
        assert(it.size.width < lazyGridWidth)
        assert(it.size.width > lazyGridWidth / 2)
      }
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestExpandedWidth
  fun expandedDevice_profileScreen() {
    // Setup expanded window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = expandedWindowSize),
        width = expandedWindowSize.width
      )
    }

    // Open profile screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.profile
    ).assertIsDisplayed().performClick()

    // Cards are placed just in 1 column
    val lazyGridWidth = composeTestRule.onNodeWithTagForStringId(R.string.lazy_grid_profile_tag).fetchSemanticsNode().size.width

    composeTestRule.onNodeWithTagForStringId(R.string.lazy_grid_profile_tag).onChildren()
      .assertCountEquals(2).fetchSemanticsNodes().forEach {
        assert(it.size.width < lazyGridWidth)
        assert(it.size.width > lazyGridWidth / 2)
      }
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestExpandedTabletWidth
  fun expandedTabletDevice_profileScreen() {
    // Setup expanded tablet window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = expandedTabletWindowSize),
        width = expandedTabletWindowSize.width
      )
    }

    // Open profile screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.profile
    ).assertIsDisplayed().performClick()

    // Cards are placed in 2 columns
    val lazyGridWidth = composeTestRule.onNodeWithTagForStringId(R.string.lazy_grid_profile_tag).fetchSemanticsNode().size.width

    composeTestRule.onNodeWithTagForStringId(R.string.lazy_grid_profile_tag).onChildren()
      .assertCountEquals(2).fetchSemanticsNodes().forEach {
        assert(it.size.width < lazyGridWidth / 2)
      }
  }
}