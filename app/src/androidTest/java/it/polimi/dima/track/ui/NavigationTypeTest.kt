package it.polimi.dima.track.ui

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
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
import it.polimi.dima.track.utlis.onAllNodesWithStringId
import it.polimi.dima.track.utlis.onNodeWithContentDescriptionForStringId
import it.polimi.dima.track.utlis.onNodeWithStringId
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

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun bottomNavigation_openAgenda() {
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

    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.agenda
    ).assertIsDisplayed().performClick()

    // Verify that the agenda screen is displayed
    composeTestRule.onNodeWithStringId(
      R.string.agenda
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun bottomNavigation_openProfile() {
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

    // Open profile screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.profile
    ).assertIsDisplayed().performClick()

    // Verify that the profile screen is displayed
    composeTestRule.onNodeWithStringId(
      R.string.profile
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun bottomNavigation_openTrainings() {
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

    // Open trainings screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed().performClick()

    // Verify that the trainings screen is displayed
    composeTestRule.onNodeWithStringId(
      R.string.trainings
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun bottomNavigation_noAddTrainingFab() {
    // Set up compact window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    // Bottom navigation is displayed and does not contains add training fab
    composeTestRule.onNode(
      hasContentDescription(composeTestRule.activity.getString(R.string.add_training))
        .and(
          hasAnyAncestor(hasTestTag(composeTestRule.activity.getString(R.string.navigation_bottom)))
        )
    ).assertDoesNotExist()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun navigationRail_openAgenda() {
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

    // Open agenda screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.agenda
    ).assertIsDisplayed().performClick()

    // Verify that the agenda screen is displayed
    composeTestRule.onNodeWithStringId(
      R.string.agenda
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun navigationRail_openProfile() {
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

    // Open profile screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.profile
    ).assertIsDisplayed().performClick()

    // Verify that the profile screen is displayed
    composeTestRule.onNodeWithStringId(
      R.string.profile
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun navigationRail_openTrainings() {
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

    // Open trainings screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed().performClick()

    // Verify that the trainings screen is displayed
    composeTestRule.onNodeWithStringId(
      R.string.trainings
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun navigationRail_addTrainingFab() {
    // Set up medium window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = mediumWindowSize),
        width = mediumWindowSize.width
      )
    }

    // Navigation rail is displayed and contains add training fab
    composeTestRule.onNode(
      hasContentDescription(composeTestRule.activity.getString(R.string.add_training))
        .and(
          hasAnyAncestor(hasTestTag(composeTestRule.activity.getString(R.string.navigation_rail)))
        )
    ).assertExists().performClick()

    // Verify that the add training screen is displayed
    composeTestRule.onNodeWithStringId(
      R.string.new_training
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun navigationDrawer_openAgenda() {
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

    // Open agenda screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.agenda
    ).assertIsDisplayed().performClick()

    // Verify that the agenda screen is displayed (agenda in title and nav drawer)
    composeTestRule.onAllNodesWithStringId(
      R.string.agenda
    ).assertCountEquals(2)
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun navigationDrawer_openProfile() {
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

    // Open profile screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.profile
    ).assertIsDisplayed().performClick()

    // Verify that the profile screen is displayed (profile in title and nav drawer)
    composeTestRule.onAllNodesWithStringId(
      R.string.profile
    ).assertCountEquals(2)
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun navigationDrawer_openTrainings() {
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

    // Open trainings screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed().performClick()

    // Verify that the trainings screen is displayed (trainings in title and nav drawer)
    composeTestRule.onAllNodesWithStringId(
      R.string.trainings
    ).assertCountEquals(2)
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  fun navigationDrawer_addTrainingFab() {
    // Set up expanded window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = expandedWindowSize),
        width = expandedWindowSize.width
      )
    }

    // Navigation drawer is displayed and contains add training fab
    composeTestRule.onNode(
      hasText(composeTestRule.activity.getString(R.string.add_training))
        .and(
          hasAnyAncestor(hasTestTag(composeTestRule.activity.getString(R.string.navigation_drawer)))
        )
    ).assertExists().performClick()

    // Verify that the add training screen is displayed
    composeTestRule.onNodeWithStringId(
      R.string.new_training
    ).assertIsDisplayed()
  }
}