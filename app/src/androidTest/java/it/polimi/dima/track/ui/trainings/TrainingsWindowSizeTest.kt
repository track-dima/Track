package it.polimi.dima.track.ui.trainings

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import it.polimi.dima.track.HiltTestActivity
import it.polimi.dima.track.R
import it.polimi.dima.track.TestCompactWidth
import it.polimi.dima.track.TestExpandedTabletWidth
import it.polimi.dima.track.TestExpandedWidth
import it.polimi.dima.track.TestMediumWidth
import it.polimi.dima.track.TrackApp
import it.polimi.dima.track.data.mockedTrainings
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
class TrainingsWindowSizeTest : InjectingTestCase() {

  /**
   * Note: To access to an empty activity, the code uses a custom activity [HiltTestActivity]
   * annotated with @AndroidEntryPoint.
   */
  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestCompactWidth
  fun compactDevice_trainingsScreen() {
    // Setup compact window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = compactWindowSize),
        width = compactWindowSize.width
      )
    }

    // The two trainings that are shown in trainings screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed()
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertExists()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].title
    ).assertExists()

    // Training is not shown in compact mode
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertDoesNotExist()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].notes
    ).assertDoesNotExist()

    // The add Fab is shown
    composeTestRule.onNodeWithTagForStringId(
      R.string.add_training_trainings_fab_tag
    ).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestMediumWidth
  fun mediumDevice_trainingsScreen() {
    // Setup medium window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = mediumWindowSize),
        width = mediumWindowSize.width
      )
    }

    // The two trainings that are shown in trainings screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed()
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertExists()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].title
    ).assertExists()

    // Training is not shown in medium mode
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertDoesNotExist()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].notes
    ).assertDoesNotExist()

    // The add Fab is not shown
    composeTestRule.onNodeWithTagForStringId(
      R.string.add_training_trainings_fab_tag
    ).assertDoesNotExist()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestExpandedWidth
  fun expandedDevice_trainingsScreen() {
    // Setup expanded window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = expandedWindowSize),
        width = expandedWindowSize.width
      )
    }

    // The two trainings that are shown in trainings screen
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed()
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertExists()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].title
    ).assertExists()

    // Training is not shown in expanded mode (not tablet)
    composeTestRule.onNodeWithText(
      mockedTrainings[0].notes
    ).assertDoesNotExist()
    composeTestRule.onNodeWithText(
      mockedTrainings[1].notes
    ).assertDoesNotExist()

    // The add Fab is not shown
    composeTestRule.onNodeWithTagForStringId(
      R.string.add_training_trainings_fab_tag
    ).assertDoesNotExist()
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Test
  @TestExpandedTabletWidth
  fun expandedTabletDevice_trainingsScreen() {
    // Setup expanded tablet window
    composeTestRule.setContent {
      TrackApp(
        windowSize = WindowSizeClass.calculateFromSize(size = expandedTabletWindowSize),
        width = expandedTabletWindowSize.width
      )
    }

    // The two trainings that are shown in trainings screen (plus the one in the details screen)
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.trainings
    ).assertIsDisplayed()
    composeTestRule.onNodeWithText(
      mockedTrainings[0].title
    ).assertExists()
    composeTestRule.onAllNodesWithText(
      mockedTrainings[1].title
    ).assertCountEquals(2)

    // Training (lastTraining) is shown in tablet expanded mode
    composeTestRule.onNodeWithText(
      mockedTrainings[1].notes
    ).assertExists()

    // In this case the close button is not shown
    composeTestRule.onNodeWithContentDescriptionForStringId(
      R.string.close
    ).assertDoesNotExist()

    // The add Fab is not shown
    composeTestRule.onNodeWithTagForStringId(
      R.string.add_training_trainings_fab_tag
    ).assertDoesNotExist()
  }
}